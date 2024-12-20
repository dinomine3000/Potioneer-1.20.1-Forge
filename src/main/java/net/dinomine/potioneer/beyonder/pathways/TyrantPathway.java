package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyrantPathway extends Beyonder{

    private static ArrayList<Consumer<Player>> passiveAbilities = new ArrayList<>();

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
    }

    public static void init(){
        passiveAbilities = new ArrayList<>();
        passiveAbilities.add(TyrantPathway::miningSpeedIncrease);
        passiveAbilities.add(TyrantPathway::giveWaterEffects);
        passiveAbilities.add(TyrantPathway::replenishStatsWhileUnderwater);
        passiveAbilities.add(TyrantPathway::mayFlyPlayer);
    }

    public static ArrayList<Consumer<Player>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }


    @Override
    public int getId() {
        return this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Swimmer";
            case 8 -> "Water-Blessed";
            case 7 -> "Hydro_Shaman";
            case 6 -> "Tempest";
            case 5 -> "Punisher";
            case 4 -> "Ice_Duke";
            case 3 -> "Prosperous_Prince";
            case 2 -> "Nature_Dictator";
            case 1 -> "Cataclysm_King";
            case 0 -> "Tyrant";
            default -> "";
        };
    }

    public static void miningSpeedIncrease(Player player) {
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            float f = 1;
            if(isInWater(player)){
                if (!player.onGround()) {
                    f *= 5.0F;
                }
                if (player.isUnderWater()) {
                    if(!EnchantmentHelper.hasAquaAffinity(player)){
                        f *= 5.0F;
                    }
                }
            }
            cap.multMiningSpeed(f);
        });
    }

    public static void giveWaterEffects(Player player){
        if(!player.level().isClientSide()){
            if(isInWater(player)){
                if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
                }
                if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, false, false));
                } else if(player.getEffect(MobEffects.NIGHT_VISION).endsWithin(205)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, false, false));
                }
            }
        }
    }

    private static void mayFlyPlayer(Player player){
        if(isInWater(player)){
            if(!player.isCreative() && !player.isSpectator()){
                player.getAbilities().mayfly = true;
            }
        } else if((player.getAbilities().flying || player.getAbilities().mayfly) && !player.isCreative() && !player.isSpectator()){
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
    }

    private static void replenishStatsWhileUnderwater(Player player){
        if(isInWater(player)){
            if(player.getFoodData().needsFood() && Math.random() < 0.01){
                player.getFoodData().eat(1, 0);
            }
        }
    }

    private static boolean isInWater(Player player){
        return player.isInWater();
    }

    private void onTick9(Player player){
        //add effects
        //check if underwater, if so add more effects
        replenishStatsWhileUnderwater(player);
        miningSpeedIncrease(player);
        //mayFlyPlayer(player);
        giveWaterEffects(player);
    }
}
