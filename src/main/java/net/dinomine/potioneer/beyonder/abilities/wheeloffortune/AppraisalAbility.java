package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.minecraft.world.entity.LivingEntity;

public class AppraisalAbility extends Ability {

    public AppraisalAbility(int sequence){
        super(sequence);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "appraisal";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()){
            int pageId = PageRegistry.getIdOfPage(PageRegistry.APPRAISAL_PAGE);
            cap.addPage(pageId);
            PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book, pageId), target);
        }
        return false;
    }
}
