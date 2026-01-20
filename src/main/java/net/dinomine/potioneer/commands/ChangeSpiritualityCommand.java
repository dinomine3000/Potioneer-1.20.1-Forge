package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangeSpiritualityCommand {

    public ChangeSpiritualityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("spirituality").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(this::setValue))))
                .then(Commands.literal("add")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(this::changeValue))))
        );
    }

    private int setValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.setSpirituality(IntegerArgumentType.getInteger(cmd, "value"));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int changeValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.setSpirituality(cap.getSpirituality() + IntegerArgumentType.getInteger(cmd, "value"));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
