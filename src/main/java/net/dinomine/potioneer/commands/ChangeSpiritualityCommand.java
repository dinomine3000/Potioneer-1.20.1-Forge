package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ChangeSpiritualityCommand {

    public ChangeSpiritualityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("spirituality").requires(stack -> {
                            return stack.getPlayer().hasPermissions(2);
                        })
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(this::setValue)))
                .then(Commands.literal("add")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(this::changeValue)))
        );
    }

    private int setValue(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            if(!player.level().isClientSide()) cap.setSpirituality(IntegerArgumentType.getInteger(cmd, "value"));
        });
        return 1;
    }

    private int changeValue(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            if(!player.level().isClientSide()) cap.setSpirituality(cap.getSpirituality() + IntegerArgumentType.getInteger(cmd, "value"));
        });
        return 1;
    }
}
