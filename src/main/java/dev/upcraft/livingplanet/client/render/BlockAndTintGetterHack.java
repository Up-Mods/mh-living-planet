package dev.upcraft.livingplanet.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class BlockAndTintGetterHack implements BlockAndTintGetter {

    private final BlockAndTintGetter delegate;
    private final BlockPos origin;

    public BlockAndTintGetterHack(BlockAndTintGetter delegate, BlockPos origin) {
        this.delegate = delegate;
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        if(!blockPos.equals(origin)) {
            return BlockAndTintGetter.super.getBrightness(lightType, origin);
        }
        return BlockAndTintGetter.super.getBrightness(lightType, blockPos);
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return delegate.getShade(direction, shade);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return delegate.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return delegate.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return delegate.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return delegate.getMinBuildHeight();
    }
}
