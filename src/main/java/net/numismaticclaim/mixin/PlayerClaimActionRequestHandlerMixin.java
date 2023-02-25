package net.numismaticclaim.mixin;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.numismaticclaim.access.ServerPlayerAccess;
import xaero.pac.common.claims.ClaimsManager;
import xaero.pac.common.claims.player.request.ClaimActionRequest;
import xaero.pac.common.server.IServerData;
import xaero.pac.common.server.claims.player.request.PlayerClaimActionRequestHandler;
import xaero.pac.common.server.player.config.IPlayerConfig;
import xaero.pac.common.server.player.data.ServerPlayerData;

@SuppressWarnings("rawtypes")
@Mixin(PlayerClaimActionRequestHandler.class)
public class PlayerClaimActionRequestHandlerMixin {

    @Inject(method = "onReceive", at = @At(value = "INVOKE", target = "Lxaero/pac/common/server/claims/ServerClaimsManager;tryClaimActionOverArea(Lnet/minecraft/util/Identifier;Ljava/util/UUID;IIIIIIILxaero/pac/common/claims/ClaimsManager$Action;Z)Lxaero/pac/common/claims/result/api/AreaClaimResult;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onReceiveMixin(ServerPlayerEntity player, ClaimActionRequest request, CallbackInfo info, ServerPlayerData playerData, boolean shouldServerClaim, UUID playerId,
            IServerData serverData, IPlayerConfig playerConfig, IPlayerConfig usedSubConfig, int subConfigIndex, int fromX, int fromZ) {
        if (!request.isByServer()) {
            if (request.getAction().equals(ClaimsManager.Action.CLAIM)) {
                if (request.getTotalChunks() == 1) {
                    ((ServerPlayerAccess) player).addClaimChunkTicker(request.getLeft(), request.getBottom());
                } else {
                    int xDiff = Math.abs(Math.abs(request.getRight()) - Math.abs(request.getLeft()));
                    int zDiff = Math.abs(Math.abs(request.getTop()) - Math.abs(request.getBottom()));
                    for (int i = 0; i <= xDiff; i++) {
                        for (int u = 0; u <= zDiff; u++) {
                            ((ServerPlayerAccess) player).addClaimChunkTicker(request.getLeft() + i, request.getTop() + u);
                        }
                    }
                }
            } else if (request.getAction().equals(ClaimsManager.Action.UNCLAIM)) {
                List<Integer> list = ((ServerPlayerAccess) player).getClaimedChunkTicker();
                if (!list.isEmpty()) {
                    if (request.getTotalChunks() == 1) {
                        for (int i = 0; i < list.size() / 3; i++) {
                            if (list.get(i * 3 + 1) == request.getLeft() && list.get(i * 3 + 2) == request.getTop()) {
                                int seconds = list.get(i * 3) % 60;
                                int minutes = list.get(i * 3) / 60 % 60;
                                int hours = list.get(i * 3) / 60 / 60;
                                player.sendMessage(Text.translatable("text.numismaticclaim.unclaim", hours, minutes, seconds), false);
                                info.cancel();
                            }
                        }
                    } else {
                        int xDiff = Math.abs(Math.abs(request.getRight()) - Math.abs(request.getLeft()));
                        int zDiff = Math.abs(Math.abs(request.getTop()) - Math.abs(request.getBottom()));
                        for (int i = 0; i <= xDiff; i++) {
                            for (int u = 0; u <= zDiff; u++) {
                                for (int o = 0; o < list.size() / 3; o++) {
                                    if (list.get(o * 3 + 1) == request.getLeft() + i && list.get(o * 3 + 2) == request.getTop() + u) {
                                        int seconds = list.get(o * 3) % 60;
                                        int minutes = list.get(o * 3) / 60 % 60;
                                        int hours = list.get(o * 3) / 60 / 60;
                                        player.sendMessage(Text.translatable("text.numismaticclaim.unclaim", hours, minutes, seconds), false);
                                        info.cancel();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
