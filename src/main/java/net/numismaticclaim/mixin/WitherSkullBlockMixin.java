package net.numismaticclaim.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.numismaticclaim.NumismaticClaimMain;
import xaero.pac.common.server.api.OpenPACServerAPI;

@Mixin(WitherSkullBlock.class)
public class WitherSkullBlockMixin {

    @Inject(method = "Lnet/minecraft/block/WitherSkullBlock;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/WitherSkullBlock;getWitherBossPattern()Lnet/minecraft/block/pattern/BlockPattern;"), cancellable = true)
    private static void onPlacedMixin(World world, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo info) {
        if (blockEntity.getWorld().getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldWitherSpawn
                && OpenPACServerAPI.get(world.getServer()).getServerClaimsManager().get(((ServerWorld) blockEntity.getWorld()).getDimensionKey().getValue(), new ChunkPos(pos)) == null)
            info.cancel();
    }
}
