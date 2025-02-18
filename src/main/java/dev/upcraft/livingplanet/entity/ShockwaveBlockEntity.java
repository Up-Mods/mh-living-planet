package dev.upcraft.livingplanet.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ShockwaveBlockEntity extends FallingBlockEntity {
	public ShockwaveBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
		super(entityType, level);
	}

	private ShockwaveBlockEntity(Level level, double x, double y, double z, BlockState state) {
		super(level, x, y, z, state);
	}

	public static void newShockwave(Level level, BlockPos origin, Vec3 direction, int distance, float yRot) {
		for(int i = 0; i < distance; i++) {
//			Vec3 v = direction.scale(distance);
//			Stream<BlockPos> blockPosStream = BlockPos.betweenClosedStream(origin, origin.offset(new BlockPos((int) v.x, 0, (int) v.z)));

//			blockPosStream.forEach(pos -> {
//				BlockPos blockPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).below();
//				BlockState state = Blocks.STONE.defaultBlockState();
//
//				ShockwaveBlockEntity fallingBlockEntity = new ShockwaveBlockEntity(level, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, false) : state);
//				fallingBlockEntity.disableDrop();
//				fallingBlockEntity.setHurtsEntities(0, 0);
//				fallingBlockEntity.addDeltaMovement(new Vec3(0, 0.25, 0));
//				level.addFreshEntity(fallingBlockEntity);
//			});
		}
	}

	@Override
	public void tick() {
		if(this.onGround()) {
            this.discard();
        }

		super.tick();
	}
}
