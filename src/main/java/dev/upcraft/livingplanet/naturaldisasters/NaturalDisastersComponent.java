package dev.upcraft.livingplanet.naturaldisasters;

import com.mojang.serialization.Codec;
import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Unmodifiable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.sync.ComponentPacketWriter;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class NaturalDisastersComponent implements ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    public static final Codec<List<NaturalDisaster>> DISASTER_LIST_CODEC = NaturalDisasters.DISASTER_CODEC.listOf();

    private final Map<NaturalDisasterType<? extends NaturalDisaster>, List<NaturalDisaster>> disastersByType = new HashMap<>();
    private final List<NaturalDisaster> disasters = new ArrayList<>();
    private final Level level;

    public NaturalDisastersComponent(Level level) {
        this.level = level;
    }

    @Override
    public void clientTick() {
        for (var disaster : this.disasters) {
            disaster.clientTick(this.level);
        }
    }

    @Override
    public void serverTick() {
        List<UUID> removed = new ArrayList<>();
        for (var iter = this.disasters.iterator(); iter.hasNext();) {
            var disaster = iter.next();
            if (disaster.serverTick((ServerLevel) this.level)) {
                iter.remove();
                removed.add(disaster.id());
                var listOfType = this.disastersByType.get(disaster.type());
                if (listOfType != null) {
                    listOfType.remove(disaster);
                }
            }
        }

        if (!removed.isEmpty()) {
            LPComponents.NATURAL_DISASTERS.sync(this.level, this.makeRemovalSyncPacketWriter(removed));
        }
        LPComponents.NATURAL_DISASTERS.sync(this.level, (buf, recipient) -> buf.writeEnum(SyncOp.TICK));
    }

    public void addDisaster(NaturalDisaster disaster) {
        this.disasters.add(disaster);
        this.disastersByType.computeIfAbsent(disaster.type(), $ -> new ArrayList<>()).add(disaster);
        LPComponents.NATURAL_DISASTERS.sync(this.level, this.makeAdditionSyncPacketWriter(disaster));
    }

    public boolean hasDisaster(Class<? extends NaturalDisaster> clazz) {
        return this.disasters.stream().anyMatch(clazz::isInstance);
    }

    public <T extends NaturalDisaster> double getDisasterStrength(NaturalDisasterType<T> type, ToDoubleFunction<T> strength) {
        return this.getDisasters(type)
                .mapToDouble(strength)
                .max()
                .orElse(0);
    }

    public <T extends NaturalDisaster> Optional<T> getDisaster(UUID id, NaturalDisasterType<T> type) {
        return this.getDisasters(type)
                .filter(t -> t.id().equals(id))
                .findAny();
    }

    public <T extends NaturalDisaster> Stream<T> getDisasters(NaturalDisasterType<T> type) {
        return this.getDisastersList(type).stream();
    }

    @SuppressWarnings("unchecked")
    public <T extends NaturalDisaster> @Unmodifiable List<T> getDisastersList(NaturalDisasterType<T> type) {
        return (List<T>) List.copyOf(this.disastersByType.getOrDefault(type, List.of()));
    }

    private ComponentPacketWriter makeAdditionSyncPacketWriter(NaturalDisaster disaster) {
        return (buf, recipient) -> {
            buf.writeEnum(SyncOp.ADD);
            writeDisaster(buf, disaster);
        };
    }

    private ComponentPacketWriter makeRemovalSyncPacketWriter(List<UUID> removed) {
        return (buf, recipient) -> {
            buf.writeEnum(SyncOp.REMOVE);
            buf.writeCollection(removed, (b, u) -> b.writeUUID(u));
        };
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeEnum(SyncOp.FULL);
        buf.writeCollection(this.disasters, NaturalDisastersComponent::writeDisaster);
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        switch (buf.readEnum(SyncOp.class)) {
            case FULL -> {
                this.disasters.clear();
                List<NaturalDisaster> toAdd = buf.readList(NaturalDisastersComponent::readDisaster);
                this.disasters.addAll(toAdd);
                for (var disaster : toAdd) {
                    this.disastersByType.computeIfAbsent(disaster.type(), $ -> new ArrayList<>()).add(disaster);
                }
            }
            case ADD -> {
                NaturalDisaster toAdd = readDisaster(buf);
                this.disasters.add(toAdd);
                this.disastersByType.computeIfAbsent(toAdd.type(), $ -> new ArrayList<>()).add(toAdd);
            }
            case REMOVE -> {
                var badUuids = buf.readList(b -> b.readUUID());
                this.disasters.removeIf(d -> badUuids.contains(d.id()));
                for (var list : this.disastersByType.values()) {
                    list.removeIf(d -> badUuids.contains(d.id()));
                }
            }
            case TICK -> {
                for (var disaster : this.disasters) {
                    disaster.clientSyncTick(this.level);
                }
            }
        }
    }

    private static void writeDisaster(FriendlyByteBuf b, NaturalDisaster d) {
        b.writeVarInt(NaturalDisasters.NATURAL_DISASTER_TYPES.getId(d.type()));
        d.toBuffer(b);
    }

    private static NaturalDisaster readDisaster(FriendlyByteBuf buf) {
        return NaturalDisasters.NATURAL_DISASTER_TYPES.getHolder(buf.readVarInt())
                .orElseThrow(() -> new NoSuchElementException("Invalid disaster type id sent"))
                .value()
                .fromPacket()
                .apply(buf);
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        this.disasters.clear();
        this.disastersByType.clear();
        if (tag.contains("disasters")) {
            this.disasters.addAll(DISASTER_LIST_CODEC.decode(NbtOps.INSTANCE, tag.get("disasters"))
                    .getOrThrow(e -> new RuntimeException("error decoding disasters: " + e))
                    .getFirst());
            for (var disaster : this.disasters) {
                this.disastersByType.computeIfAbsent(disaster.type(), $ -> new ArrayList<>()).add(disaster);
            }
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("disasters", DISASTER_LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.disasters)
                .getOrThrow(e -> new RuntimeException("error encoding disasters: "+e)));
    }

    private enum SyncOp {
        FULL,
        ADD,
        REMOVE,
        TICK
    }

    @SuppressWarnings("unchecked")
    private static <T extends NaturalDisaster> Stream<T> filterStream(Stream<? extends NaturalDisaster> stream, NaturalDisasterType<T> type) {
        return stream
                .filter(d -> d.type() == type)
                .map(d -> (T) d);
    }
}
