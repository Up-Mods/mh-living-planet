package dev.upcraft.livingplanet.naturaldisasters;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.upcraft.livingplanet.LPOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Earthquake implements NaturalDisaster {
    public static final NaturalDisasterType<Earthquake> TYPE = new NaturalDisasterType<>(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("id").forGetter(NaturalDisaster::id),
                    BlockPos.CODEC.fieldOf("centre").forGetter(n -> n.centre),
                    Codec.INT.fieldOf("ticks_existed").forGetter(n -> n.ticksExisted),
                    Hole.CODEC.listOf().fieldOf("holes").forGetter(n -> n.holes)
            ).apply(instance, Earthquake::new)),
            Earthquake::new,
            Earthquake::spawn);

    private static Earthquake spawn(ServerLevel level, List<ServerPlayer> players) {
        var targetPlayer = players.get(level.random.nextInt(players.size()));
        int x = targetPlayer.getBlockX() + level.random.nextIntBetweenInclusive(-30, 30);
        int z = targetPlayer.getBlockZ() + level.random.nextIntBetweenInclusive(-30, 30);
        int y;
        if (level.dimensionType().hasCeiling()) {
            y = targetPlayer.getBlockY();
        } else {
            y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        }

        return new Earthquake(new BlockPos(x, y, z), level.random);
    }

    private final UUID id;
    private final BlockPos centre;
    private final List<Hole> holes = new ArrayList<>();
    private int ticksExisted;

    public Earthquake(BlockPos centre, RandomSource random) {
        this.id = UUID.randomUUID();
        this.centre = centre;
        int holeCount = random.nextIntBetweenInclusive(LPOptions.EARTHQUAKE_MIN_HOLE_COUNT.getValue(), LPOptions.EARTHQUAKE_MAX_HOLE_COUNT.getValue());
        for (int i = 0; i < holeCount; i++) {
            this.holes.add(new Hole(random.nextIntBetweenInclusive(-50, 50),
                    random.nextIntBetweenInclusive(-50, 50),
                    random.nextIntBetweenInclusive(0, 60),
                    random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z));
        }
    }

    public Earthquake(UUID id, BlockPos centre, int ticksExisted, List<Hole> holes) {
        this.id = id;
        this.centre = centre;
        this.ticksExisted = ticksExisted;
        this.holes.addAll(holes);
    }

    public Earthquake(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readBlockPos(), buf.readInt(), buf.readList(Hole::new));
    }

    public double getStrengthAt(Vec3 cameraPos) {
        return 1 - Mth.clamp(Vec3.atCenterOf(this.centre).distanceTo(cameraPos) - 75, 0, 3)/3;
    }

    @Override
    public boolean serverTick(ServerLevel level) {
        if (this.ticksExisted++ > 100) {
        return true;
        }

        var pos = new BlockPos.MutableBlockPos();
        for (var hole : this.holes) {
            int ticks = this.ticksExisted - hole.timeOffset;
            double progress = ticks/100.0;
            int xVariance = (int) (hole.axis() == Direction.Axis.X ? 10*progress : 4*progress);
            int zVariance = (int) (hole.axis() == Direction.Axis.Z ? 10*progress : 4*progress);
            for (int i = 0; i < progress*10; i++) {
                int x = (int) (hole.xOffset + this.centre.getX() + level.random.nextGaussian() * xVariance);
                int z = (int) (hole.zOffset + this.centre.getZ() + level.random.nextGaussian() * zVariance);
                int y = this.getY(level, pos.set(x, 0, z));
                if (y < level.getMinBuildHeight()) {
                    continue;
                }
                for (pos.set(x, y, z); pos.getY() > y - 10; pos.move(0, -1, 0)) {
                    level.removeBlock(pos, false);
                }
                if (pos.getY() <= 60) {
                    level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                }
            }
        }

        return false;
    }

    private int getY(ServerLevel level, BlockPos.MutableBlockPos pos) {
        if (level.dimensionType().hasCeiling()) {
            for (pos.setY(this.centre.getY() + 20);
                 pos.getY() > this.centre.getY() - 20;
                 pos.move(0, -1, 0)) {
                if (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(level.getBlockState(pos))) {
                    return pos.getY();
                }
            }

            return Integer.MIN_VALUE;
        }

        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
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
        buf.writeBlockPos(this.centre);
        buf.writeInt(this.ticksExisted);
        buf.writeCollection(this.holes, Hole::toBuffer);
    }

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public NaturalDisasterType<?> type() {
        return TYPE;
    }

    public record Hole(int xOffset, int zOffset, int timeOffset, Direction.Axis axis) {
        public static final Codec<Hole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("x_offset").forGetter(Hole::xOffset),
                Codec.INT.fieldOf("z_offset").forGetter(Hole::zOffset),
                Codec.INT.fieldOf("time_offset").forGetter(Hole::timeOffset),
                Direction.Axis.CODEC.fieldOf("axis").forGetter(Hole::axis)
        ).apply(instance, Hole::new));

        Hole(FriendlyByteBuf buf) {
            this(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean() ? Direction.Axis.X : Direction.Axis.Z);
        }

        public static void toBuffer(FriendlyByteBuf buf, Hole hole) {
            buf.writeVarInt(hole.xOffset());
            buf.writeVarInt(hole.zOffset());
            buf.writeVarInt(hole.timeOffset());
            buf.writeBoolean(hole.axis() == Direction.Axis.X);
        }
    }
}
