package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class LuckCommand {

    public LuckCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonderluck").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(this::setEffects))))
        );
    }

    private int setEffects(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            int luck = IntegerArgumentType.getInteger(cmd, "value");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.getLuckManager().setLuck(luck);
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
