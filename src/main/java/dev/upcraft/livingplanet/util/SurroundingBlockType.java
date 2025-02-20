package dev.upcraft.livingplanet.util;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public interface SurroundingBlockType {
    // represents a known themed set of blocks
    record Preset(BlockState[] includes) implements SurroundingBlockType {
        @Override
        public BlockState get(IntUnaryOperator random) {
            return this.includes[random.applyAsInt(this.includes.length)];
        }
    }
    // represents surroundings which don't fit into a preset (so we'll just use the blockstates we see)
    record Unknown(BlockState block) implements SurroundingBlockType {
        @Override
        public BlockState get(IntUnaryOperator random) {
            return this.block;
        }
    }

    BlockState get(IntUnaryOperator random);
}
