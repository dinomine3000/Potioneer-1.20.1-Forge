package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerCharacteristicManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ChangeActingCommand {

    public ChangeActingCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("acting").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("percent", IntegerArgumentType.integer(0, 100))
                            .then(Commands.argument("sequence", IntegerArgumentType.integer(0, 49))
                        .executes(this::setValue))))
                .then(Commands.literal("print")
                        .executes(this::printValue))
        );
    }

    private int setValue(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
            cap.getCharacteristicManager().setActing(IntegerArgumentType.getInteger(cmd, "percent")/100d, IntegerArgumentType.getInteger(cmd, "sequence"));
        });
        return 1;
    }

    private int printValue(CommandContext<CommandSourceStack> cmd){
        ServerPlayer player = cmd.getSource().getPlayer();
        Component message;
        if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
            LivingEntityBeyonderCapability cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            PlayerCharacteristicManager mng = cap.getCharacteristicManager();
            message = mng.getDescComponent();
            player.sendSystemMessage(message);
        }
        return 1;
    }
}
