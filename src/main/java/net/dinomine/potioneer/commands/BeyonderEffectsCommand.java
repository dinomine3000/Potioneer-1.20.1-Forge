package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderEffectsCommand {

    public BeyonderEffectsCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyondereffects").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("clear")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::cleareffects)))
                .then(Commands.literal("print")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::printeffects)))
        );
    }

    private int cleareffects(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.getEffectsManager().clearEffects(cap, lTarget);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int printeffects(CommandContext<CommandSourceStack> cmd){
        try{
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            ServerPlayer executor = cmd.getSource().getPlayer();
            if(executor == null) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                executor.sendSystemMessage(Component.literal(cap.getEffectsManager().toString()));
            });
            return 1;

        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

}
