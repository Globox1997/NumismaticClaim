package net.numismaticclaim.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.numismaticclaim.NumismaticClaimMain;

public record UpdateClaimPricePacket(long claimPrice) implements CustomPayload {

    public static final CustomPayload.Id<UpdateClaimPricePacket> PACKET_ID = new CustomPayload.Id<>(NumismaticClaimMain.identifierOf("update_claim_price_packet"));

    public static final PacketCodec<RegistryByteBuf, UpdateClaimPricePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeLong(value.claimPrice());
    }, buf -> new UpdateClaimPricePacket(buf.readLong()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}


