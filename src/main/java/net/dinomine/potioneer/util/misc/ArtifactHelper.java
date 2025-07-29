package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.mystery.AirBulletAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.MeltAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.LuckTrendAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MinerLightAbility;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.downsides.DummyDownside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ArtifactHelper {
    public static final String ARTIFACT_TAG_ID = "artifact_info";
    public static final String GEM_TAG_ID = "gem_ability_info";
    public static final String BEYONDER_TAG_ID = "beyonder_info";
    public static final String CHARM_TAG_ID = "mystical_charm_info";

    private static final HashMap<String, MetaAbilityEntry> abilityMap = new HashMap<>();
    private static final HashMap<String, MetaAbilityEntry> downsideMap = new HashMap<>();
    private static final HashMap<String, MetaEffectEntry> effectMap = new HashMap<>();

    static {
        //define minimum and maximum sequences here to define what levels can generate the ability
        abilityMap.put("miner_light", new MetaAbilityEntry(0, 10, MinerLightAbility::new));
        abilityMap.put("water_affinity", new MetaAbilityEntry(10, 20, WaterAffinityAbility::new));
        abilityMap.put("air_bullet", new MetaAbilityEntry(20, 30, AirBulletAbility::new));
        abilityMap.put("melt", new MetaAbilityEntry(30, 40, MeltAbility::new));
        abilityMap.put("crafting_gui", new MetaAbilityEntry(40, 50, CraftingGuiAbility::new));

        //abilities for gems and amulets
        abilityMap.put("lucky_trend", new MetaAbilityEntry(-1, -1, LuckTrendAbility::new));

        downsideMap.put("d_dummy", new MetaAbilityEntry(0, 50, DummyDownside::new));

        effectMap.put("silk", new MetaEffectEntry(0, 8, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, 8, 0, dur, true)));
        effectMap.put("water_affinity", new MetaEffectEntry(10, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, 9, 5, dur, true)));
        effectMap.put("life_sap", new MetaEffectEntry(20, 9, dur -> BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REGEN, 9, 0, dur, true)));
    }

    public static BeyonderEffect getEffectFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !charm.hasTag() || !charm.getTag().contains(CHARM_TAG_ID)) return null;
        String effId = charm.getTag().getCompound(CHARM_TAG_ID).getString("effectId");
        int duration = charm.getTag().getCompound(CHARM_TAG_ID).getInt("duration");
        return effectMap.get(effId).constructor.apply(duration);
    }

    public static int getPathwayIdFromCharm(ItemStack charm){
        if(!charm.is(ModItems.CHARM.get()) || !charm.hasTag() || !charm.getTag().contains(CHARM_TAG_ID)) return -1;
        return charm.getTag().getCompound(CHARM_TAG_ID).getInt("pathwayId");
    }

    //artifact without beyonder info or downsides. single use
    public static void makeCharm(ItemStack stack, int pathwayId, int duration){
        String effId = getEffectId(pathwayId);
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(CHARM_TAG_ID) || effId.isEmpty()) return;

        CompoundTag charmTag = new CompoundTag();
        charmTag.putString("effectId", effId);
        charmTag.putInt("duration", duration);
        charmTag.putInt("pathwayId", pathwayId);
        tag.put(CHARM_TAG_ID, charmTag);
    }

    //artifact with only passive and free abilities and no downsides and no beyonder tag
    public static void makeAmuletGem(ItemStack gemStack, String ablId, int pathwayId, int color){
        CompoundTag ogTag = gemStack.getOrCreateTag();
        if(ogTag.contains(ARTIFACT_TAG_ID) || ogTag.contains(GEM_TAG_ID)) return;
        if(!gemStack.is(ModItems.GEM.get())) return;

        CompoundTag gemTag = new CompoundTag();
        ListTag abilitiesList = new ListTag();
        abilitiesList.add(makeAbilityTag(ablId, pathwayId));
        gemTag.put("abilities", abilitiesList);
        gemTag.putInt("color", color);
        ogTag.put(GEM_TAG_ID, gemTag);
        gemStack.setTag(ogTag);
    }

    //artifact weapons without downsides and without beyonder tag
    public static void makeBeyonderWeapon(){}

    public static void makeSealedArtifact(ItemStack stack, int pathwayId, RandomSource random){
        CompoundTag root = stack.getOrCreateTag();
        if(root.contains(BEYONDER_TAG_ID)) return;

        CompoundTag beyonderInfo = new CompoundTag();
        beyonderInfo.putInt("id", pathwayId);
        root.put(BEYONDER_TAG_ID, beyonderInfo);
        System.out.println("Created artifact beyonder info:\n"+beyonderInfo);

        CompoundTag artifactInfo = new CompoundTag();
        ListTag abilitiesList = new ListTag();
        ListTag downsidesList = new ListTag();
        abilitiesList.add(makeRandomAbilityTag(pathwayId, random));
        downsidesList.add(makeRandomDownsideTag(pathwayId, random));
        artifactInfo.put("abilities", abilitiesList);
        artifactInfo.put("downsides", downsidesList);
        root.put(ARTIFACT_TAG_ID, artifactInfo);
        System.out.println("Created Artifact info:\n"+artifactInfo);
        stack.setTag(root);
    }

    private static CompoundTag makeAbilityTag(String ablId, int pathwayId){
        CompoundTag res = new CompoundTag();
        res.putString("ablId", ablId);
        res.putInt("sequence", pathwayId);
        return res;
    }

    private static CompoundTag makeRandomAbilityTag(int pathwayId, RandomSource random){
        //ability Id given should be the normalized one
        return makeAbilityTag(getRandomAbilityId(pathwayId, random), pathwayId);
    }

    private static CompoundTag makeRandomDownsideTag(int pathwayId, RandomSource random){
        CompoundTag res = new CompoundTag();
        String ablId = getRandomDownsideId(pathwayId, random);
        res.putString("ablId", ablId);
        res.putInt("sequence", pathwayId);
        return res;
    }

    private static String getRandomAbilityId(int pathwayId, RandomSource random) {
        List<Map.Entry<String, MetaAbilityEntry>> matching = abilityMap.entrySet().stream()
                .filter(entry -> entry.getValue().isInRange(pathwayId))
                .toList();

        if (matching.isEmpty()) System.out.println("No ability IDs match sequence: " + pathwayId);

        return matching.get(random.nextInt(matching.size())).getKey();
    }

    private static String getRandomDownsideId(int pathwayId, RandomSource random) {
        List<Map.Entry<String, MetaAbilityEntry>> matching = downsideMap.entrySet().stream()
                .filter(entry -> entry.getValue().isInRange(pathwayId))
                .toList();

        if (matching.isEmpty()) System.out.println("No ability IDs match sequence: " + pathwayId);

        return matching.get(random.nextInt(matching.size())).getKey();
    }
    private static String getEffectId(int pathwayId) {
        List<Map.Entry<String, MetaEffectEntry>> matching = effectMap.entrySet().stream()
                .filter(entry -> entry.getValue().isInRange(pathwayId))
                .toList();

        if (matching.isEmpty()){
            System.out.println("No ability IDs match sequence: " + pathwayId);
            return "";
        }

        return matching.get(0).getKey();
    }

    public static boolean isValidItemForArtifact(ItemStack stack){
        return !isValidArtifact(stack) && (stack.is(Tags.Items.TOOLS) || stack.is(ModItems.RING.get())) && !stack.is(ModItems.CHARACTERISTIC.get());
    }

    public static boolean isValidArtifact(ItemStack stack){
        boolean artifactCheck = stack.hasTag() && stack.getTag().contains(ARTIFACT_TAG_ID);
//        boolean enabledCheck = artifactCheck &&
//                (!stack.getTag().getCompound(ARTIFACT_TAG_ID).contains("enabled")
//                        || stack.getTag().getCompound(ARTIFACT_TAG_ID).getBoolean("enabled")
//                );
        return artifactCheck;
    }

    public static Ability getAbilityFromId(String id, int sequence){
        if(id.substring(0, 2).equals("d_")){
//            System.out.println("Found a downside: " + id + " and " + sequence);
            int lastUnderscore = id.lastIndexOf('_');
            int newCd = Integer.parseInt(id.substring(lastUnderscore + 1));
//            System.out.println("Checking for ability at key value: " + id.substring(0, lastUnderscore));
//            System.out.println("New cooldown for it is: " + newCd);
            Downside downside = (Downside) downsideMap.get(id.substring(0, lastUnderscore)).constructor.apply(sequence%10);
            downside.copyCd(newCd);
            return downside;
        }
        return abilityMap.get(id).constructor.apply(sequence%10);
    }

    /**
     *
     * @param stack
     * @return the artifact ids corresponding to the ability and downside
     */
    public static List<String> getArtifactIdsFromItem(ItemStack stack){
        if(!stack.hasTag() || !stack.getTag().contains(ARTIFACT_TAG_ID)) return new ArrayList<>();
//        if(stack.is(ModItems.AMULET.get())) return new ArrayList<>();
        if(stack.getTag().getCompound(ARTIFACT_TAG_ID).contains("enabled")){
            if(!stack.getTag().getCompound(ARTIFACT_TAG_ID).getBoolean("enabled")) return new ArrayList<>();
        }
        ArrayList<String> abilityIds = new ArrayList<>();
        CompoundTag artifactTag = stack.getTag().getCompound(ARTIFACT_TAG_ID);
        ListTag abilities = artifactTag.getList("abilities", ListTag.TAG_COMPOUND);
        ListTag downsides = artifactTag.getList("downsides", ListTag.TAG_COMPOUND);
        int biggestCd = 0;
        for(Tag tag: abilities){
            if(tag instanceof CompoundTag ablTag){
                String ablId = ablTag.getString("ablId");
                int ablSequence = ablTag.getInt("sequence");
                abilityIds.add((ablSequence < 10 ? "0" + ablSequence : ablSequence) + "_" + ablId);
                biggestCd = Math.max(biggestCd, getAbilityFromId(ablId, ablSequence).getCooldown());
                //System.out.println("Biggest CD: " + biggestCd);
            }
        }
        for(Tag tag: downsides){
            if(tag instanceof CompoundTag ablTag){
                String downId = ablTag.getString("ablId");
                int downSequence = ablTag.getInt("sequence");
                abilityIds.add((downSequence < 10 ? "0" + downSequence : downSequence) + "_" + downId + "_" + biggestCd);

            }
        }
        //System.out.println("Read artifact data as: " + abilityIds);
        return abilityIds;
    }

    record MetaAbilityEntry(int minSequence, int maxSequence, Function<Integer, Ability> constructor) {

        boolean isInRange(int sequence) {
            return sequence >= minSequence && sequence < maxSequence;
        }
    }

    record MetaEffectEntry(int pathwayId, int minSequence, Function<Integer, BeyonderEffect> constructor){
        boolean isInRange(int targetPathwayId) {return pathwayId/10 == targetPathwayId/10 && targetPathwayId%10 <= minSequence;}
    }
}
