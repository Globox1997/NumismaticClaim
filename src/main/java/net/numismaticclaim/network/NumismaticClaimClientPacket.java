package net.numismaticclaim.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.numismaticclaim.access.VillagerAccess;

public class NumismaticClaimClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(NumismaticClaimServerPacket.SET_OFFERER, (client, handler, buf, sender) -> {
            int entityId = buf.readVarInt();
            boolean isNumismaticClaimTrader = buf.readBoolean();
            client.execute(() -> {
                VillagerEntity villagerEntity = (VillagerEntity) client.world.getEntityById(entityId);
                ((VillagerAccess) client.player).setCurrentOfferer(villagerEntity);
                ((VillagerAccess) villagerEntity).setNumismaticClaimTrader(isNumismaticClaimTrader);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(NumismaticClaimServerPacket.UPDATE_CLAIM_PRICE, (client, handler, buf, sender) -> {
            long claimPrice = buf.readLong();
            client.execute(() -> {
                if (client.player != null)
                    ((VillagerAccess) client.player).setClaimPrice(claimPrice);
            });
        });
    }

    public static void writeC2SBuyClaimPacket(MinecraftClient client) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(NumismaticClaimServerPacket.BUY_CLAIM, buf);
        client.getNetworkHandler().sendPacket(packet);
    }

    public static void writeC2SCloseScreenPacket(MinecraftClient client, int entityId) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(NumismaticClaimServerPacket.CLOSE_SCREEN, buf);
        client.getNetworkHandler().sendPacket(packet);
    }

}
