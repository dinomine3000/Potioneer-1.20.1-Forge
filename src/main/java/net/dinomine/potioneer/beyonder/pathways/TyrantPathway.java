package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TyrantPathway extends Beyonder{

    private static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> passiveAbilities = new ArrayList<>();

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 250, 100};

    }

    public static void init(){
        passiveAbilities = new ArrayList<>();
        passiveAbilities.add(TyrantPathway::miningSpeedIncrease);
        passiveAbilities.add(TyrantPathway::giveWaterEffects);
        passiveAbilities.add(TyrantPathway::replenishStatsWhileUnderwater);
        passiveAbilities.add(TyrantPathway::mayFlyPlayer);
    }

    public static ArrayList<BiConsumer<Player, PlayerBeyonderStats>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }


    @Override
    public int getId() {
        return 10 + this.sequence;
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

    public static void miningSpeedIncrease(Player player, PlayerBeyonderStats cap) {
        float f = 1;
        //doesnt cost spirituality
        if(isInWater(player)){
            if (!player.onGround() && cap.getSequenceLevel() < 9) {
                f *= 5.0F;
            }
            if (player.isUnderWater()) {
                if(!EnchantmentHelper.hasAquaAffinity(player)){
                    f *= 5.0F;
                }
            }
        }
        cap.multMiningSpeed(f);
    }


    public static void giveWaterEffects(Player player, PlayerBeyonderStats cap){
        //On average, this calls the cost once a second
        float cost = 4f;
        if(!player.level().isClientSide()){
            if(isInWater(player) && cap.getSpirituality() > 0){
                if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 38, 0, false, false));
                    cap.requestSpiritualityCost(cost);
                } else if(player.getEffect(MobEffects.WATER_BREATHING).endsWithin(25)){
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 38, 0, false, false));
                    cap.requestSpiritualityCost(cost);
                }
                if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 230, 0, false, false));
                    cap.requestSpiritualityCost(cost);
                } else if(player.getEffect(MobEffects.NIGHT_VISION).endsWithin(205)){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 230, 0, false, false));
                    cap.requestSpiritualityCost(cost);
                }
            }
        }
    }

    private static void mayFlyPlayer(Player player, PlayerBeyonderStats cap){
        //calls cost once every tick
        if(isInWater(player) && cap.getSequenceLevel() < 9 && cap.getSpirituality() > 0){
            if(!player.isCreative() && !player.isSpectator()){
                if(player.getAbilities().flying) cap.requestSpiritualityCost(3f);
                cap.mayFly = true;
            }
        } else if((player.getAbilities().flying || player.getAbilities().mayfly) && !player.isCreative() && !player.isSpectator()){
            cap.mayFly = false;
        }
    }

    private static void replenishStatsWhileUnderwater(Player player, PlayerBeyonderStats cap){
        if(isInWater(player)){
            //cap.requestSpiritualityCost(-4);
            if(player.getFoodData().needsFood() && Math.random() < 0.02){
                cap.requestSpiritualityCost(20);
                player.getFoodData().eat(1, 0);
            }
        }
    }

    private static boolean isInWater(Player player){
        return player.isInWater();
    }

}
