package net.numismaticclaim.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.network.packet.BuyClaimPacket;
import net.numismaticclaim.network.packet.CloseScreenPacket;
import net.numismaticclaim.network.packet.SetOffererPacket;
import net.numismaticclaim.network.packet.UpdateClaimPricePacket;

public class NumismaticClaimClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SetOffererPacket.PACKET_ID, (payload, context) -> {
            int entityId = payload.entityId();
            boolean isNumismaticClaimTrader = payload.isNumismaticClaimTrader();
            context.client().execute(() -> {
                if (context.player() != null && context.player().getWorld().getEntityById(entityId) instanceof VillagerEntity villagerEntity) {
                    ((VillagerAccess) context.player()).setCurrentOfferer(villagerEntity);
                    ((VillagerAccess) villagerEntity).setNumismaticClaimTrader(isNumismaticClaimTrader);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(UpdateClaimPricePacket.PACKET_ID, (payload, context) -> {
            long claimPrice = payload.claimPrice();
            context.client().execute(() -> {
                if (context.player() != null) {
                    ((VillagerAccess) context.player()).setClaimPrice(claimPrice);
                }
            });
        });
    }

    public static void writeC2SBuyClaimPacket(MinecraftClient client) {
        ClientPlayNetworking.send(new BuyClaimPacket());
    }

    public static void writeC2SCloseScreenPacket(MinecraftClient client, int entityId) {
        ClientPlayNetworking.send(new CloseScreenPacket(entityId));
    }

}
