package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Potioneer.MOD_ID);

    // RangedAttribute takes: (translationKey, defaultValue, minValue, maxValue)
    // .setSyncable(true) makes sure the value is automatically sent to clients!
    public static final RegistryObject<Attribute> RESISTANCE = ATTRIBUTES.register("resistance",
            () -> new RangedAttribute("attribute.potioneer.resistance", 0, 0.0D, 1000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> STAMINA = ATTRIBUTES.register("stamina",
            () -> new RangedAttribute("attribute.potioneer.stamina", 1, 1.0D, 10D).setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    public static double getResistance(LivingEntity entity) {
        var attributeInstance = entity.getAttribute(RESISTANCE.get());
        return attributeInstance != null ? attributeInstance.getValue() : 0.0D;
    }

    public static double getStamina(LivingEntity entity) {
        var attributeInstance = entity.getAttribute(STAMINA.get());
        return attributeInstance != null ? attributeInstance.getValue() : 5.0D;
    }

    public static double getAttribute(LivingEntity entity, Attribute attribute) {
        return getAttribute(entity, attribute, 0);
    }

    public static double getAttribute(LivingEntity entity, Attribute attribute, double defaultValue) {
        var attributeInstance = entity.getAttribute(attribute);
        return attributeInstance != null ? attributeInstance.getValue() : defaultValue;
    }
}
