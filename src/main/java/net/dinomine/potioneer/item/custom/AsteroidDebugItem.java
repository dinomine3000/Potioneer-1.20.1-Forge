package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class AsteroidDebugItem extends Item {
    public AsteroidDebugItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        System.out.println("Used on");
        summonAsteroid(pContext.getClickedPos(), pContext.getLevel());
        return super.useOn(pContext);
    }

    private void summonAsteroid(BlockPos pos, Level level){
        AsteroidEntity ent = new AsteroidEntity(ModEntities.ASTEROID.get(), level);
        ent.setToHit(pos, level.random);
        level.addFreshEntity(ent);
    }
}
