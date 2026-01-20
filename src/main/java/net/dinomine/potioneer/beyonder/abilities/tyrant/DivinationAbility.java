package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.ArrayList;
import java.util.HashMap;

public class DivinationAbility extends PassiveAbility {
    public static HashMap<BlockState, Float> banners_chances = new HashMap<>();
    public static ArrayList<BlockState> banners = new ArrayList<>();
    static {
        banners_chances.put(Blocks.BLACK_BANNER.defaultBlockState(), 0.1f);
        banners.add(Blocks.BLACK_BANNER.defaultBlockState());
        banners_chances.put(Blocks.RED_BANNER.defaultBlockState(), 0.2f);
        banners.add(Blocks.RED_BANNER.defaultBlockState());
        banners_chances.put(Blocks.BLUE_BANNER.defaultBlockState(), 0.4f);
        banners.add(Blocks.BLUE_BANNER.defaultBlockState());
        banners_chances.put(Blocks.GREEN_BANNER.defaultBlockState(), 0.5f);
        banners.add(Blocks.GREEN_BANNER.defaultBlockState());
        banners_chances.put(Blocks.YELLOW_BANNER.defaultBlockState(), 0.6f);
        banners.add(Blocks.YELLOW_BANNER.defaultBlockState());
        banners_chances.put(Blocks.PINK_BANNER.defaultBlockState(), 0.8f);
        banners.add(Blocks.PINK_BANNER.defaultBlockState());
        banners_chances.put(Blocks.WHITE_BANNER.defaultBlockState(), 0.9f);
        banners.add(Blocks.WHITE_BANNER.defaultBlockState());
    }

    public DivinationAbility(int sequence){
//        this.info = new AbilityInfo(31, 56, "Divination", 10 + sequence, 0, this.getMaxCooldown(), "divination");
        super(sequence, BeyonderEffects.MISC_DIVINATION, level -> "divination");
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;


        if(target instanceof Player player)
            PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Divination), player);
        return true;
    }

    public static boolean doMapDivination(ArrayList<BlockPos> positions, ItemStack map, MapItemSavedData mapItemSavedData, Level level, ArrayList<Boolean> check, float correctChance, PlayerLuckManager luckManager){
        if(check.size() != positions.size()) return false;
        boolean flag = false;

        for(int i = 0; i < positions.size(); i++){
            ArrayList<Float> probabilities = new ArrayList<>();
            for(int j = 0; j < 7; j++){
                //for each banner index, calculates the probability of choosing that banner given a successful (or not) divination result
                float likelihood = banners_chances.get(banners.get(i));
                probabilities.add(PotioneerMathHelper.ProbabilityHelper.bayes(check.get(i) ? likelihood : 1 - likelihood,
                                1f/7f,
                                check.get(i) ? correctChance : 1 - correctChance)
                );
            }
            //gets the banner index based on the probabilities.
            //since the first banners
            float playerLuck = luckManager.checkLuck((float)Math.random());
            int idx = PotioneerMathHelper.ProbabilityHelper.pickRandom(probabilities, check.get(i) ? playerLuck : 1-playerLuck);
            boolean aux = drawBannerOnMap(positions.get(i), mapItemSavedData, level, banners.get(idx));
            if(!aux) continue;
            flag = true;
        }
        if(flag){
            MapItem.lockMap(level, map);
            System.out.println("Spirituality conusmed yadadadada. in divination ability class");
        }
        return flag;
    }

    private static boolean drawBannerOnMap(BlockPos position, MapItemSavedData mapItemSavedData, Level level, BlockState bannerBlock){
        if (mapItemSavedData == null || mapItemSavedData.locked) return false;

        BlockPos pos = position;
        BlockPos banner_pos = placeAtNearestAirHorizontal(level, pos, bannerBlock, 5);
        if(banner_pos == null) return false;
        mapItemSavedData.toggleBanner(level, banner_pos);
        level.setBlock(banner_pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE);
        return isBannerInMap(mapItemSavedData.getBanners(), banner_pos);
    }

    private static BlockPos placeAtNearestAirHorizontal(Level level, BlockPos center, BlockState blockToPlace, int maxRadius) {
        int fixedY = 319; // Fixed height

        BlockPos base = new BlockPos(center.getX(), fixedY, center.getZ());

        for (int r = 0; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = base.offset(dx, 0, dz);
                    if (level.isInWorldBounds(pos) && level.isEmptyBlock(pos)) {
                        level.setBlock(pos, blockToPlace, 3);
                        return pos;
                    }
                }
            }
        }

        return null; // No air block found in the horizontal area
    }

    private static boolean isBannerInMap(Iterable<MapBanner> banners, BlockPos pos){
        for(MapBanner banner : banners){
            if (banner.getPos().equals(pos)) return true;
        }
        return false;
    }
}
