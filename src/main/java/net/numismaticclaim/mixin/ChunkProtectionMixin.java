package net.numismaticclaim.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.numismaticclaim.NumismaticClaimMain;
import xaero.pac.common.parties.party.IPartyPlayerInfo;
import xaero.pac.common.parties.party.member.IPartyMember;
import xaero.pac.common.server.IServerData;
import xaero.pac.common.server.claims.IServerClaimsManager;
import xaero.pac.common.server.claims.protection.ChunkProtection;
import xaero.pac.common.server.parties.party.IServerParty;

import java.util.List;

@Mixin(ChunkProtection.class)
public abstract class ChunkProtectionMixin<CM extends IServerClaimsManager<?, ?, ?>, M extends IPartyMember, I extends IPartyPlayerInfo, P extends IServerParty<M, I, ?>> {

    @Shadow
    @Final
    @Mutable
    private CM claimsManager;

    @Shadow
    @Final
    @Mutable
    private Text BLOCK_DISABLED;

    @Shadow
    @Final
    @Mutable
    private Text ITEM_DISABLED_MAIN;

    @Inject(method = "onBlockInteraction(Lxaero/pac/common/server/IServerData;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZZ)Z", at = @At(value = "RETURN"), cancellable = true)
    private void onBlockInteractionMixin(IServerData<CM, P> serverData, BlockState blockState, Entity entity, Hand hand, ItemStack heldItem, ServerWorld world, BlockPos pos, Direction direction,
            boolean breaking, boolean messages, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && !breaking && entity instanceof PlayerEntity playerEntity && world.getRegistryKey() == World.OVERWORLD
                && !NumismaticClaimMain.CONFIG.overworldPlayerSpecificItemUse && claimsManager.get(world.getDimensionKey().getValue(), new ChunkPos(pos)) == null && !playerEntity.isCreative()) {
            Item item = playerEntity.getStackInHand(hand).getItem();
            if (item instanceof BlockItem || item instanceof HoeItem || item instanceof AxeItem || item instanceof ShovelItem || item instanceof FlintAndSteelItem || item instanceof BoneMealItem) {
                playerEntity.sendMessage(serverData.getAdaptiveLocalizer().getFor((ServerPlayerEntity) playerEntity, BLOCK_DISABLED));
                info.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onCropTrample", at = @At("TAIL"), cancellable = true)
    private void onCropTrampleMixin(IServerData<CM, P> serverData, Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && entity instanceof PlayerEntity playerEntity && entity.world.getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldCropTrample
                && claimsManager.get(entity.world.getDimensionKey().getValue(), new ChunkPos(pos)) == null && !playerEntity.isCreative())
            info.setReturnValue(true);
    }

    @Inject(method = "onRaidSpawn", at = @At("RETURN"), cancellable = true)
    private void onRaidSpawnMixin(IServerData<CM, P> serverData, ServerWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && world.getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldRaids)
            info.setReturnValue(true);
    }

    @Inject(method = "onBucketUse", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onBucketUseMixin(IServerData<CM, ?> serverData, Entity entity, ServerWorld world, HitResult hitResult, ItemStack itemStack, CallbackInfoReturnable<Boolean> info, BlockPos pos,
            Direction direction) {
        if (!info.getReturnValue() && entity instanceof PlayerEntity playerEntity && world.getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldPlayerSpecificItemUse
                && claimsManager.get(world.getDimensionKey().getValue(), new ChunkPos(pos)) == null && !playerEntity.isCreative()) {
            playerEntity.sendMessage(serverData.getAdaptiveLocalizer().getFor((ServerPlayerEntity) playerEntity, ITEM_DISABLED_MAIN));
            info.setReturnValue(true);
        }
    }

    @Inject(method = "Lxaero/pac/common/server/claims/protection/ChunkProtection;onMobGrief(Lxaero/pac/common/server/IServerData;Lnet/minecraft/entity/Entity;ZZZ)Z", at = @At("RETURN"), cancellable = true)
    private void onMobGriefMixin(IServerData<CM, P> serverData, Entity entity, boolean blocks, boolean entities, boolean items, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && entity instanceof PlayerEntity && entity.world.getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldMobGrief
                && claimsManager.get(entity.world.getDimensionKey().getValue(), entity.getChunkPos()) == null)
            info.setReturnValue(true);
    }

    @Inject(method = "onExplosionDetonate", at = @At("HEAD"), cancellable = true)
    private void onExplosionDetonateMixin(IServerData<CM, P> serverData, ServerWorld world, Explosion explosion, List<Entity> affectedEntities, List<BlockPos> affectedBlocks, CallbackInfo info) {
        if (!affectedBlocks.isEmpty() && world.getRegistryKey() == World.OVERWORLD && !NumismaticClaimMain.CONFIG.overworldExplosion
                && claimsManager.get(world.getDimensionKey().getValue(), new ChunkPos(affectedBlocks.get(0))) == null)// explosion.getCausingEntity() != null &&
            affectedBlocks.clear();
    }

}
