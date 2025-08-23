package net.numismaticclaim.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.numismaticclaim.NumismaticClaimMain;

public record SetOffererPacket(int entityId, boolean isNumismaticClaimTrader) implements CustomPayload {

    public static final CustomPayload.Id<SetOffererPacket> PACKET_ID = new CustomPayload.Id<>(NumismaticClaimMain.identifierOf("set_offerer_packet"));

    public static final PacketCodec<RegistryByteBuf, SetOffererPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.entityId());
        buf.writeBoolean(value.isNumismaticClaimTrader());
    }, buf -> new SetOffererPacket(buf.readInt(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}


