package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.util.PotionIngredient;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public record PotionRecipeData(ArrayList<PotionIngredient> main, ArrayList<PotionIngredient> supplementary, int waterLevel, boolean fire, int id, boolean includeRitual, String name) {

    public CompoundTag save(CompoundTag nbt){
        nbt.putInt("id", id);
        nbt.putInt("water", waterLevel());
        nbt.putBoolean("fire", fire());
        nbt.putInt("size_main", main.size());
        for(int i = 0; i < main.size(); i++){
            CompoundTag temp = new CompoundTag();
            main.get(i).toNbt(temp);
            nbt.put("main_" + i, temp);
        }

        nbt.putInt("size_supp", supplementary.size());
        for(int i = 0; i < supplementary.size(); i++){
            CompoundTag temp = new CompoundTag();
            supplementary.get(i).toNbt(temp);
            nbt.put("supp_" + i, temp);
        }

        if(id >= -1 && id%10 <= 5){
            nbt.putBoolean("includeRitual", includeRitual);
        }
        nbt.putString("name", name == null ? "" : name);
        return nbt;
    }

    public static PotionRecipeData load(CompoundTag nbt){
        int id = nbt.getInt("id");
        int water = nbt.getInt("water");
        boolean fire = nbt.getBoolean("fire");

        int size = nbt.getInt("size_main");
        ArrayList<PotionIngredient> main = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                CompoundTag temp = nbt.getCompound("main_" + i);
                main.add(PotionIngredient.fromNbt(temp));
            }
        }

        int sizeSupp = nbt.getInt("size_supp");
        ArrayList<PotionIngredient> supp = new ArrayList<>();
        if(sizeSupp != 0){
            for(int i = 0; i < sizeSupp; i++){
                CompoundTag temp = nbt.getCompound("supp_" + i);
                supp.add(PotionIngredient.fromNbt(temp));
            }
        }
        boolean ritual = nbt.getBoolean("includeRitual");
        String name = nbt.getString("name");
        return new PotionRecipeData(main, supp, water, fire, id, ritual, name);
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeNbt(save(new CompoundTag()));
    }

    public static PotionRecipeData decode(FriendlyByteBuf buffer){
        CompoundTag tag = buffer.readNbt();
        if(tag == null) return null;
        return load(tag);
    }

    public PotionRecipeData copy(){
        return new PotionRecipeData(new ArrayList<>(main), new ArrayList<>(supplementary), waterLevel, fire, id, includeRitual, name);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PotionRecipeData otherData)) return false;

        return PotionFormulaSaveData.isContainedIn(this.main, otherData.main)
                && PotionFormulaSaveData.isContainedIn(this.supplementary, otherData.supplementary)
                && this.fire == otherData.fire
                && this.waterLevel == otherData.waterLevel
                && this.id == otherData.id
                && getName(this).equalsIgnoreCase(getName(otherData));

    }

    public static String getName(CompoundTag formulaTag){
        return getName(load(formulaTag));
    }

    public static String getName(PotionRecipeData data){
        if(data.id >= 0)
            return Component.translatable(
                    "potioneer.beyonder.sequence." + Pathways.getPathwayBySequenceId(data.id).getSequenceNameFromId(data.id, false)
            ).getString();

        String key = "item.potioneer." + data.name;
        Component comp;
        if(I18n.exists(key))
            comp = Component.translatable(key);
        else
            comp = Component.literal(capitalizeFirstLetters(data.name.replace("_", " ")));
        return comp.getString();
    }

    static String capitalizeFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        return Arrays.stream(input.split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
