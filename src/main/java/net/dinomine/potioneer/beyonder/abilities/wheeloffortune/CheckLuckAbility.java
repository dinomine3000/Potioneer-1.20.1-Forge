package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class CheckLuckAbility extends Ability {

    public CheckLuckAbility(int sequence){
//        this.info = new AbilityInfo(5, 104, "Luck Check", sequence, 0, getMaxCooldown(), "luck_check" + (sequence > 7 ? "1" : "2"));
        super(sequence);
        setCost(in -> 0);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "luck_check" + (sequenceLevel > 7 ? "1" : "2");
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        PlayerLuckManager luckMng = cap.getLuckManager();
        LuckRange range = luckMng.getRange();
        if(getSequenceLevel() > 7){
            target.sendSystemMessage(Component.literal("Your luck is: " + luckMng.getLuck()));
        } else {
            ItemStack item = target.getMainHandItem();
            Player itemTarget = MysticismHelper.getPlayerFromMysticalItem(item, (ServerLevel) target.level(), 0);
            if(itemTarget != null){
                Optional<LivingEntityBeyonderCapability> stats = itemTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(stats.isPresent()) {
                    target.sendSystemMessage(Component.literal(itemTarget.getName() + " luck: "
                            + stats.get().getLuckManager().getLuck()
                            + "\nTheir luck range is: " + stats.get().getLuckManager().getMinPassiveLuck()
                            + " to " + stats.get().getLuckManager().getMaxPassiveLuck()));
                    System.out.println(stats.get().getLuckManager().getRange());
                    return true;
                }
            }
            int radius = 3;
            ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, radius);

            for(Entity ent: hits){
                if(ent instanceof LivingEntity entity){
                    Optional<LivingEntityBeyonderCapability> stats = entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                    if(stats.isPresent()) {
                        target.sendSystemMessage(Component.literal(entity.getName().getString() + " luck: "
                                + stats.get().getLuckManager().getLuck()
                                + "\nTheir luck range is: " + stats.get().getLuckManager().getMinPassiveLuck()
                                + " to " + stats.get().getLuckManager().getMaxPassiveLuck()));
                        System.out.println(stats.get().getLuckManager().getRange());
                        return true;
                    }
                }
            }


            target.sendSystemMessage(Component.literal("Your luck is: " + luckMng.getLuck()
                    + "\nYour range is: " + range.getMinLuck() + " to " + range.getMaxLuck()));
            System.out.println(luckMng.getRange());
        }
        return true;
    }
}
