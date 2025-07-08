package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.beyonder.player.luck.LuckRange;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class CheckLuckAbility extends Ability {

    public CheckLuckAbility(int sequence){
        this.info = new AbilityInfo(5, 104, "Luck Check", sequence, 0, getCooldown(), "luck_check" + (sequence > 7 ? "1" : "2"));
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        PlayerLuckManager luckMng = cap.getLuckManager();
        LuckRange range = luckMng.getRange();
        if(getSequence() > 7){
            target.sendSystemMessage(Component.literal("Your luck is: " + luckMng.getLuck()));
            return true;
        } else {
            ItemStack item = target.getMainHandItem();
            Player itemTarget = MysticismHelper.getPlayerFromMysticalItem(item, (ServerLevel) target.level(), 0);
            if(itemTarget != null){
                Optional<EntityBeyonderManager> stats = itemTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(stats.isPresent()) {
                    target.sendSystemMessage(Component.literal(itemTarget.getName() + " luck: "
                            + stats.get().getLuckManager().getLuck()
                            + "\nTheir luck range is: " + stats.get().getLuckManager().getMinPassiveLuck()
                            + " to " + stats.get().getLuckManager().getMaxPassiveLuck()));
                    System.out.println(stats.get().getLuckManager().getRange());
                    return true;
                }
            }

            ServerLevel level = (ServerLevel) target.level();
            Vec3 lookAngle = target.getLookAngle();
            Vec3 pos = target.position();
            int radius = 3;
            AABB box = new AABB(
                    pos.x-radius, pos.y-radius, pos.z-radius,
                    pos.x+radius, pos.y+radius, pos.z+radius
            );
            ArrayList<Entity> hits = new ArrayList<>(level.getEntities(target, box, new Predicate<Entity>() {
                @Override
                public boolean test(Entity entity) {
                    if(entity instanceof LivingEntity living){
                        double dist = living.position().subtract(target.position()).length();
//                        System.out.println(dist);
//                        System.out.println(height);
                        boolean hit = living.getBoundingBoxForCulling().intersects(target.getEyePosition(),
                                target.getEyePosition().add(lookAngle.scale(dist+1)));
//                        System.out.println(hit);
                        return hit;
                    }
                    return false;
                }
            }));
            for(Entity ent: hits){
                if(ent instanceof LivingEntity entity){
                    Optional<EntityBeyonderManager> stats = entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
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
            return true;
        }
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

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
