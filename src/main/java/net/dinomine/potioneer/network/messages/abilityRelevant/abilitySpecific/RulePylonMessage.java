package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.client.screen.RulePylonScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RulePylonMessage {
    public Map<Rule, Punishment> rulePunishments;
    public List<Law> laws;
    public BlockPos pylonPos;
    public boolean aoj;

    public RulePylonMessage(Map<Rule, Punishment> rulePunishments, List<Law> laws, BlockPos pylonPos, boolean aoj) {
        this.rulePunishments = rulePunishments;
        this.laws = laws;
        this.pylonPos = pylonPos;
        this.aoj = aoj;
    }

    public static void encode(RulePylonMessage msg, FriendlyByteBuf buf) {
        // Write the Map using FriendlyByteBuf#writeMap
        buf.writeMap(
                msg.rulePunishments,
                (buffer, rule) -> buffer.writeUtf(rule.id()),
                (buffer, punishment) -> buffer.writeUtf(punishment.id())
        );
        buf.writeCollection(msg.laws, (buffer, law) -> buffer.writeUtf(law.id()));
        buf.writeBlockPos(msg.pylonPos);
        buf.writeBoolean(msg.aoj);
    }

    public static RulePylonMessage decode(FriendlyByteBuf buffer) {
        return new RulePylonMessage(
                // Read the Map using FriendlyByteBuf#readMap
                buffer.readMap(
                        HashMap::new,
                        buf -> Rule.byId(buf.readUtf()),
                        buf -> Punishment.byId(buf.readUtf())
                ),
                buffer.readCollection(ArrayList::new, buf -> Law.byId(buf.readUtf())),
                buffer.readBlockPos(),
                buffer.readBoolean()
        );
    }

    public static void handle(RulePylonMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientRulePylonHandler.handlePacket(msg));
            } else {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    ServerLevel level = (ServerLevel) player.level();
                    // Server-side logic here
                }
            }
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientRulePylonHandler {
    public static void handlePacket(RulePylonMessage msg) {
        Minecraft.getInstance().setScreen(new RulePylonScreen(msg));
    }
}