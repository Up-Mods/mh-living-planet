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

public class PinnedBrightnessBlockAndTintGetter implements BlockAndTintGetter {
    private final BlockAndTintGetter delegate;
    private final BlockPos origin;

    public PinnedBrightnessBlockAndTintGetter(BlockAndTintGetter delegate, BlockPos origin) {
        this.delegate = delegate;
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        if(!blockPos.equals(this.origin)) {
            return BlockAndTintGetter.super.getBrightness(lightType, this.origin);
        }
        return BlockAndTintGetter.super.getBrightness(lightType, blockPos);
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return this.delegate.getShade(direction, shade);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.delegate.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return this.delegate.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.delegate.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return this.delegate.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return this.delegate.getMinBuildHeight();
    }
}
