package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class RefreshFormulasCommand {

    public RefreshFormulasCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("formula")
                .then(Commands.literal("refresh")
                        .executes(this::refresh))
        );
    }

    private int refresh(CommandContext<CommandSourceStack> cmd){
        System.out.println("attempting refresh...");
        PotionFormulaSaveData data = PotionFormulaSaveData.from(cmd.getSource().getLevel());
        data.requestRefresh(true);
        return 1;
    }

}
