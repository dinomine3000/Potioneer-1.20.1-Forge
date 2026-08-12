package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.WaterAffinityEffect;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.block.custom.RulePylonBlock;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RulePylonAbility extends Ability implements IAreaOfJurisdiction {

    public RulePylonAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "rule_pylon";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(target.level().isClientSide) return true;
        return AbilityFunctionHelper.placeBlockAtReach(target.level(), cap, target, this::placeBlock);
    }

    public static int getMaxPylons(int sequenceLevel){return 1;}

    private boolean placeBlock(Level level, BlockPos positionToPlace, BeyonderCapability cap, LivingEntity player){
        if(!canAddNewPylon(level.getServer(), player)) return false;
        return doPlacePylon((ServerLevel) level, positionToPlace, player);
    }

    private boolean doPlacePylon(ServerLevel level, BlockPos pos, LivingEntity target){
        return RulePylonBlock.placePylon(level, pos, Set.of(Law.UNDERWATER), target, getSequenceLevel());
    }

    private boolean canAddNewPylon(MinecraftServer server, LivingEntity owner){
        int max = getMaxPylons(sequenceLevel);
        Set<BlockPos> pylons = DimensionChunkSavedData.getAllPylonPositionsOwnedBy(server, owner);
        return pylons.size() < max;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return;
        if(target.tickCount%(20*3) == target.getId()){
            List<BlockPos> center = new ArrayList<>();
            List<Integer> sides = new ArrayList<>();
            List<String> dims = new ArrayList<>();
            DimensionChunkSavedData.collectAojDataForOwner(target.level().getServer(), target, center, sides, dims);
            CompoundTag dataTag = getData();
            CompoundTag aojTag = getCompoundTag(center, sides, dims);
            dataTag.put("aoj", aojTag);
            setData(dataTag, target);
        }
    }

    @Override
    public List<BlockPos> getCenters(String dimensionLocation) {
        return getCentersFromTag(getData().getCompound("aoj"), dimensionLocation);
    }

    @Override
    public List<Integer> getSides(String dimensionLocation) {
        return getSideFromTag(getData().getCompound("aoj"), dimensionLocation);
    }


    public interface Displayable {
        Component title();
        Component tooltip();
    }
    public record Rule(String id, Component title, Component tooltip) implements Displayable{
        private static final Map<String, Rule> REGISTRY = new LinkedHashMap<>();

        public Rule {
            REGISTRY.put(id, this);
        }
        public static final Rule HURT = new Rule("hurt", Component.literal("No Violence"), Component.literal("Forbidden to hurt any entities."));
        public static final Rule BEYONDER = new Rule("beyonder", Component.literal("No Beyonder Abilities"), Component.literal("Forbidden to activate or disable Beyonder abilities."));
        public static final Rule BLOCK_PLACE = new Rule("block_place", Component.literal("No Placing"), Component.literal("Forbidden to place any blocks."));
        public static final Rule BLOCK_BREAK = new Rule("block_break", Component.literal("No Breaking"), Component.literal("Forbidden to mine or destroy any blocks."));
        public static final Rule THEFT = new Rule("theft", Component.literal("No Theft"), Component.literal("Forbidden to perform acts of theft."));
        public static final Rule LUCK = new Rule("luck", Component.literal("No Fortuity"), Component.literal("Forbidden to manipulate luck."));
        public static final Rule AOJ = new Rule("aoj", Component.literal("No Areas of Jurisdiction"), Component.literal("Forbidden to place an Area of Jurisdiction in this area."));
        public static final Rule HOLINESS = new Rule("holiness", Component.literal("No Sacrilege"), Component.literal("Forbidden to use holy powers."));
        public static final Rule ARTIFACT = new Rule("artifact", Component.literal("No Sealed Artifacts"), Component.literal("Forbidden to use or activate sealed artifacts."));
        public static final Rule JUMP = new Rule("jump", Component.literal("No Jumping"), Component.literal("Forbidden to leave the ground via jumping."));
        public static final Rule SPRINT = new Rule("sprint", Component.literal("No Sprinting"), Component.literal("Forbidden to run or sprint."));
        public static final Rule MAIN_HAND_ITEM = new Rule("main_hand_item", Component.literal("No Item Switching"), Component.literal("Forbidden change the item in your main or off hands."));
        public static final Rule WEAPONS = new Rule("main_hand_item", Component.literal("No Item Switching"), Component.literal("Forbidden change the item in your main or off hands."));

        public static Rule byId(String id) {
            Rule rule = REGISTRY.get(id);
            if (rule == null) {
                throw new IllegalArgumentException("Unknown Rule ID: " + id);
            }
            return rule;
        }

        public static Collection<Rule> values() {
            return Collections.unmodifiableCollection(REGISTRY.values());
        }
    }

    public record Punishment(String id, Component title, Component tooltip, Execution execution) implements Displayable {
        private static final Map<String, Punishment> REGISTRY = new LinkedHashMap<>();

        public Punishment {
            REGISTRY.put(id, this);
        }

        public static final Punishment STRIKE = new Punishment("strike", Component.literal("Strike"), Component.literal("Deal purifying damage."), (target, targetCap, tribunal, lvl) -> {
            target.hurt(PotioneerDamage.strike((ServerLevel) target.level(), (LivingEntity) tribunal), 10);
        });
        public static final Punishment DISABLE = new Punishment("disable", Component.literal("Disable Abilities"), Component.literal("Disable Abilities"), (target, targetCap, tribunal, lvl) -> {
            targetCap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_DISABLE_PUNISHMENT.createInstance(lvl, 20*20, true),
                    targetCap, target);
        });
        public static final Punishment DISARM = new Punishment("disarm", Component.literal("Disarm"), Component.literal("Drop items and armor in use"), (target, targetCap, tribunal, lvl) -> {
            Set<ItemStack> targetItems = new HashSet<>();
            targetItems.add(target.getMainHandItem());
            targetItems.add(target.getOffhandItem());
            target.getArmorSlots().forEach(targetItems::add);
            targetItems.forEach(stack -> AbilityFunctionHelper.dropItem(target, stack, false, true));
        });
        public static final Punishment GLOWING = new Punishment("glowing", Component.literal("Glow"), Component.literal("Apply glowing"), (target, targetCap, tribunal, lvl) -> {
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20*30, 1, true, true, true));
        });
        public static final Punishment ARREST = new Punishment("arrest", Component.literal("Arrest"), Component.literal("Arrest rule breaker"), (target, targetCap, tribunal, lvl) -> {
            targetCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_ARREST_RECIPIENT.createInstance(lvl, 20*15, true),
                    targetCap, target);
        });

        public static Punishment byId(String id) {
            Punishment punishment = REGISTRY.get(id);
            if (punishment == null) {
                throw new IllegalArgumentException("Unknown Punishment ID: " + id);
            }
            return punishment;
        }

        public static Collection<Punishment> values() {
            return Collections.unmodifiableCollection(REGISTRY.values());
        }

        @FunctionalInterface
        public interface Execution {
            void execute(LivingEntity target, BeyonderCapability targetCap, @Nullable Entity tribunal, int sequenceLevel);
        }
    }

    public record Law(String id, Component title, Component tooltip, Execution execution) implements Displayable{
        private static final Map<String, Law> REGISTRY = new LinkedHashMap<>();

        public Law {
            REGISTRY.put(id, this);
        }

        public static final Law UNDERWATER = new Law("underwater", Component.literal("Breathe Underwater"), Component.literal("guess"), be -> {
            Set<LivingEntity> hits = new HashSet<>();
            for (ChunkPos pos : be.getClaimedChunks()) {
                hits.addAll(AbilityFunctionHelper.getLivingEntitiesInChunk(be.getLevel(), pos));
            }
            hits.forEach(ent -> {
                WaterAffinityEffect eff = (WaterAffinityEffect) BeyonderEffects.getEffect(BeyonderEffects.TYRANT_WATER_AFFINITY.getEffectId())
                        .createInstance(be.getSequenceLevel(), 0, 20, true);
                ent.getCapability(CapProvider.BEYONDER_STATS)
                        .ifPresent(cap -> cap.getEffectsManager().addOrRefreshEffect(eff, cap, ent));
            });
        });

        public static final Law DAMAGE_AMP = new Law("damage_amp", Component.literal("Damage Amplification"), Component.literal("Every instance of damage is amplified"), be -> {});
        public static final Law DAMAGE_RED = new Law("damage_red", Component.literal("Damage Reduction"), Component.literal("Every instance of damage is weakened"), be -> {});
        public static final Law HEALING = new Law("healing", Component.literal("Healing"), Component.literal("Every entity is healed"), be -> {
            if(be.tickCount%20 != 0) return;
            be.getEntities().forEach(ent ->{
                if(ent.isInvertedHealAndHarm()) ent.hurt(ent.damageSources().magic(), 2);
                else ent.heal(2);
            });
        });
        public static final Law DAMAGE = new Law("damage", Component.literal("Constant Damage"), Component.literal("Every entity is constantly taking damage"), be -> {
            if(be.tickCount%20 != 0) return;
            if(be.getLevel().isClientSide()) return;
            be.getEntities().forEach(ent ->{
                ent.hurt(PotioneerDamage.law((ServerLevel) be.getLevel(), be.getOwner()), 1);
            });
        });
        public static final Law MINING = new Law("mining", Component.literal("Mining Weakness"), Component.literal("Every entity is affected by mining fatigue"), be -> {
            if(be.tickCount%20 != 0) return;
            be.getEntities().forEach(ent ->  {
                ent.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20*2 + 10, be.getSequenceLevel() < 5 ? 3 : 1, false, true, true));
            });
        });

        public static Law byId(String id) {
            Law law = REGISTRY.get(id);
            if (law == null) {
                throw new IllegalArgumentException("Unknown Law ID: " + id);
            }
            return law;
        }

        public static Collection<Law> values() {
            return Collections.unmodifiableCollection(REGISTRY.values());
        }

        @FunctionalInterface
        public interface Execution {
            void execute(RulePylonBlockEntity be);
        }
    }
}
