package dev.upcraft.livingplanet.naturaldisasters;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.upcraft.livingplanet.LPOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class LightningStorm implements NaturalDisaster {
    public static final NaturalDisasterType<LightningStorm> TYPE = new NaturalDisasterType<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("id").forGetter(NaturalDisaster::id),
                    Codec.INT.fieldOf("ticks_existed").forGetter(n -> n.ticksExisted)
            ).apply(instance, LightningStorm::new)),
            LightningStorm::new,
            LightningStorm::spawn);

    private static LightningStorm spawn(ServerLevel serverLevel, List<ServerPlayer> serverPlayers) {
        return new LightningStorm();
    }

    private final UUID id;
    private int ticksExisted;

    public LightningStorm() {
        this.id = UUID.randomUUID();
    }

    public LightningStorm(UUID id, int ticksExisted) {
        this.id = id;
        this.ticksExisted = ticksExisted;
    }

    public LightningStorm(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readInt());
    }

    @Override
    public boolean serverTick(ServerLevel level) {
        return this.ticksExisted++ > LPOptions.LIGHTNING_DURATION_SECONDS.getValue()*20;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(Level level) {
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientSyncTick(Level level) {
        this.ticksExisted++;
    }


    @Override
    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeInt(this.ticksExisted);
    }

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public NaturalDisasterType<?> type() {
        return TYPE;
    }
}
