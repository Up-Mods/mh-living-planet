package dev.upcraft.livingplanet.util;

import net.minecraft.world.food.FoodData;

public class DummyFoodData extends FoodData {

    public static final DummyFoodData INSTANCE = new DummyFoodData();

    @Override
    public int getFoodLevel() {
        return 20;
    }

    @Override
    public int getLastFoodLevel() {
        return 20;
    }

    @Override
    public boolean needsFood() {
        return false;
    }

    @Override
    public float getExhaustionLevel() {
        return 0.0F;
    }

    @Override
    public float getSaturationLevel() {
        return 5.0F;
    }
}
