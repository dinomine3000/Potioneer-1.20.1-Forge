package net.dinomine.potioneer.beyonder.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.mystery.AirBulletAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.MeltAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MinerLightAbility;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.downsides.DummyDownside;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
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
    public static final String BEYONDER_TAG_ID = "beyonder_info";

    private static final HashMap<String, MetaAbilityEntry> abilityMap = new HashMap<>();
    private static final HashMap<String, MetaAbilityEntry> downsideMap = new HashMap<>();

    static {
        //define minimum and maximum sequences here to define what levels can generate the ability
        abilityMap.put("miner_light", new MetaAbilityEntry(0, 10, MinerLightAbility::new));
        abilityMap.put("water_affinity", new MetaAbilityEntry(10, 20, WaterAffinityAbility::new));
        abilityMap.put("air_bullet", new MetaAbilityEntry(20, 30, AirBulletAbility::new));
        abilityMap.put("melt", new MetaAbilityEntry(30, 40, MeltAbility::new));
        abilityMap.put("crafting_gui", new MetaAbilityEntry(40, 50, CraftingGuiAbility::new));

        downsideMap.put("d_dummy", new MetaAbilityEntry(0, 50, DummyDownside::new));
    }

//    public static void makeSealedArtifact(ItemStack stack, RandomSource random){
//    }

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
        abilitiesList.add(makeAbilityTag(pathwayId, random));
        downsidesList.add(makeDownsideTag(pathwayId, random));
        artifactInfo.put("abilities", abilitiesList);
        artifactInfo.put("downsides", downsidesList);
        root.put(ARTIFACT_TAG_ID, artifactInfo);
        System.out.println("Created Artifact info:\n"+artifactInfo);
        stack.setTag(root);
    }

    private static CompoundTag makeAbilityTag(int pathwayId, RandomSource random){
        //ability Id given should be the normalized one
        CompoundTag res = new CompoundTag();
        String ablId = getRandomAbilityId(pathwayId, random);
        res.putString("ablId", ablId);
        res.putInt("sequence", pathwayId);
        return res;
    }

    private static CompoundTag makeDownsideTag(int pathwayId, RandomSource random){
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

    public static boolean isValidItemForArtifact(ItemStack stack){
        return !isValidArtifact(stack) && (stack.is(Tags.Items.TOOLS) || stack.is(ModItems.RING.get())) && !stack.is(ModItems.CHARACTERISTIC.get());
    }

    public static boolean isValidArtifact(ItemStack stack){
        return stack.hasTag() && stack.getTag().contains(ARTIFACT_TAG_ID);
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
}
