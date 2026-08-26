package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.misc.*;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.paragon.*;
import net.dinomine.potioneer.beyonder.abilities.redpriest.*;
import net.dinomine.potioneer.beyonder.abilities.tyrant.*;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;
import net.dinomine.potioneer.beyonder.downsides.DummyDownside;
import net.dinomine.potioneer.beyonder.downsides.MobNoisesDownside;
import net.dinomine.potioneer.beyonder.downsides.SlownessDownside;
import net.dinomine.potioneer.beyonder.downsides.tyrant.*;
import net.dinomine.potioneer.beyonder.downsides.wheeloffortune.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class Abilities {
    public static final DeferredRegister<AbilityFactory> ABILITIES =
            DeferredRegister.create(new ResourceLocation(Potioneer.MOD_ID, "ability"), Potioneer.MOD_ID);

    public static final Supplier<IForgeRegistry<AbilityFactory>> REGISTRY = ABILITIES.makeRegistry(
            RegistryBuilder::new);

    public static final RegistryObject<AbilityFactory> COGITATION_WOF = registerAbility("cogitation_wof",
            CogitationAbility::new, 12, 0);

    public static final RegistryObject<AbilityFactory> COGITATION_TY = registerAbility("cogitation_ty",
            CogitationAbility::new, 12, 1);

    public static final RegistryObject<AbilityFactory> COGITATION_MY = registerAbility("cogitation_my",
            CogitationAbility::new, 12, 2);

    public static final RegistryObject<AbilityFactory> COGITATION_RP = registerAbility("cogitation_rp",
            CogitationAbility::new, 12, 3);

    public static final RegistryObject<AbilityFactory> COGITATION_PA = registerAbility("cogitation_pa",
            CogitationAbility::new, 12, 4);

    public static final RegistryObject<AbilityFactory> DUMMY_DOWNSIDE = registerAbility("d_dummy",
            DummyDownside::new, 0, 0);
    public static final RegistryObject<AbilityFactory> SLOWNESS_DOWNSIDE = registerAbility("d_slowness",
            SlownessDownside::new, 0, 0);
    public static final RegistryObject<AbilityFactory> NOISES_DOWNSIDE = registerAbility("d_noises",
            MobNoisesDownside::new, 0, 0);

    public static final RegistryObject<AbilityFactory> CHAOS_LUCK_DOWNSIDE = registerAbility("d_chaos",
            ChaosLuckDownside::new, 0, 0);
    public static final RegistryObject<AbilityFactory> COOLDOWN_DOWNSIDE = registerAbility("d_cooldown",
            CooldownDownside::new, 0, 0);
    public static final RegistryObject<AbilityFactory> FAKE_LAG_DOWNSIDE = registerAbility("d_lag",
            FakeLagDownside::new, 0, 0);
    public static final RegistryObject<AbilityFactory> LUCK_CONSUME_DOWNSIDE = registerAbility("d_luck",
            LuckConsumeDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> LUCK_TREND_DOWNWARDS_DOWNSIDE = registerAbility("d_luck_trend",
            LuckTrendDownwardsDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> RANDOM_VELOCITY_DOWNSIDE = registerAbility("d_velocity",
            RandomVelocityDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> FATE_CAST_DOWNSIDE = registerAbility("d_fate",
            FateCastDownside::new, 0, 0);

    public static RegistryObject<AbilityFactory> AXIS_DOWNSIDE = registerAbility("d_axis",
            AxisDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> CALAMITY_DOWNSIDE = registerAbility("d_calamity",
            CalamityDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> WATER_DOWNSIDE = registerAbility("d_water",
            WaterDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> AURA_DOWNSIDE = registerAbility("d_aura",
            AuraDownside::new, 0, 0);
    public static RegistryObject<AbilityFactory> MIST_DOWNSIDE = registerAbility("d_mist",
            MistDownside::new, 0, 0);


    public static RegistryObject<AbilityFactory> BLANK_OPTIONS = registerAbility("blank_options",
            BlankOptionsAbility::new, 0, 0);

    // -------------------------- WHEEL OF FORTUNE ---------------------------------------------------

    //retweaked
    public static RegistryObject<AbilityFactory> CONJURE_PICKAXE = registerAbility("pick",
            ConjurePickaxeAbility::new, 2, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> MINING_SPEED = registerAbility("mining",
            () -> (new MiningSpeedAbility()).canFlip().enabledOnAcquire(),
            0, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> ZERO_DAMAGE = registerAbility("zero_damage",
            ZeroDamageAbility::new, 8, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> VOID_VISION = registerAbility("void_vision",
            () -> MobEffectPassiveAbility.createAbility(MobEffects.NIGHT_VISION, ignored -> "void_vision")
                    .withAmplifier(1).withPassiveCost(2).withThreshold(5), 9, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> WHEEL_KNOWLEDGE = registerAbility("wheel_knowledge",
            WheelKnowledgeAbility::new, 13, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> MINER_LIGHT = registerAbility("miner_light",
            MinerLightAbility::new, 1, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> FORTUNE_ABILITY = registerAbility("fortune",
            () -> PassiveAbility.createAbility(BeyonderEffects.WHEEL_FORTUNE, (ignored) -> "fortune")
                    .canFlip().withThreshold(0.1f).withCost(5),
            6, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> SILK_TOUCH_ABILITY = registerAbility("silk",
            () -> PassiveAbility.createAbility(BeyonderEffects.WHEEL_SILK, (ignored) -> "silk")
                    .canFlip().withThreshold(0.1f).withCost(5),
            5, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> CALAMITY_INCREASE = registerAbility("calamity",
            () -> PassiveAbility.createAbility(BeyonderEffects.WHEEL_CALAMITY,
                            (level) -> level < 8 ? (level < 6 ? "calamity_3" : "calamity_2" ): "calamity_1")
                    .enabledOnAcquire(),
            4, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> BLOCK_APPRAISAL = registerAbility("block_appraisal",
            BlockAppraisalAbility::new, 15, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> APPRAISAL = registerAbility("appraisal",
            AppraisalAbility::new, 14, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> TARGET_APPRAISAL = registerAbility("target_appraisal",
            EntityAppraisalAbility::new, 16, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> PATIENCE = registerAbility("patience",
            (PatienceAbility::new), 11, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> VELOCITY = registerAbility("velocity",
            VelocityAbility::new, 10, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> MINER_BONE_MEAL = registerAbility("w_bone_meal",
            BoneMealAbility::new, 7, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> FORCE_COOLDOWN_ABILITY = registerAbility("aoe_cooldown",
            CooldownAbility::new, 18, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> GAMBLING = registerAbility("gambling",
            GamblingAbility::new,17, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> LUCK = registerAbility("luck",
            LuckAbility::new, 3, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> FATE = registerAbility("fate",
            FateAbility::new,
            20, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> WHEEL_DIVINATION = registerAbility("wheel_divination",
            WheelDivination::new, 19, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> HALF_COOLDOWN = registerAbility("half_cooldown",
            HalfCooldownAbility::new, 0, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> PHASING = registerAbility("phasing",
            () -> PassiveAbility.createAbility(BeyonderEffects.WHEEL_PHASING, ignored -> "phasing")
                    .canFlip().withCost(20), 21, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> MISFORTUNE = registerAbility("misfortune",
            MisfortuneAbility::new, 23, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> BET = registerAbility("bet",
            BetAbility::new, 22, 0);

    //retweaked
    public static RegistryObject<AbilityFactory> RECORD_DAMAGE = registerAbility("damage_recording",
            DamageRecordingAbility::new, 24, 0);

    // -------------------------- TYRANT ---------------------------------------------------

    //retweaked
    public static RegistryObject<AbilityFactory> WATER_AFFINITY = registerAbility("water_affinity",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_WATER_AFFINITY, (level) -> "water_affinity_" + (level < 9 ? "2" : "1"))
                    .canFlip().enabledOnAcquire(),
            0, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> WATER_SCALES = registerAbility("scales",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_SCALES, lv -> lv < 7 ? "scales_2" : "scales").canFlip().withThreshold(0.1f).withCost(2),
            16, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> OCEAN_ORDER = registerAbility("ocean_order",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_OCEAN_ORDER, ignored -> "ocean_order").enabledOnAcquire().canFlip(),
            0, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> TYRANT_DIVINATION = registerAbility("tyrant_divination",
            DivinationAbility::new, 19, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> TYRANT_WATER_SPELLS = registerAbility("water_spells",
            WaterSpellAbility::new, 3, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> AOJ = registerAbility("area_of_jurisdiction",
            AreaOfJurisdictionAbility::new, 6, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> TYRANT_AURA = registerAbility("aoj_aura",
            AuraAbility::new, 21, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> ARREST = registerAbility("arrest",
            ArrestAbility::new, 18, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> MIST = registerAbility("mist",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_MIST_EFFECT, ignored -> "mist").canFlip().withThreshold(25).withCost(10), 0, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> MIST_BLINKING = registerAbility("mist_blinking",
            MistBlinkingAbility::new, 0, 1);

    //retweaked
    public static RegistryObject<AbilityFactory> SENSE_OF_ORDER = registerAbility("sense_of_order",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_SENSE_OF_ORDER, ignored -> "sense_of_order").enabledOnAcquire().canFlip(),
            17, 1);

    public static RegistryObject<AbilityFactory> AMPLIFICATION = registerAbility("amplification",
            AmplificationAbility::new, 0, 1);

    public static RegistryObject<AbilityFactory> EXILE = registerAbility("exile",
            ExileAbility::new, 0, 1);

    public static RegistryObject<AbilityFactory> CONTRACT = registerAbility("contract",
            ContractAbility::new, 15, 1);

    public static RegistryObject<AbilityFactory> CONTRACT_VIEW = registerAbility("contract_view",
            ContractViewAbility::new, 14, 1);

    public static RegistryObject<AbilityFactory> TYRANT_CALAMITY = registerAbility("tyrant_calamity",
            CalamityAbility::new, 20, 1);

    public static RegistryObject<AbilityFactory> BERSERK_RAGE = registerAbility("berserk",
            () -> PassiveAbility.createAbility(BeyonderEffects.TYRANT_BERSERK, ign -> "berserk").canFlip(),
            0, 1);

    public static RegistryObject<AbilityFactory> ANCHOR_BLINKING = registerAbility("mist_blinking_anchors",
            MistBlinkingAnchorsAbility::new, 0, 1);

    public static RegistryObject<AbilityFactory> RULE_PYLON = registerAbility("rule_pylon",
            RulePylonAbility::new, 0, 1);

    public static RegistryObject<AbilityFactory> PROHIBITION = registerAbility("prohibition",
            ProhibitionAbility::new, 0, 1);

    public static RegistryObject<AbilityFactory> BRIBE = registerAbility("bribe",
            BribeAbility::new, 0, 1);


    // -------------------------- MYSTERY ---------------------------------------------------

    public static RegistryObject<AbilityFactory> MYSTERY_SAP = registerAbility("mystery_sap",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_SAP, (ignored) -> "mystery_sap")
                    .canFlip().enabledOnAcquire(),
            0, 2);

    public static RegistryObject<AbilityFactory> MYSTERY_JAB = registerAbility("mystery_jab",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_JAB,(ignored) -> "mystery_jab")
                    .canFlip().enabledOnAcquire().withCost(5),
            3, 2);

    public static RegistryObject<AbilityFactory> DOOR_OPENING = registerAbility("door_opening",
            DoorOpeningAbility::new, 2, 2);

    public static RegistryObject<AbilityFactory> THEFT = registerAbility("theft",
            TheftAbility::new, 80, 2);

    public static RegistryObject<AbilityFactory> STEP_UP = registerAbility("step_up",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_STEP_UP, (ignored) -> "step_up")
                    .canFlip().enabledOnAcquire(),
            5, 2);

    public static RegistryObject<AbilityFactory> GYMNASTICS = registerAbility("gymnastics",
            GymnasticsAbility::new, 5, 2);

    public static RegistryObject<AbilityFactory> DODGE = registerAbility("dodge",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_DODGE, ign -> "dodge")
                    .canFlip().withCooldown(20*5, PassiveAbility.CooldownTrigger.ON_REMOVE).withCost(5),
            5, 2);

    public static RegistryObject<AbilityFactory> UNSEEN_HAND = registerAbility("unseen_hand",
            UnseenHandAbility::new, 5, 2);

    public static RegistryObject<AbilityFactory> AERIAL_DOMAIN = registerAbility("aerial_domain",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_AERIAL_DOMAIN, ign -> "aerial_domain")
                    .canFlip().enabledOnAcquire(),
            5, 2);

    public static RegistryObject<AbilityFactory> AIR_BULLET = registerAbility("air_bullet",
            AirBulletAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> BLINK = registerAbility("blink",
            BlinkAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> CLONE = registerAbility("clone",
            CloneAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> FAKE_DEATH = registerAbility("fakeout",
            () -> TimedPassiveAbility.createTimed(BeyonderEffects.MYSTERY_FAKEOUT, ign -> "fakeout", ign -> 20*10)
                    .withCost(25, 0).withCooldown(20*60, PassiveAbility.CooldownTrigger.ON_APPLY),
            1, 2);

    public static RegistryObject<AbilityFactory> TRICKS = registerAbility("tricks",
            MagicTricksAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> MAGIC_TOOLS = registerAbility("magic_tools",
            MagicToolsAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> CLEANSE = registerAbility("mystery_cleanse",
            CleanseAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> ELYTRA = registerAbility("elytra",
            ElytraAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> RECORDING = registerAbility("recording",
            RecordingAbility::new, 1, 2);

    public static RegistryObject<AbilityFactory> RANMA = registerAbility("ranma",
            () -> PassiveAbility.createAbility(BeyonderEffects.MYSTERY_RANMA, ign -> "ranma").canFlip().withCooldown(20*10, PassiveAbility.CooldownTrigger.ON_REMOVE).withThreshold(0.1f).withCost(10), 1, 2);


    public static RegistryObject<AbilityFactory> CONCEPT_THEFT = registerAbility("concept_theft",
            ConceptualTheftAbility::new, 1, 2);

    // -------------------------- RED PRIEST ---------------------------------------------------

/*    public static RegistryObject<AbilityFactory> FIRE_SWORD = registerAbility("fire_sword",
            ConjureFireSwordAbility::new, 56, 3, 25);

//        this.info = new AbilityInfo(83, 104, "Fire Guard", 30 + sequence, 1, this.getMaxCooldown(), "fire_aura");
    public static RegistryObject<AbilityFactory> FIRE_AURA = registerAbility("fire_aura",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_FIRE_AURA,
                            (ignored) -> "fire_aura")
                    .canFlip().withThreshold(0.05f).withCost(ignored -> 1),
            104, 3, 0);

    public static RegistryObject<AbilityFactory> FIRE_BALL = registerAbility("fire_ball",
            FireBallAbility::new);

//        this.info = new AbilityInfo(83, 80, "Fire Dance", 30 + sequence, 5, this.getMaxCooldown(), "fire_buff");
    public static RegistryObject<AbilityFactory> FIRE_BUFF = registerAbility("fire_buff",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_FIRE_BUFF,
                            (ignored) -> "fire_buff")
                    .enabledOnAcquire().canFlip().withThreshold(5).withCost(ignored -> 5),
            80, 3, 0);

    public static RegistryObject<AbilityFactory> HEALING = registerAbility("heal",
            HealAbility::new, 152, 3, 20);

    public static RegistryObject<AbilityFactory> LIGHT_BUFF = registerAbility("light_buff",
            LightBuffAbility::new, 224, 3, 5);

    public static RegistryObject<AbilityFactory> MELT_ABILITY = registerAbility("melt",
            MeltAbility::new, 80, 3, 20);

    public static RegistryObject<AbilityFactory> PRIEST_LIGHT = registerAbility("priest_light",
            PriestLightAbility::new, 200, 3, 10);

//        this.info = new AbilityInfo(83, 176, "Purification", 30 + sequence, 5, this.getMaxCooldown(), "purification");
    public static RegistryObject<AbilityFactory> PURIFICATION = registerAbility("purification",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_PURIFICATION,
                            (ignored) -> "purification")
                    .enabledOnAcquire().canFlip().withThreshold(5).withCost(ignored -> 5),
            176, 3, 0);

//        this.info = new AbilityInfo(83, 32, "Weapon Proficiency", 30 + sequence, 0, this.getMaxCooldown(), "weapons_master");
    public static RegistryObject<AbilityFactory> WEAPON_PROFICIENCY = registerAbility("weapon_proficiency",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_PROFICIENCY,
                            (ignored) -> "weapon_proficiency")
                    .enabledOnAcquire().canFlip(),
            32, 3, 0);

    // -------------------------- PARAGON ---------------------------------------------------

    public static RegistryObject<AbilityFactory> ANVIL_GUI = registerAbility("anvil_gui",
            AnvilGuiAbility::new, 104, 4, 10);

    public static RegistryObject<AbilityFactory> CONJURER_CONTAINER = registerAbility("conjure_container",
            ConjurerContainerAbility::new, 176, 4, 0);

    public static RegistryObject<AbilityFactory> CRAFTING_GUI = registerAbility("crafting_gui",
            CraftingGuiAbility::new, 32, 4, 10);

    public static RegistryObject<AbilityFactory> ENDER_CHEST = registerAbility("ender_chest",
            EnderChestAbility::new, 152, 4, 50);

    public static RegistryObject<AbilityFactory> FUEL_CREATE = registerAbility("fuel",
            FuelAbility::new, 80, 4, 0);

    public static RegistryObject<AbilityFactory> CRAFTER_BONE_MEAL = registerAbility("p_bone_meal",
            ParagonBoneMealAbility::new, 128, 4, 2);

    public static RegistryObject<AbilityFactory> REMOVE_ENCHANTMENT = registerAbility("disenchant",
            RemoveEnchantmentAbility::new, 224, 4, 50);

//        this.info = new AbilityInfo(109, 32, "Crafting Spirituality", 40 + sequence, 0, this.getMaxCooldown(), "craft");
    public static RegistryObject<AbilityFactory> CRAFTING_SPIRITUALITY = registerAbility("crafting_spirituality",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.PARAGON_CRAFTING,
                            (ignored) -> "craft")
                    .enabledOnAcquire().canFlip(),
            32, 4, 0);

//        this.info = new AbilityInfo(109, 200, "Xp Cost Reduction", 40 + sequence, 20, this.getMaxCooldown(), "xp_reduce");
    public static RegistryObject<AbilityFactory> XP_COST_REDUCE = registerAbility("xp_reduce",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.PARAGON_XP,
                            (ignored) -> "xp_reduce")
                    .enabledOnAcquire().canFlip(),
            200, 4, 0);

    public static RegistryObject<AbilityFactory> DURABILITY_REGEN = registerAbility("durability_regen",
            DurabilityRegenAbility::new, 56, 4, 3);*/

    public static RegistryObject<AbilityFactory> registerAbility(String ablId, Supplier<Ability> factory, int posY, int pathwayId) {
        ResourceLocation fullId = new ResourceLocation(Potioneer.MOD_ID, ablId);
        return registerAbility(fullId, factory, pathwayId, posY);
    }
    public static RegistryObject<AbilityFactory> registerAbility(ResourceLocation location, Supplier<Ability> factory, int pathwayId, int posY) {
        return ABILITIES.register(location.getPath(), () -> new AbilityFactory(factory, location, posY, pathwayId));
    }

    public static Optional<AbilityFactory> getFactory(ResourceLocation location) {
        if (location == null || REGISTRY.get() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(REGISTRY.get().getValue(location));
    }

    public static @Nullable Ability getFactoryAndConstruct(ResourceLocation ablId, int level, AbilityInfo.Group group){
        Optional<AbilityFactory> optFac = getFactory(ablId);
        if(optFac.isEmpty()) return null;
        return optFac.get().construct(level, group);
    }

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }
}
