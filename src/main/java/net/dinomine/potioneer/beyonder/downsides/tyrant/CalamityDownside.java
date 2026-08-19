package net.dinomine.potioneer.beyonder.downsides.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.CalamityAbility;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class CalamityDownside extends Downside {

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        doChaos(0.1f, cap, target);
        setNextCooldownAs(20*5);
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        doChaos(0.0001f, cap, target);
    }

    private void doChaos(float chance, BeyonderCapability cap, LivingEntity target){
        if(cap.getLuckManager().passesLuckCheck(chance, 0, 10, target.getRandom())) return;
        int chaos = target.getRandom().nextInt(3);
        switch (chaos){
            case 0:
                CalamityAbility.doMeteor(target);
                break;
            case 1:
                CalamityAbility.doRain(target, getSequenceLevel());
                break;
            case 2:
                CalamityAbility.doThunder(target.level().isThundering(), target, getSequenceLevel(), cap, true);
                break;
        }
        target.level().playSound(null, target.getOnPos(), ModSounds.UNLUCK.get(), SoundSource.AMBIENT);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_downside";
    }
}
