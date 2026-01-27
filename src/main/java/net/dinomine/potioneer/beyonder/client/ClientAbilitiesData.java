package net.dinomine.potioneer.beyonder.client;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.ArtifactHolder;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.config.PotioneerClientConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerSyncHotbarMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientAbilitiesData {
    public static ArrayList<AbilityInfo> getAbilities() {
        return new ArrayList<>(abilities.values().stream().toList());
    }

    public static void setShowHotbar(boolean val){
        if(configScreenOpenAnimation) return;
        if(!showHotbar && val && !hotbar.isEmpty() && abilities.get(hotbar.get(caret)) != null){
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("potioneer.ability_name." + abilities.get(hotbar.get(caret)).descId()), true);
            openAnimation = true;
            openingAnimationPercent = 0;
            showHotbar = true;
        }
        if(openAnimation && !val){
            openAnimation = false;
            openingAnimationPercent = 1;
        }
    }

    public static void showHotbarOnConfigScreen(boolean show){
        if(hotbar.isEmpty()) return;
        if(!configScreenOpenAnimation && show){
            configScreenOpenAnimation = true;
            openingAnimationPercent = 0;
        }
        if(configScreenOpenAnimation && !show){
            configScreenOpenAnimation = false;
            openingAnimationPercent = 1;
        }
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
        scaleAnimationTime += dt;
        if(scaleAnimationTime > 5){
            scaleAnimationTime = -5;
        }
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

    private static float castPrimary = 0f;
    private static float castSecondary = 0f;
    private static final float maxPrimaryCooldown = 15f;

    public static float getPercent(boolean primary){
        return (primary ? castPrimary : castSecondary) / maxPrimaryCooldown;
    }

    private static void beginCastAnimation(boolean primary){
        if(primary) castPrimary = maxPrimaryCooldown;
        else castSecondary = maxPrimaryCooldown;
    }


    public static void animationTick(float dt){
        if(animationTime > 0) animationTime = Math.max(animationTime - dt, 0);
        if(animationTime < 0) animationTime = Math.min(animationTime + dt, 0);
        float diff = (openAnimation || configScreenOpenAnimation) ? dt : -dt;
        openingAnimationPercent = Mth.clamp(openingAnimationPercent + diff/10, 0, 1);
        if(openingAnimationPercent <= 0){
            showHotbar = false;
        }
        if(castPrimary > 0f) castPrimary = Math.max(castPrimary - dt, 0);
        if(castSecondary > 0f) castSecondary = Math.max(castSecondary - dt, 0);
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) return;
        disabledCountdown -= dt*disableFps/60f;
        if(disabledCountdown < 0){
            disabledCountdown = 1;
            disabledPosition = level.random.nextInt(12);
        }
    }

    public static boolean openAnimation = false;
    public static boolean configScreenOpenAnimation = false;
    public static float openingAnimationPercent = 0;
    public static final float maxAnimationtime = 0.65f*20;
    public static float animationTime = 0;
    private static float time = 0;
    public static float scaleAnimationTime = 0;
    private static HashMap<AbilityKey, AbilityInfo> abilities = new LinkedHashMap<>();
    //private static ArrayList<String> abilitiesByIndex;
    private static ArrayList<AbilityKey> hotbar = new ArrayList<>();
    private static AbilityKey quickSelect = new AbilityKey();
    /**
     * caret refers to the index in the hotbar -> current selected ability in hotbar
     */
    private static int caret = 0;
    public static boolean showHotbar = false;

    public static int getDisabledPosition() {
        return disabledPosition;
    }

    private static int disabledPosition = 0;
    private static float disabledCountdown = 0;
    private static final int disableFps = 24;

    public static void changeCaret(int diff){
        if(animationTime != 0 || hotbar.isEmpty()) return;
        caret = Math.floorMod(caret + diff, hotbar.size());
        animationTime = diff < 0 ? -maxAnimationtime : maxAnimationtime;
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
        if(Abilities.getAbilityFactory(key).getHasSecondaryFunction())
            beginCastAnimation(primary);
        else if(ClientConfigData.getHotbarOutlines() && primary) beginCastAnimation(true);
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//                    System.out.println(caret);
//                    ClientStatsData.setSpirituality(ClientStatsData.getPlayerSpirituality() - abilities.get(caret).cost());
            cap.getAbilitiesManager().useAbility(cap, player, key, true, primary);
//            abilities.get(cAblId).setEnabled(false);
        });
//        if(cooldowns.get(cAblId) == 0){
//        } else if(cooldowns.get(cAblId) > 0){
//            player.sendSystemMessage(Component.translatable("potioneer.message.ability_cooldown"));
//        } else {
//            player.sendSystemMessage(Component.translatable("potioneer.message.ability_disabled"));
//        }
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

    public static ArtifactHolder getArtifact(AbilityKey key) {
        return ClientStatsData.getCapability().get().getAbilitiesManager().getArtifact(key);
    }
}
