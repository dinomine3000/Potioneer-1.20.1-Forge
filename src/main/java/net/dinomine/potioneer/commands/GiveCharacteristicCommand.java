package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.item.custom.CharacteristicItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GiveCharacteristicCommand {

    public GiveCharacteristicCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("characteristic").requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("sequenceId", IntegerArgumentType.integer())
                            .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::giveCharacteristic))))
        );
    }

    private int giveCharacteristic(CommandContext<CommandSourceStack> cmd){
        try {
            ServerPlayer player = EntityArgument.getPlayer(cmd, "player");
            ItemStack characteristic = CharacteristicItem.createCharacteristic(IntegerArgumentType.getInteger(cmd, "sequenceId"));
            if(!player.addItem(characteristic)){
                player.drop(characteristic, false, false);
                return 1;
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
