package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class SetBeyonderCommand {

    public SetBeyonderCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("beyonder").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .then(Commands.argument("id", IntegerArgumentType.integer())
                                .executes(this::setSequence))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("target", EntityArgument.entity())
                            .executes(this::resetSequence)))
                .then(Commands.literal("consume")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                        .executes(this::consumeSequence)))));
    }

    private int setSequence(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            int pathSeqId = IntegerArgumentType.getInteger(cmd, "id");
            int pathwayId = pathSeqId / 10;
            int level = pathSeqId%10;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.resetBeyonder(false);
                for(int i = 9; i > level; i--){
                    cap.advance(10*pathwayId + i, true);
                    cap.getCharacteristicManager().setActing(1, 10*pathwayId + i);
                }
                cap.advance(pathSeqId, false);
                System.out.println(cap.getPageList().stream().map(page -> PageRegistry.getPageById(page).getTitle().getString()));

                System.out.println("Pages: " + cap.getPageList());
                //cap.getCharacteristicManager().setActing(1, pathSeqId);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int resetSequence(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.resetBeyonder(true);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }

    private int consumeSequence(CommandContext<CommandSourceStack> cmd){
        try {
            Entity target = EntityArgument.getEntity(cmd, "target");
            if(!(target instanceof LivingEntity lTarget)) return 0;
            lTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->{
                cap.advance(IntegerArgumentType.getInteger(cmd, "id"), false);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            return 0;
        }
    }
}
