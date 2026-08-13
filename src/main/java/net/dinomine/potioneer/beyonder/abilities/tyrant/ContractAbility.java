package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.tyrant.ContractedEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.beyonder.player.PlayerEffectsManager;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.OpenContractScreenMessage;
import net.dinomine.potioneer.server.ServerTokenCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class ContractAbility extends Ability {
    public ContractAbility(int sequenceLevel) {
        super(sequenceLevel);
        withCost(PotioneerAbilityConfig.CONTRACT_COST.get());
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "contract_give";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster, CompoundTag args) {
        if(args.isEmpty()){
            LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 2, 1);
            return startWritingContract(target, caster);
        } else {
            if(cap.getSpirituality() < cost()) return false;
            Entity ent = caster.level().getEntity(args.getInt("target"));
            if(!(ent instanceof LivingEntity target)) return false;
            if(caster.level().isClientSide()) return true;

            boolean nonAlly = PotioneerAbilityConfig.TYRANT_CAN_DO_CONTRACTS_TO_NON_ALLIES.get();
            if(!nonAlly && !AbilityFunctionHelper.areEntitiesAllies(caster, target)) return false;
            if(target instanceof Monster) return false;

            setNextCooldownAs(20*30);
            cap.requestActiveSpiritualityCost(cost());
            if(target instanceof Player){
                UUID token = UUID.randomUUID();
                int duration = 20*60;
                args.putUUID("casterId", caster.getUUID());
                ServerTokenCache.addToken(token, duration, args);
                AbilityFunctionHelper.sendCommandMessage(target, "/beyonderability contract " + token, Component.translatable("message.potioneer.contract_message"), Component.translatable("message.potioneer.contract_clickable", duration/20), Component.translatable("message.potioneer.contract_tooltip"));
            } else {
                ContractAbility.ContractOption condition = ContractAbility.ContractOption.loadFromNbt(args.getCompound("condition")).get();
                ContractAbility.ContractOption reward = ContractAbility.ContractOption.loadFromNbt(args.getCompound("reward")).get();
                ContractedEffect eff = ContractedEffect.getInstance(condition, reward, caster.getUUID());
                Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
                boolean isPresent = optCap.isPresent();
                if(isPresent){
                    BeyonderCapability targetCap = optCap.get();
                    PlayerEffectsManager manager = targetCap.getEffectsManager();
                    manager.addOrReplaceEffect(eff, targetCap, target);
                }
            }
            return true;
        }
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return startWritingContract(target, target);
    }

    private boolean startWritingContract(LivingEntity target, LivingEntity caster){
        setNextCooldownAs(0);
        if(target == null) return false;
        if(caster.level().isClientSide()) return true;
        PacketHandler.sendMessageSTC(new OpenContractScreenMessage(buildOptions(sequenceLevel, target), target.getId(), this.getAbilityKey()), caster);
        return true;
    }

    private List<ContractOption> buildOptions(int sequenceLevel, LivingEntity targetEntity){
        List<String> keys = new ArrayList<>(List.of());
        targetEntity.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().getAbilities().forEach(abl -> {
                if(!abl.getType().equalsIgnoreCase(PlayerAbilitiesManager.AbilityList.INTRINSIC.name())) return;
                keys.add(abl.getAbilityKey().toString());
            });
        });
        if(keys.isEmpty())
            return List.of(ContractOption.DAMAGE_BUFF, ContractOption.HEALTH_BUFF, ContractOption.REGENERATION_BUFF, ContractOption.UNDEAD_BUFF,ContractOption.STAMINA_BUFF,
                    ContractOption.NETHER_COND, ContractOption.UNDEAD_COND, ContractOption.HP_COND, ContractOption.SPIRITUALITY_COND);
        else
            return List.of(ContractOption.DAMAGE_BUFF, ContractOption.HEALTH_BUFF, ContractOption.REGENERATION_BUFF, ContractOption.UNDEAD_BUFF, ContractOption.ABILITY_BUFF.apply(keys),
                    ContractOption.NETHER_COND, ContractOption.UNDEAD_COND, ContractOption.HP_COND, ContractOption.SPIRITUALITY_COND, ContractOption.ABILITY_COND.apply(keys));
    }

    public static class ContractOption {

        public enum OptionType {
            REWARD,
            CONDITION
        }

        private static final Map<String, ContractOption> REGISTRY = new HashMap<>();
        private static final Map<String, Function<List<String>, ContractOption>> FACTORY_REGISTRY = new HashMap<>();

        private final String id;
        private final OptionType type;
        private final String finalDescription;
        private final Component previewComponent;
        private final List<String> arguments;
        private final int argumentsToChoose;
        private boolean valid = true;
        public ContractOption markInvalid(){valid = false;return this;}
        public ContractOption markValid(boolean valid){this.valid = valid;return this;}
        public boolean isValid(){return valid;}

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof ContractOption otherOption)) return false;
            return otherOption.id.equalsIgnoreCase(this.id);
        }

        // --- Constructors ---

        public ContractOption(ContractOption other, List<String> args, boolean valid) {
            this.id = other.id;
            this.type = other.type;
            this.finalDescription = other.finalDescription;
            this.previewComponent = other.previewComponent;
            this.arguments = args.isEmpty() ? other.arguments : List.copyOf(args);
            this.argumentsToChoose = other.argumentsToChoose;
            this.valid = valid;
        }

        public ContractOption(String id, OptionType type, String finalComponent, Component previewComponent, List<String> arguments, int argumentsToChoose) {
            this.id = id;
            this.type = type;
            this.finalDescription = finalComponent;
            this.previewComponent = previewComponent;
            this.arguments = List.copyOf(arguments);
            this.argumentsToChoose = argumentsToChoose;
            markValid(true);
        }

        public ContractOption(String id, OptionType type, Component previewComponent, List<String> arguments, int argumentsToChoose) {
            this(id, type, previewComponent.getString(), previewComponent, arguments, argumentsToChoose);
        }

        public ContractOption(String id, OptionType type, String finalComponent, Component previewComponent, List<String> arguments) {
            this(id, type, finalComponent, previewComponent, arguments, 1);
        }

        public ContractOption(String id, OptionType type, Component previewComponent, List<String> arguments) {
            this(id, type, previewComponent.getString(), previewComponent, arguments, 1);
        }

        public ContractOption(String id, OptionType type, String finalComponent, Component previewComponent) {
            this(id, type, finalComponent, previewComponent, List.of(), 0);
        }

        public ContractOption(String id, OptionType type, Component previewComponent) {
            this(id, type, previewComponent.getString(), previewComponent, List.of(), 0);
        }

        // --- Registry Methods ---

        public static ContractOption register(ContractOption option) {
            REGISTRY.put(option.getId(), option);
            return option;
        }

        public static void registerFactory(String id, Function<List<String>, ContractOption> factory) {
            FACTORY_REGISTRY.put(id, factory);
        }

        public static Optional<ContractOption> create(String id, List<String> args) {
            return create(id, args, true);
        }

        public static Optional<ContractOption> create(String id, List<String> args, boolean valid) {
            if (REGISTRY.containsKey(id)) {
                return Optional.of(new ContractOption(REGISTRY.get(id), args, valid));
            }
            if (FACTORY_REGISTRY.containsKey(id)) {
                return Optional.of(FACTORY_REGISTRY.get(id).apply(args).markValid(valid));
            }
            return Optional.empty();
        }

        public static Optional<ContractOption> create(String id) {
            return create(id, List.of());
        }

        public static Optional<ContractOption> create(String id, boolean valid) {
            return create(id, List.of(), valid);
        }


        // --- Static Option Instances ---

        public static final ContractOption HEALTH_BUFF = register(new ContractOption("hp", OptionType.REWARD, Component.translatable("contract.potioneer.hp_gain")));
        public static final ContractOption DAMAGE_BUFF = register(new ContractOption("dmg", OptionType.REWARD, Component.translatable("contract.potioneer.dmg_gain")));
        public static final ContractOption REGENERATION_BUFF = register(new ContractOption("regeneration", OptionType.REWARD, Component.translatable("contract.potioneer.spir_gain")));
        public static final ContractOption STAMINA_BUFF = register(new ContractOption("stamina", OptionType.REWARD, Component.translatable("contract.potioneer.stamina_gain")));
        public static final ContractOption UNDEAD_BUFF = register(new ContractOption("undead", OptionType.REWARD, Component.translatable("contract.potioneer.undead_gain")));

        public static final ContractOption UNDEAD_COND = register(new ContractOption("undead_cond", OptionType.CONDITION, Component.translatable("contract.potioneer.undead_condition")));
        public static final ContractOption NETHER_COND = register(new ContractOption("nether_cond", OptionType.CONDITION, Component.translatable("contract.potioneer.nether_condition")).markValid(true));
        public static final ContractOption HP_COND = register(new ContractOption("hp_cond", OptionType.CONDITION, Component.translatable("contract.potioneer.hp_condition")));
        public static final ContractOption SPIRITUALITY_COND = register(new ContractOption("spir_cond", OptionType.CONDITION, Component.translatable("contract.potioneer.spir_condition")));

        // --- Static Factory References ---

        public static final Function<List<String>, ContractOption> ABILITY_BUFF = args ->
                new ContractOption("ability", OptionType.REWARD, Component.translatable("contract.potioneer.ability"), args);

        public static final Function<List<String>, ContractOption> ABILITY_COND = args ->
                new ContractOption("ability_cond", OptionType.CONDITION, Component.translatable("contract.potioneer.ability_condition"), args, 3);

        // Register factories dynamically on class loading
        static {
            registerFactory("ability", ABILITY_BUFF);
            registerFactory("ability_cond", ABILITY_COND);
        }

        // --- NBT Serialization ---

        public CompoundTag saveToNbt() {
            return this.saveToNbt(this.arguments);
        }

        public CompoundTag saveToNbt(List<String> arguments) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", this.id);
            tag.putBoolean("valid", valid);

            if (!arguments.isEmpty()) {
                ListTag argsList = new ListTag();
                for (String arg : arguments) {
                    argsList.add(StringTag.valueOf(arg));
                }
                tag.put("args", argsList);
            }

            return tag;
        }

        public static Optional<ContractOption> loadFromNbt(CompoundTag tag) {
            if (!tag.contains("id", Tag.TAG_STRING)) {
                return Optional.empty();
            }

            String id = tag.getString("id");
            boolean valid = tag.getBoolean("valid");
            List<String> args = new ArrayList<>();

            if (tag.contains("args", Tag.TAG_LIST)) {
                ListTag argsList = tag.getList("args", Tag.TAG_STRING);
                for (int i = 0; i < argsList.size(); i++) {
                    args.add(argsList.getString(i));
                }
            }

            return create(id, args, valid);
        }

        // --- Networking (FriendlyByteBuf) ---

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(this.id);
            buf.writeBoolean(this.valid);
            buf.writeVarInt(this.arguments.size());
            for (String arg : this.arguments) {
                buf.writeUtf(arg);
            }
        }

        public static Optional<ContractOption> decode(FriendlyByteBuf buf) {
            String id = buf.readUtf();
            boolean valid = buf.readBoolean();
            int argCount = buf.readVarInt();
            List<String> args = new ArrayList<>(argCount);
            for (int i = 0; i < argCount; i++) {
                args.add(buf.readUtf());
            }

            return create(id, args, valid);
        }

        // --- Getters ---

        public String getId() { return id; }
        public OptionType getType() { return type; }
        public Component getFinalComponent(Object... args) {
            return Component.translatable(finalDescription, args);
        }
        public Component getPreviewComponent() { return previewComponent; }
        public List<String> getArguments() { return arguments; }
        public int getArgumentsToChoose() { return argumentsToChoose; }

        public Component getComponentForArgument(String argument) {
            AbilityKey key = AbilityKey.fromString(argument);
            if (key.isEmpty()) return Component.literal(argument);
            return Ability.getNameComponent(key);
        }

        public boolean isCondition() {
            return this.type == OptionType.CONDITION;
        }

        public boolean isReward() {
            return this.type == OptionType.REWARD;
        }
    }
}
