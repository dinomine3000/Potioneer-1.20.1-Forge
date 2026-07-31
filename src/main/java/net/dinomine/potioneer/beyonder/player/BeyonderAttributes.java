package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BeyonderAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Potioneer.MOD_ID);

    // RangedAttribute takes: (translationKey, defaultValue, minValue, maxValue)
    // .setSyncable(true) makes sure the value is automatically sent to clients!
    public static final RegistryObject<Attribute> RESISTANCE = ATTRIBUTES.register("resistance",
            () -> new RangedAttribute("attribute.potioneer.resistance", 0, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> DEFENSE = ATTRIBUTES.register("defense",
            () -> new RangedAttribute("attribute.potioneer.resistance", 0, 0.0D, 0.75D).setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static double getResistance(LivingEntity entity) {
        var attributeInstance = entity.getAttribute(RESISTANCE.get());
        return attributeInstance != null ? attributeInstance.getValue() : 0.0D;
    }

    public static double getDefense(LivingEntity entity) {
        var attributeInstance = entity.getAttribute(DEFENSE.get());
        return attributeInstance != null ? attributeInstance.getValue() : 0.0D;
    }

    public static double getDefenseMultiplier(LivingEntity entity){
        return 1 - getDefense(entity);
    }
}
