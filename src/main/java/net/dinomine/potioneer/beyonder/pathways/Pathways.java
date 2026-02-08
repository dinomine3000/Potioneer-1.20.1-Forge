package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class Pathways {
    public static final DeferredRegister<BeyonderPathway> PATHWAYS =
            DeferredRegister.create(new ResourceLocation(Potioneer.MOD_ID, "pathway"), Potioneer.MOD_ID);

    public static final Supplier<IForgeRegistry<BeyonderPathway>> REGISTRY = PATHWAYS.makeRegistry(
            () -> new RegistryBuilder<BeyonderPathway>().setDefaultKey(new ResourceLocation(Potioneer.MOD_ID, "beyonderless")));

    public static final RegistryObject<BeyonderPathway> BEYONDERLESS =
            registerPathway("-1", BeyonderlessPathway::new);

    public static final RegistryObject<BeyonderPathway> WHEEL_OF_FORTUNE =
            registerPathway("0", WheelOfFortunePathway::new);

    public static final RegistryObject<BeyonderPathway> TYRANT =
            registerPathway("1", TyrantPathway::new);

    public static final RegistryObject<BeyonderPathway> MYSTERY =
            registerPathway("2", MysteryPathway::new);

    public static final RegistryObject<BeyonderPathway> RED_PRIEST =
            registerPathway("3", RedPriestPathway::new);

    public static final RegistryObject<BeyonderPathway> PARAGON =
            registerPathway("4", ParagonPathway::new);

    public static int getRandomPathwayId(RandomSource random){
        int res = -1;
        while(res < 0){
            res = getPathwayIdFromPathway(PATHWAYS.getEntries().stream().toList().get(random.nextInt(PATHWAYS.getEntries().size())).get());
        }
        return res;
    }

    public static <T extends BeyonderPathway> RegistryObject<T> registerPathway(String name, Supplier<T> abl){
        return PATHWAYS.register(name, abl);
    }

    public static BeyonderPathway getPathwayBySequenceId(int pathwayId){
        return getPathwayById(new ResourceLocation(Potioneer.MOD_ID, String.valueOf(Math.floorDiv(pathwayId, 10))));
    }

    public static BeyonderPathway getPathwayById(int pathwayId){
        return getPathwayById(new ResourceLocation(Potioneer.MOD_ID, String.valueOf(pathwayId)));
    }

    public static List<BeyonderPathway> getAllPathways(){
        return PATHWAYS.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static BeyonderPathway getPathwayById(ResourceLocation id){
        return REGISTRY.get().getValue(id);
    }

    public static int getPathwayIdFromPathway(BeyonderPathway pathway){
        ResourceLocation key = REGISTRY.get().getKey(pathway);
        if(key == null) return -1;
        return Integer.parseInt(key.getPath());
    }

    public static void register(IEventBus eventBus){
        PATHWAYS.register(eventBus);
    }
}
