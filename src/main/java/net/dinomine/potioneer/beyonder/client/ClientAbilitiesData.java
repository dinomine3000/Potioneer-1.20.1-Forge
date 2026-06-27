package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD;
import net.dinomine.potioneer.util.misc.ArtifactHolder;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderAbilitiesScreen;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerSyncHotbarMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientAbilitiesData {
    private static Consumer<ArrayList<AbilityInfo>> onListChange = null;
    public static ArrayList<AbilityInfo> getAbilities() {
        return new ArrayList<>(abilities.values().stream().toList());
    }
    public static ArrayList<AbilityInfo> getAbilities(Consumer<ArrayList<AbilityInfo>> runOnUpdate) {
        onListChange = runOnUpdate;
        return getAbilities();
    }

    public static boolean isHotbarValid(){
        return !hotbar.isEmpty() && abilities.get(hotbar.get(caret)) != null;
    }

    public static boolean isHotbarVisible(){
        return AbilitiesHotbarHUD.hotbarAnimation.isPlaying();
    }

    public static void setShowHotbar(boolean val){
        if(configScreenOpenAnimation) return;
        if(!isHotbarValid()){
            AbilitiesHotbarHUD.hotbarAnimation.tickInReverse(true);
            return;
        }
        if(val && !isHotbarVisible()){
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("potioneer.ability_name." + abilities.get(hotbar.get(caret)).descId()), true);
        }
        AbilitiesHotbarHUD.hotbarAnimation.tickInReverse(!val);
    }

    public static void showHotbarOnConfigScreen(boolean show){
        if(hotbar.isEmpty()) return;
        AbilitiesHotbarHUD.hotbarAnimation.tickInReverse(!show);
        configScreenOpenAnimation = show;
    }

    public static void updateCaret(){
        if(hotbar.isEmpty()){
            caret = 0;
            return;
        }
        caret = Mth.clamp(caret, 0, hotbar.size() - 1);
    }

    public static void setAbilities(List<AbilityInfo> abilities2) {
        clearAbilitiesOf(false);
        for (AbilityInfo abl : abilities2) {
            abilities.put(abl.getKey(), abl);
        }
        updateHotbarOnChange();
//        if(changingPath) hotbar = new ArrayList<>();
//        if(changingPath) quickSelect = "";
        if(!hasQuickSelect() && !abilities.containsKey(quickSelect)) quickSelect = new AbilityKey();
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().setAbilitiesOnClient(abilities2, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to set abilities on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static boolean hasQuickSelect(){
        return quickSelect != null && !quickSelect.isEmpty();
    }

    public static void addAbilities(List<AbilityInfo> abilities2){
        for(AbilityInfo info: abilities2){
            if(abilities.containsKey(info.getKey())) continue;
            abilities.put(info.getKey(), info);
        }
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().addAbilitiesOnClient(abilities2, cap, Minecraft.getInstance().player, true);
            else
                System.out.println("Player is null while trying to add abilities on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static void removeAbilities(List<AbilityInfo> abilities2){
        for(AbilityInfo info: abilities2){
            if(!abilities.containsKey(info.getKey())) continue;
            abilities.remove(info.getKey());
        }

        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().removeAbilitiesOnClient(abilities2, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to remove abilities on client side.");
        });
        updateHotbarOnChange();
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static void updateAbilities(List<AbilityInfo> abilities2){
        for(AbilityInfo abl: abilities2){
            AbilityKey key = abl.getKey();
            if(key == null){
                System.out.println("Warning: tried to update an ability with a null id: " + abl.descId());
                continue;
            }
            if(!abilities.containsKey(key)) continue;
//            if(key.isArtifactKey()) continue;
            abilities.put(key, abl);
        }

        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().updateAbilitiesOnClient(abilities2, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to update abilities on client side.");
        });

        if(onListChange != null) onListChange.accept(getAbilities());
    }

    private static void updateHotbarOnChange(){
        if(hotbar == null) hotbar = new ArrayList<>();
        for(AbilityKey key: hotbar){
            if (abilities.containsKey(key)) continue;
            if (!key.getGroup().equals(PlayerAbilitiesManager.AbilityList.INTRINSIC.name())) continue;
            for(AbilityKey iKey: abilities.keySet()){
                if(iKey.isSameAbility(key.getAbilityId())
                        && iKey.isSameGroup(PlayerAbilitiesManager.AbilityList.INTRINSIC.name())){
                    key.setSequenceLevel(iKey.getSequenceLevel());
                    break;
                }
            }
        }

        if(!hotbar.isEmpty()){
            hotbar.removeIf(key -> !abilities.containsKey(key));
        }
        setHotbarChanged();
    }

    public static void setArtifacts(List<ArtifactHolder> artifacts){
        clearAbilitiesOf(true);
        for(ArtifactHolder artifact: artifacts){
            for (AbilityInfo abl : artifact.getAbilitiesInfo(false)) {
                abilities.put(abl.getKey(), abl);
            }
        }
        updateHotbarOnChange();
//        if(changingPath) hotbar = new ArrayList<>();
//        if(changingPath) quickSelect = "";
        if(!hasQuickSelect() && !abilities.containsKey(quickSelect)) quickSelect = new AbilityKey();
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().setArtifactsOnClient(artifacts, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to set artifacts on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }


    public static void updateArtifacts(List<ArtifactHolder> artifacts) {
        for(ArtifactHolder artifact: artifacts){
            for (AbilityInfo abl : artifact.getAbilitiesInfo(false)) {
                abilities.put(abl.getKey(), abl);
            }
        }
        updateHotbarOnChange();
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().updateArtifactsOnClient(artifacts, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to set artifacts on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static void removeArtifacts(List<ArtifactHolder> artifacts) {
        for(ArtifactHolder artifact: artifacts){
            for(AbilityInfo info: artifact.getAbilitiesInfo(false)){
                if(!abilities.containsKey(info.getKey())) continue;
                abilities.remove(info.getKey());
            }
        }

        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().removeArtifactsOnClient(artifacts, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to remove artifacts on client side.");
        });
        updateHotbarOnChange();
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static void addArtifacts(List<ArtifactHolder> artifacts) {
        for(ArtifactHolder artifact: artifacts){
            for(AbilityInfo info: artifact.getAbilitiesInfo(false)){
                if(abilities.containsKey(info.getKey())) continue;
                abilities.put(info.getKey(), info);
            }
        }
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().addArtifactsOnClient(artifacts, cap, Minecraft.getInstance().player, true);
            else
                System.out.println("Player is null while trying to add artifacts on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static AbilityKey getQuickAbility(){
        return quickSelect;
    }

    public static void setQuickAbility(AbilityKey id){
        quickSelect = id;
        if(id == null) quickSelect = new AbilityKey();
    }

    public static ArrayList<AbilityKey> getHotbar() {
        return hotbar;
    }

    public static void setHotbar(ArrayList<AbilityKey> hotbar2) {
        hotbar = hotbar2;
        updateHotbarOnChange();
    }

    public static void tick(float dt){
        if(Minecraft.getInstance().isSingleplayer() && Minecraft.getInstance().isPaused()) return;

        time += dt;
        if(time > 1){
            for(Map.Entry<AbilityKey, AbilityInfo> entry: abilities.entrySet()){
                if(getCooldown(entry.getKey()) > 0) abilities.get(entry.getKey()).tickCooldown();
            }
            time = 0;
        }
    }

    public static int getCooldown(){
        return getCooldown(caret);
    }

    public static int getCooldown(AbilityKey key){
        return abilities.get(key).getCooldown();
    }

    public static int getCooldown(int pos){
        if(hotbar.isEmpty()) return 0;
        AbilityKey key = hotbar.get(Math.floorMod(pos, hotbar.size()));
        if(key == null) return 0;
        return getCooldown(key);
    }

    public static int getMaxCooldown(AbilityKey key){
        if(!abilities.containsKey(key)) return 1;
        return Math.max(abilities.get(key).maxCooldown(), 1);
    }

    public static int getMaxCooldown(int pos){
        if(hotbar.isEmpty()) return 1;
        return abilities.get(hotbar.get(Math.floorMod(pos, hotbar.size()))).maxCooldown();
    }

    public static void setHotbarChanged(){
        if(!hotbar.isEmpty()){
            caret = Mth.clamp(caret, 0, hotbar.size() - 1);
        } else {
            caret = 0;
        }
        PacketHandler.INSTANCE.sendToServer(new PlayerSyncHotbarMessage(getHotbar(), getQuickAbility()));
//        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//            cap.getAbilitiesManager().clientHotbar = new ArrayList<>(hotbar);
//        });
    }

    private static void beginCastAnimation(boolean primary){
        if(primary) AbilitiesHotbarHUD.leftCastCooldownAnimation.startAnimation("", false);
        else AbilitiesHotbarHUD.rightCastCooldownAnimation.startAnimation("", false);
    }


    public static boolean configScreenOpenAnimation = false;
    private static float time = 0;
    private static HashMap<AbilityKey, AbilityInfo> abilities = new LinkedHashMap<>();
    //private static ArrayList<String> abilitiesByIndex;
    private static ArrayList<AbilityKey> hotbar = new ArrayList<>();
    private static AbilityKey quickSelect = new AbilityKey();
    /**
     * caret refers to the index in the hotbar -> current selected ability in hotbar
     */
    private static int caret = 0;


    public static void changeCaret(int diff){
        if(hotbar.isEmpty()) return;
        caret = Math.floorMod(caret + diff, hotbar.size());
        if(diff < 0) AbilitiesHotbarHUD.scrollAnimation.startAnimation("scrollRight", false);
        else AbilitiesHotbarHUD.scrollAnimation.startAnimation("scrollLeft", false);
        if(Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(Component.translatable("potioneer.ability_name." + abilities.get(hotbar.get(caret)).descId()), true);
    }

    public static int getCaret(){
        return caret;
    }

    public static AbilityInfo getCurrentAbility(){
        return getAbilityAt(caret);
    }

    public static AbilityInfo getAbilityAt(int caretPos){
        if(hotbar.isEmpty()) return null;
        return abilities.get(hotbar.get(Math.floorMod(caretPos, hotbar.size())));
    }

    public static boolean isEnabled(AbilityKey key){
        return abilities.get(key).isEnabled();
    }

    public static boolean isEnabled(int pos){
        AbilityKey key = hotbar.get(Math.floorMod(pos, hotbar.size()));
        if(key == null) return false;
        return isEnabled(key);
    }

    public static boolean useQuickAbility(Player player){
        if(!hasQuickSelect() && hotbar.isEmpty()) return false;
        //TODO change mode here to reflect the MODE chosen for the quick select
        return useAbility(player, hasQuickSelect() ? quickSelect : hotbar.get(caret), true);
    }

    public static boolean useAbility(Player player, boolean primary){
        if(hotbar.isEmpty()) return false;
        return useAbility(player, hotbar.get(Math.floorMod(caret, hotbar.size())), primary);
    }

    public static boolean useAbility(Player player, AbilityKey key, boolean primary){
        return useAbility(player, key, primary, new CompoundTag());
    }
    public static boolean useAbility(Player player, AbilityKey key, boolean primary, CompoundTag args){
        if(abilities.isEmpty() || key == null || abilities.get(key) == null ) return false;
        Component abilityName = abilities.get(key).getNameComponent();
        if(abilities.get(key).getCooldown() < 0){
            player.sendSystemMessage(Component.translatableWithFallback("message.potioneer.blocked_ability", "%s has been disabled.", abilityName));
            return false;
        }
        int cost = Abilities.getAbilityFactory(key).getCostFunction().apply(key.getSequenceLevel());
        float spir = ClientStatsData.getPlayerSpirituality();
        if(spir < cost){
            player.sendSystemMessage(Component.translatable("message.potioneer.insufficient_spirituality", abilityName));
            return false;
        }
        if(Abilities.getAbilityFactory(key).getHasSecondaryFunction()) beginCastAnimation(primary);
        else if(ClientConfigData.getHotbarOutlines() && primary) beginCastAnimation(true);
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().useAbility(cap, player, key, true, primary, args);
        });
        return true;
    }

    private static void clearAbilitiesOf(boolean clearArtifactsNotAbilities){
        Set<AbilityKey> keysToRemove = abilities.keySet().stream().filter(key -> clearArtifactsNotAbilities == key.isArtifactKey()).collect(Collectors.toSet());
        for(AbilityKey key: keysToRemove){
            abilities.remove(key);
        }
    }

    public static boolean hasAbility(AbilityKey key) {
        return abilities.containsKey(key);
    }

    public static boolean hasAbility(String ablId){
        for(AbilityKey key: abilities.keySet()){
            if(key.isSameAbility(ablId)) return true;
        }
        return false;
    }

    public static ArtifactHolder getArtifact(AbilityKey key) {
        return ClientStatsData.getCapability().get().getAbilitiesManager().getArtifact(key);
    }

    public static class AbilitySpecific{
        private static final HashMap<UUID, Integer> enforcerAuraMap = new HashMap<>();
        public static void addEnforcerAura(UUID id){
            enforcerAuraMap.put(id, 40);
        }

        public static Set<UUID> getEnforcers(){
            enforcerAuraMap.replaceAll((uuid, integer) -> integer-1);
            enforcerAuraMap.entrySet().removeIf((uuidIntegerEntry -> uuidIntegerEntry.getValue() < 0));
            return enforcerAuraMap.keySet();
        }
    }
}
