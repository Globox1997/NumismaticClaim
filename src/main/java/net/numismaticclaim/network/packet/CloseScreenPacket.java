package net.numismaticclaim.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.numismaticclaim.NumismaticClaimMain;

public record CloseScreenPacket(int entityId) implements CustomPayload {

    public static final CustomPayload.Id<CloseScreenPacket> PACKET_ID = new CustomPayload.Id<>(NumismaticClaimMain.identifierOf("close_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, CloseScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.entityId());
    }, buf -> new CloseScreenPacket(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}


