package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.item.custom.CharacteristicItem;
import net.dinomine.potioneer.savedata.RitualSpiritsSaveData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SpiritDebugCommand {

    public SpiritDebugCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("debug_spirits").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("print")
                        .executes(this::printSpirits))
        );
    }

    private int printSpirits(CommandContext<CommandSourceStack> cmd){
        ServerLevel level = cmd.getSource().getLevel();
        String message = RitualSpiritsSaveData.from(level).getSpiritsAsString();
        if(cmd.getSource().getPlayer() != null){
            cmd.getSource().getPlayer().sendSystemMessage(Component.literal(message));
        } else {
            System.out.println(message);
        }
        return 1;
    }
}
