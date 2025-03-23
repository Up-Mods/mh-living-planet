package dev.upcraft.livingplanet.naturaldisasters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface NaturalDisaster {
    boolean serverTick(ServerLevel level);
    @Environment(EnvType.CLIENT)
    void clientTick(Level level);
    @Environment(EnvType.CLIENT)
    void clientSyncTick(Level level);
    void toBuffer(FriendlyByteBuf buf);
    UUID id();
    NaturalDisasterType<?> type();
}
