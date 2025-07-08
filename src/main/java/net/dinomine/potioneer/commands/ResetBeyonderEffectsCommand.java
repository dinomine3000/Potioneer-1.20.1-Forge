package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ResetBeyonderEffectsCommand {

    public ResetBeyonderEffectsCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyondereffects").requires(stack -> {
                            return stack.getPlayer().hasPermissions(2);
                        })
                .then(Commands.literal("clear")
                        .executes(this::cleareffects))
                .then(Commands.literal("print")
                        .executes(this::printeffects))
        );
    }

    private int cleareffects(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            cap.getEffectsManager().clearEffects(cap, player);
        });
        return 1;
    }

    private int printeffects(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            player.sendSystemMessage(Component.literal(cap.getEffectsManager().toString()));
        });
        return 1;
    }

}
