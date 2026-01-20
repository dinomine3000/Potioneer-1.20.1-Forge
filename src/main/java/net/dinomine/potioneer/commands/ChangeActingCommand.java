package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerCharacteristicManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangeActingCommand {

    public ChangeActingCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("acting").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("percent", IntegerArgumentType.integer(0, 100))
                                .then(Commands.argument("sequence", IntegerArgumentType.integer(0, 49))
                        .executes(this::setValue)))))
                .then(Commands.literal("print")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::printValue)))
        );
    }

    private int setValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.getCharacteristicManager().setActing(IntegerArgumentType.getInteger(cmd, "percent")/100d, IntegerArgumentType.getInteger(cmd, "sequence"));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int printValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity livingEntity)) return 0;
            livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                PlayerCharacteristicManager mng = cap.getCharacteristicManager();
                Component message = mng.getDescComponent();
                cmd.getSource().getPlayer().sendSystemMessage(message);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
