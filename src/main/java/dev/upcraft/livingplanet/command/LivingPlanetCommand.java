package dev.upcraft.livingplanet.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.upcraft.livingplanet.component.LPComponents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.*;

public class LivingPlanetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("livingplanet").requires(source -> source.hasPermission(LEVEL_GAMEMASTERS)).then(
                                argument("player", EntityArgument.player()).then(
                                                argument("enabled", BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            var target = EntityArgument.getPlayer(ctx, "player");
                                                            var enabledState = TriState.of(BoolArgumentType.getBool(ctx, "enabled"));
                                                            return execute(ctx, target, enabledState);
                                                        }))
                                        .executes(ctx -> {
                                            var target = EntityArgument.getPlayer(ctx, "player");
                                            return execute(ctx, target, TriState.DEFAULT);
                                        }))
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayerOrException();
                            return execute(ctx, player, TriState.DEFAULT);
                        })
        );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, TriState enabledState) {
        var component = target.getComponent(LPComponents.LIVING_PLANET);
        var newState = enabledState == TriState.DEFAULT ? !component.isLivingPlanet() : enabledState.get();
        component.setLivingPlanet(newState);
        component.setVisible(false);
        if(newState) {
            component.resetHealth();
        }
        component.sync();
        target.refreshDimensions();
        ctx.getSource().sendSuccess(() -> newState ? Component.translatable("commands.livingplanet.state.enabled", target.getFeedbackDisplayName()) : Component.translatable("commands.livingplanet.state.disabled", target.getFeedbackDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }
}
