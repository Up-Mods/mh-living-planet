package dev.upcraft.livingplanet.naturaldisasters;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public record NaturalDisasterType<T extends NaturalDisaster>(MapCodec<T> codec, Function<FriendlyByteBuf, T> fromPacket, BiFunction<ServerLevel, List<ServerPlayer>, T> spawnFunc) {
}
