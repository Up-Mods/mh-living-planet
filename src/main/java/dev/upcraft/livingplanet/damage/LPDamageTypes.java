package dev.upcraft.livingplanet.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class LPDamageTypes {
    public static final ResourceKey<DamageType> SHOCKWAVE = ResourceKey.create(Registries.DAMAGE_TYPE, id("shockwave"));
}
