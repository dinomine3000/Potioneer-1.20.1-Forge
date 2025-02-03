package net.dinomine.potioneer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.FormulaItem;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RefreshFormulasCommand {

    public RefreshFormulasCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("formula").requires(stack -> {
                            return stack.getPlayer().hasPermissions(2);
                        })
                .then(Commands.literal("refresh")
                        .executes(this::refresh))
                .then(Commands.literal("generate")
                        .then(Commands.argument("id", IntegerArgumentType.integer())
                                .executes(this::create)))
        );
    }

    private int refresh(CommandContext<CommandSourceStack> cmd){
        System.out.println("attempting refresh...");
        PotionFormulaSaveData data = PotionFormulaSaveData.from(cmd.getSource().getLevel());
        data.requestRefresh(true);
        return 1;
    }

    private int create(CommandContext<CommandSourceStack> cmd){
        PotionFormulaSaveData data = PotionFormulaSaveData.from(cmd.getSource().getLevel());
        PotionRecipeData result = data.getDataFromId(IntegerArgumentType.getInteger(cmd, "id"));
        if(result == null){
            cmd.getSource().getPlayer().sendSystemMessage(Component.literal("Could not find formula for the specified id"));
        } else {
            FormulaItem formulaItem = (FormulaItem) ModItems.FORMULA.get();
            ItemStack stack = new ItemStack(formulaItem);
            formulaItem.writeToNbt(stack, result, false);
            cmd.getSource().getPlayer().addItem(stack);
        }
        return 1;
    }

}
