package net.dinomine.potioneer.beyonder.client;

import lombok.Getter;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.HUD.AbilitiesHotbarHUD;
import net.dinomine.potioneer.util.misc.ArtifactHolder;
import net.dinomine.potioneer.beyonder.client.screen.BeyonderAbilitiesScreen;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerSyncHotbarMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

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
        return !hotbar.isEmpty() && getCurrentAbility() != null;
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
            Minecraft.getInstance().player.displayClientMessage(Ability.getNameComponent(getCurrentAbility().getDescId()), true);
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
            abilities.put(abl.getInstanceId(), abl);
        }
        updateHotbarOnChange();
        ClientStatsData.getCapability().ifPresent(cap -> {
            if(Minecraft.getInstance().player != null)
                cap.getAbilitiesManager().setAbilitiesOnClient(abilities2, cap, Minecraft.getInstance().player);
            else
                System.out.println("Player is null while trying to set abilities on client side.");
        });
        BeyonderAbilitiesScreen.refreshAbilitiesScreen();
    }

    public static boolean hasQuickSelect(){
        return quickSelect != null;
    }

    public static void addAbilities(List<AbilityInfo> abilities2){
        for(AbilityInfo info: abilities2){
            if(abilities.containsKey(info.getInstanceId())) continue;
            abilities.put(info.getInstanceId(), info);
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
            if(!abilities.containsKey(info.getInstanceId())) continue;
            abilities.remove(info.getInstanceId());
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
            UUID key = abl.getInstanceId();
            if(key == null){
                System.out.println("Warning: tried to update an ability with a null id: " + abl.getDescId());
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
        if(!hotbar.isEmpty()){
            hotbar.removeIf(key -> !abilities.containsKey(key));
        }
        if(hasQuickSelect() && !abilities.containsKey(quickSelect)) quickSelect = null;
        setHotbarChanged();
    }

    public static void setArtifacts(List<ArtifactHolder> artifacts){
        clearAbilitiesOf(true);
        for(ArtifactHolder artifact: artifacts){
            for (AbilityInfo abl : artifact.getAbilitiesInfo(false)) {
                abilities.put(abl.getInstanceId(), abl);
            }
        }
        updateHotbarOnChange();
//        if(changingPath) hotbar = new ArrayList<>();
//        if(changingPath) quickSelect = "";
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
                abilities.put(abl.getInstanceId(), abl);
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
                if(!abilities.containsKey(info.getInstanceId())) continue;
                abilities.remove(info.getInstanceId());
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
                if(abilities.containsKey(info.getInstanceId())) continue;
                abilities.put(info.getInstanceId(), info);
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

    public static boolean isQuickAbility(UUID testId){
        return getQuickAbility() != null && getQuickAbility().equals(testId);
    }

    public static @Nullable UUID getQuickAbility(){
        return quickSelect;
    }

    public static void setQuickAbility(UUID id){
        quickSelect = id;
    }

    public static void setHotbar(ArrayList<UUID> hotbar2) {
        hotbar = hotbar2;
        updateHotbarOnChange();
    }

    public static void tick(float dt){
        if(Minecraft.getInstance().isSingleplayer() && Minecraft.getInstance().isPaused()) return;

        time += dt;
        if(time > 1){
            for(Map.Entry<UUID, AbilityInfo> entry: abilities.entrySet()){
                if(entry.getValue().getCooldown() > 0) entry.getValue().tickCooldown();
            }
            time = 0;
        }
    }

    public static int getCooldown(){
        return getCooldown(caret);
    }

    public static int getCooldown(UUID key){
        return abilities.get(key).getCooldown();
    }

    public static int getCooldown(int pos){
        if(hotbar.isEmpty()) return 0;
        UUID key = hotbar.get(Math.floorMod(pos, hotbar.size()));
        if(key == null) return 0;
        return getCooldown(key);
    }

    public static int getMaxCooldown(UUID key){
        if(!abilities.containsKey(key)) return 1;
        return Math.max(abilities.get(key).getMaxCd(), 1);
    }

    public static int getMaxCooldown(int pos){
        if(hotbar.isEmpty()) return 1;
        return abilities.get(hotbar.get(Math.floorMod(pos, hotbar.size()))).getMaxCd();
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
    private static HashMap<UUID, AbilityInfo> abilities = new LinkedHashMap<>();
    //private static ArrayList<String> abilitiesByIndex;
    @Getter
    private static ArrayList<UUID> hotbar = new ArrayList<>();
    private static UUID quickSelect = null;
    /**
     * caret refers to the index in the hotbar -> current selected ability in hotbar
     */
    @Getter
    private static int caret = 0;


    public static void changeCaret(int diff){
        if(hotbar.isEmpty()) return;
        if(!AbilitiesHotbarHUD.scrollAnimation.isFinished()) return;
        caret = Math.floorMod(caret + diff, hotbar.size());
        if(Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(Ability.getNameComponent(getCurrentAbility().getDescId()), true);

        if(diff < 0)
            AbilitiesHotbarHUD.scrollAnimation.startAnimation("scrollRight", false);
        else
            AbilitiesHotbarHUD.scrollAnimation.startAnimation("scrollLeft", false);
    }

    public static AbilityInfo getCurrentAbility(){
        return getAbilityAt(caret);
    }

    public static AbilityInfo getAbilityAt(int caretPos){
        if(hotbar.isEmpty()) return null;
        return abilities.get(hotbar.get(Math.floorMod(caretPos, hotbar.size())));
    }

    public static boolean isEnabled(UUID key){
        return abilities.get(key).isEnabled();
    }

    public static boolean isEnabled(int pos){
        UUID key = hotbar.get(Math.floorMod(pos, hotbar.size()));
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

    public static boolean useAbility(Player player, UUID key, boolean primary){
        return useAbility(player, key, primary, new CompoundTag());
    }
    public static boolean useAbility(Player player, UUID key, boolean primary, CompoundTag args){
        if(abilities.isEmpty() || key == null || abilities.get(key) == null ) return false;
        AbilityInfo abl = abilities.get(key);
        Component abilityName = abl.getNameComponent();
        if(abl.getCooldown() < 0){
            player.sendSystemMessage(Component.translatableWithFallback("message.potioneer.blocked_ability", "%s has been disabled.", abilityName));
            //return true so the player doesnt accidentaly hit something
            AbilitiesHotbarHUD.disabledHighlightAnimation.startAnimation("", false);
            return true;
        }
        if(abl.getCooldown() != 0)
            return true;
        if(abl.isHasSecondary()) beginCastAnimation(primary);
        else if(ClientConfigData.getHotbarOutlines() && primary) beginCastAnimation(true);
        player.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().useAbility(cap, player, key, true, primary, args);
        });
        return true;
    }

    private static void clearAbilitiesOf(boolean clearArtifactsNotAbilities){
        Set<UUID> keysToRemove = abilities.keySet().stream().filter(key -> clearArtifactsNotAbilities == !abilities.get(key).getArtifactStack().isEmpty()).collect(Collectors.toSet());
        for(UUID key: keysToRemove){
            abilities.remove(key);
        }
    }

    public static boolean hasAbility(ResourceLocation ablId){
        for(AbilityInfo info: abilities.values()){
            if(info.getAbilityId().equals(ablId)) return true;
        }
        return false;
    }

    public static boolean hasAbility(UUID ablId){
        return abilities.containsKey(ablId);
    }

    public static ItemStack getArtifactItem(UUID ablId) {
        return abilities.get(ablId).getArtifactStack();
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
