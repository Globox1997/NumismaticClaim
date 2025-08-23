package net.numismaticclaim.network;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.numismaticclaim.NumismaticClaimMain;
import net.numismaticclaim.network.packet.BuyClaimPacket;
import net.numismaticclaim.network.packet.CloseScreenPacket;
import net.numismaticclaim.network.packet.SetOffererPacket;
import net.numismaticclaim.network.packet.UpdateClaimPricePacket;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.player.config.api.IPlayerConfigAPI;
import xaero.pac.common.server.player.config.api.PlayerConfigOptions;

public class NumismaticClaimServerPacket {


    public static void init() {
        PayloadTypeRegistry.playS2C().register(UpdateClaimPricePacket.PACKET_ID, UpdateClaimPricePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SetOffererPacket.PACKET_ID, SetOffererPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(BuyClaimPacket.PACKET_ID, BuyClaimPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(CloseScreenPacket.PACKET_ID, CloseScreenPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BuyClaimPacket.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player() != null) {
                    CurrencyComponent playerBalance = ModComponents.CURRENCY.get(context.player());
                    IPlayerConfigAPI iPlayerConfigAPI = OpenPACServerAPI.get(context.server()).getPlayerConfigs().getLoadedConfig(context.player().getUuid());
                    int bonusClaims = iPlayerConfigAPI.getEffective(PlayerConfigOptions.BONUS_CHUNK_CLAIMS);
                    long claimPrice = NumismaticClaimMain.CONFIG.claim_price_modificator <= 0.0001f ? NumismaticClaimMain.CONFIG.claim_price
                            : (long) (NumismaticClaimMain.CONFIG.claim_price * (NumismaticClaimMain.CONFIG.claim_price_modificator * bonusClaims + 1));

                    if (playerBalance.getValue() >= claimPrice) {
                        if (bonusClaims < NumismaticClaimMain.CONFIG.max_bonus_claims) {
                            playerBalance.silentModify(-claimPrice);
                            iPlayerConfigAPI.tryToSet(PlayerConfigOptions.BONUS_CHUNK_CLAIMS, bonusClaims + 1);
                            writeS2CClaimPricePacket(context.player());
                        }
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(CloseScreenPacket.PACKET_ID, (payload, context) -> {
            int entityId = payload.entityId();
            context.server().execute(() -> {
                if (context.player() != null && context.player().getWorld().getEntityById(entityId) != null && context.player().getWorld().getEntityById(entityId) instanceof VillagerEntity villagerEntity) {
                    villagerEntity.setCustomer(null);
                }
            });
        });

    }

    public static void writeS2COffererPacket(ServerPlayerEntity serverPlayerEntity, VillagerEntity villagerEntity, boolean isNumismaticClaimTrader) {
        ServerPlayNetworking.send(serverPlayerEntity, new SetOffererPacket(villagerEntity.getId(), isNumismaticClaimTrader));
    }

    public static void writeS2CClaimPricePacket(ServerPlayerEntity serverPlayerEntity) {
        IPlayerConfigAPI iPlayerConfigAPI = OpenPACServerAPI.get(serverPlayerEntity.getServer()).getPlayerConfigs().getLoadedConfig(serverPlayerEntity.getUuid());
        long claimPrice = NumismaticClaimMain.CONFIG.claim_price_modificator <= 0.0001f ? NumismaticClaimMain.CONFIG.claim_price
                : (long) (NumismaticClaimMain.CONFIG.claim_price * (NumismaticClaimMain.CONFIG.claim_price_modificator * iPlayerConfigAPI.getEffective(PlayerConfigOptions.BONUS_CHUNK_CLAIMS) + 1));
        ServerPlayNetworking.send(serverPlayerEntity, new UpdateClaimPricePacket(claimPrice));
    }

}
