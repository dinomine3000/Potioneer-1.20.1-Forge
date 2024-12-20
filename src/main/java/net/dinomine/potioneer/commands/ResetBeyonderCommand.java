package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ResetBeyonderCommand {

    public ResetBeyonderCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonder").then(Commands.literal("reset").executes((command) -> {
            ServerPlayer player = command.getSource().getPlayer();
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(!player.level().isClientSide()) cap.advance(-1, player, true);
            });

            return 1;
        })));
    }
}
