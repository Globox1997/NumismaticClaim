package net.numismaticclaim.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.numismaticclaim.NumismaticClaimMain;
import xaero.pac.common.event.CommonEvents;
import xaero.pac.common.server.ServerData;
import xaero.pac.common.server.api.OpenPACServerAPI;

@Mixin(CommonEvents.class)
public class CommonEventsMixin {

    private final Text BLOCK_DISABLED = Text.translatable("gui.xaero_claims_protection_block_disabled").formatted(Formatting.RED);

    @Inject(method = "onDestroyBlock", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void onDestroyBlockMixin(WorldAccess world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && ((ServerWorld) world).getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldPlayerBlockDestroy
                && OpenPACServerAPI.get(world.getServer()).getServerClaimsManager().get(((ServerWorld) world).getDimensionKey().getValue(), new ChunkPos(pos)) == null && !player.isCreative()) {
            player.sendMessage(ServerData.from(world.getServer()).getAdaptiveLocalizer().getFor((ServerPlayerEntity) player, BLOCK_DISABLED));
            info.setReturnValue(true);
        }
    }

}
