package net.numismaticclaim.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.numismaticclaim.NumismaticClaimMain;

public record BuyClaimPacket() implements CustomPayload {

    public static final CustomPayload.Id<BuyClaimPacket> PACKET_ID = new CustomPayload.Id<>(NumismaticClaimMain.identifierOf("buy_claim_packet"));

    public static final PacketCodec<RegistryByteBuf, BuyClaimPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new BuyClaimPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}


