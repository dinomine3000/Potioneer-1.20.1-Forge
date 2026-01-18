package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class PageManageCommand {

    public PageManageCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonderpage").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("grant")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("pageNumber", IntegerArgumentType.integer())
                                    .executes(this::grant))))
                .then(Commands.literal("clear")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::clear)))
                .then(Commands.literal("fill")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(this::grantAll)))
        );
    }

    private int grant(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            int pageNum = IntegerArgumentType.getInteger(cmd, "pageNumber");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.addPage(pageNum);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int grantAll(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                PageRegistry.getAllKeys().forEach(cap::addPage);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int clear(CommandContext<CommandSourceStack> cmd){
        try{
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            ServerPlayer executor = cmd.getSource().getPlayer();
            if(executor == null) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(LivingEntityBeyonderCapability::clearPages);
            return 1;

        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

}
