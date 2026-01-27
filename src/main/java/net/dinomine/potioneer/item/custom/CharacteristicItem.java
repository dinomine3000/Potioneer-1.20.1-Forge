package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class CharacteristicItem extends Item {
    public CharacteristicItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack createCharacteristic(int sequenceId){
        ItemStack res = new ItemStack(ModItems.CHARACTERISTIC.get());
        CompoundTag tag = new CompoundTag();
        CompoundTag beyonderInfo = new CompoundTag();
        beyonderInfo.putInt("id", sequenceId);
        tag.put(MysticalItemHelper.BEYONDER_TAG_ID, beyonderInfo);
        res.setTag(tag);
        return res;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getPlayer() == null) return InteractionResult.FAIL;
        pContext.getLevel().playSound(pContext.getPlayer(), pContext.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.PLAYERS);
        if(pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), pContext.getLevel(), pContext.getItemInHand().copy(), -1);
        Vec3 pos = pContext.getClickedPos().relative(pContext.getClickedFace()).getCenter().add(0, -0.5f, 0);

        if(pContext.getItemInHand().hasTag() && pContext.getItemInHand().getTag().contains(MysticalItemHelper.BEYONDER_TAG_ID)){
            entity.setSequenceId(pContext.getItemInHand().getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).getInt("id"));
        } else {
            entity.setSequenceId(-1);
        }

        entity.moveTo(pos.x, pos.y, pos.z, pContext.getRotation(), 0);
        pContext.getLevel().addFreshEntity(entity);
        if(!pContext.getPlayer().isCreative()){
            pContext.getPlayer().setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack characteristicStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(characteristicStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pLevel.isClientSide()) return;
        if(characteristicStack.hasTag() && characteristicStack.getTag().contains(MysticalItemHelper.BEYONDER_TAG_ID)
                && PotioneerCommonConfig.ARTIFACT_CONVERSION_CHANCE.get() > 0 && pLevel.random.nextInt(PotioneerCommonConfig.ARTIFACT_CONVERSION_CHANCE.get()) == 1){
            if(pEntity instanceof Player player){
                Optional<LivingEntityBeyonderCapability> cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(cap.isEmpty()) return;
                if(cap.get().getArtifactCooldown() > 0) return;

                ArrayList<ItemStack> items = new ArrayList(player.getInventory().items.stream().toList());
                items.addAll(StreamSupport.stream(player.getArmorSlots().spliterator(), false).toList());


                if(ModList.get().isLoaded("curios")){
                    if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                        Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                        for(ICurioStacksHandler handler: curios.values()){
                            int slots = handler.getSlots();
                            for(int i = 0; i < slots; i++){
                                ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                                items.add(itemStack);
                            }
                        }
                    }
                }
                items.add(player.getOffhandItem());

                for(ItemStack iStack: items){
                    if(MysticalItemHelper.isValidItemForArtifact(iStack)){
                        pLevel.playSound(null, pEntity.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, 1);
                        int pathwaySequenceId = characteristicStack.getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).getInt("id");
                        MysticalItemHelper.generateSealedArtifact(iStack, pathwaySequenceId, pLevel.random);
                        copyMysticismTag(iStack, characteristicStack);
                        pEntity.sendSystemMessage(Component.translatable("potioneer.characteristic.corrupt", iStack.getDisplayName().getString()));
                        characteristicStack.setCount(0);
                        cap.get().putCharacteristicArtifactCooldown(PotioneerCommonConfig.ARTIFACT_CONVERSION_COOLDOWN.get());
                        break;
                    }
                }
            }
        }
    }

    private static void copyMysticismTag(ItemStack target, ItemStack characteristic){
        if(!characteristic.getOrCreateTag().contains(MysticismHelper.mysticismTagId)) return;
        CompoundTag root = target.getOrCreateTag();
        root.put(MysticismHelper.mysticismTagId, characteristic.getTag().getCompound(MysticismHelper.mysticismTagId));
        target.setTag(root);
    }

    @OnlyIn(Dist.CLIENT)
    public static class CharacteristicTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            int seq = -1;
            if(i != 1) seq = -1;
            if(itemStack.hasTag()){
                if(!itemStack.getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).isEmpty()){
                    seq = itemStack.getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID).getInt("id");
                }
            }
            return Pathways.getPathwayBySequenceId(seq).getSequenceColorFromLevel(seq);
        }
    }
}
