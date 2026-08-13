package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangeSanityCommand {

    public ChangeSanityCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("sanity").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(this::setValue))))
                .then(Commands.literal("add")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(this::changeValue))))
                .then(Commands.literal("print")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(this::printValue)))
        );
    }

    private int setValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.setSanity(IntegerArgumentType.getInteger(cmd, "value"));
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
            lTarget.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.setSanity(cap.getSanity() + IntegerArgumentType.getInteger(cmd, "value"));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int printValue(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            ServerPlayer executor = cmd.getSource().getPlayer();
            if(executor == null) return 0;
            lTarget.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap ->{
                executor.sendSystemMessage(Component.literal("Sanity of " + lTarget.getDisplayName().getString() + ": " + cap.getSanity() + "/" + cap.getMaxSanity()));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
