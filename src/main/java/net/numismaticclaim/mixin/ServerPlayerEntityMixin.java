package net.numismaticclaim.mixin;

import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numismaticclaim.NumismaticClaimMain;
import net.numismaticclaim.access.ServerPlayerAccess;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerAccess {

    // [claimTicker, claimX, claimZ, ..]
    @Unique
    private ArrayList<Integer> claimedChunkTicker = new ArrayList<Integer>();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        claimedChunkTicker.clear();
        for (int i = 0; i < nbt.getIntArray("ClaimChunkTicker").length; i++) {
            claimedChunkTicker.add(nbt.getIntArray("ClaimChunkTicker")[i]);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putIntArray("ClaimChunkTicker", this.claimedChunkTicker);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (this.age % 20 == 0) {
            if (!claimedChunkTicker.isEmpty()) {
                for (int i = 0; i < claimedChunkTicker.size() / 3; i++) {
                    claimedChunkTicker.set(i * 3, claimedChunkTicker.get(i * 3) - 1);
                    if (claimedChunkTicker.get(i * 3) <= 0) {
                        for (int u = 0; u < 3; u++) {
                            claimedChunkTicker.remove(i * 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addClaimChunkTicker(int x, int z) {
        for (int i = 0; i < this.claimedChunkTicker.size() / 3; i++) {
            if (this.claimedChunkTicker.get(i * 3 + 1) == x && this.claimedChunkTicker.get(i * 3 + 2) == z) {
                return;
            }
        }
        claimedChunkTicker.add(NumismaticClaimMain.CONFIG.chunk_claim_ticker);
        claimedChunkTicker.add(x);
        claimedChunkTicker.add(z);
    }

    @Override
    public List<Integer> getClaimedChunkTicker() {
        return this.claimedChunkTicker;
    }
}
