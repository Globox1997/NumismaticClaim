package net.numismaticclaim.network;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.numismaticclaim.NumismaticClaimMain;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.player.config.api.IPlayerConfigAPI;
import xaero.pac.common.server.player.config.api.PlayerConfigOptions;

public class NumismaticClaimServerPacket {

    public static final Identifier SET_OFFERER = new Identifier("numismaticclaim", "set_offerer");
    public static final Identifier BUY_CLAIM = new Identifier("numismaticclaim", "buy_claim");
    public static final Identifier CLOSE_SCREEN = new Identifier("numismaticclaim", "close_screen");
    public static final Identifier UPDATE_CLAIM_PRICE = new Identifier("numismaticclaim", "update_claim_price");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(BUY_CLAIM, (server, player, handler, buffer, sender) -> {
            server.execute(() -> {
                if (player != null) {
                    CurrencyComponent playerBalance = ModComponents.CURRENCY.get(player);
                    IPlayerConfigAPI iPlayerConfigAPI = OpenPACServerAPI.get(server).getPlayerConfigs().getLoadedConfig(player.getUuid());
                    int bonusClaims = iPlayerConfigAPI.getEffective(PlayerConfigOptions.BONUS_CHUNK_CLAIMS);
                    long claimPrice = NumismaticClaimMain.CONFIG.claim_price_modificator <= 0.0001f ? NumismaticClaimMain.CONFIG.claim_price
                            : (long) (NumismaticClaimMain.CONFIG.claim_price * (NumismaticClaimMain.CONFIG.claim_price_modificator * bonusClaims + 1));

                    if (playerBalance.getValue() >= claimPrice) {
                        if (bonusClaims < NumismaticClaimMain.CONFIG.max_bonus_claims) {
                            playerBalance.silentModify(-claimPrice);
                            iPlayerConfigAPI.tryToSet(PlayerConfigOptions.BONUS_CHUNK_CLAIMS, bonusClaims + 1);
                            writeS2CClaimPricePacket(player);
                        }
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(CLOSE_SCREEN, (server, player, handler, buffer, sender) -> {
            int entityId = buffer.readInt();
            server.execute(() -> {
                if (player != null && player.world.getEntityById(entityId) != null && player.world.getEntityById(entityId) instanceof VillagerEntity villagerEntity)
                    villagerEntity.setCustomer(null);
            });
        });

    }

    public static void writeS2COffererPacket(ServerPlayerEntity serverPlayerEntity, VillagerEntity villagerEntity, boolean isNumismaticClaimTrader) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(villagerEntity.getId());
        buf.writeBoolean(isNumismaticClaimTrader);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(SET_OFFERER, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CClaimPricePacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        IPlayerConfigAPI iPlayerConfigAPI = OpenPACServerAPI.get(serverPlayerEntity.getServer()).getPlayerConfigs().getLoadedConfig(serverPlayerEntity.getUuid());
        long claimPrice = NumismaticClaimMain.CONFIG.claim_price_modificator <= 0.0001f ? NumismaticClaimMain.CONFIG.claim_price
                : (long) (NumismaticClaimMain.CONFIG.claim_price * (NumismaticClaimMain.CONFIG.claim_price_modificator * iPlayerConfigAPI.getEffective(PlayerConfigOptions.BONUS_CHUNK_CLAIMS) + 1));
        buf.writeLong(claimPrice);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(UPDATE_CLAIM_PRICE, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

}
