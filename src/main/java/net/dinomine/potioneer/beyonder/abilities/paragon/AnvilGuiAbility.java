package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.menus.CrafterAnvilMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.*;
import net.minecraftforge.network.NetworkHooks;

public class AnvilGuiAbility extends Ability {

    public AnvilGuiAbility(int sequence){
        this.info = new AbilityInfo(109, 104, "Anvil Gui", 40 + sequence, 10, this.getCooldown(), "anvil_gui");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof ServerPlayer player){
            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider((i, inventory, player1) ->
                            new CrafterAnvilMenu(i, inventory, ContainerLevelAccess.create(player1.level(), player1.getOnPos()), getSequence()),
                            Component.literal("craft men")),
                    buff -> buff.writeInt(getSequence()));

            cap.requestActiveSpiritualityCost(info.cost());
            return true;
        }
        return false;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
