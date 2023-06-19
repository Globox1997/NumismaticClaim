package net.numismaticclaim.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.numismaticclaim.NumismaticClaimMain;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.network.NumismaticClaimServerPacket;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements VillagerAccess {

    @Unique
    private boolean isNumismaticClaimTrader = false;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("NumismaticClaimTrader", this.isNumismaticClaimTrader);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.isNumismaticClaimTrader = nbt.getBoolean("NumismaticClaimTrader");
    }

    @Inject(method = "setCustomer", at = @At("HEAD"))
    private void setCustomerMixin(@Nullable PlayerEntity customer, CallbackInfo info) {
        if (customer != null && !customer.getWorld().isClient()) {
            NumismaticClaimServerPacket.writeS2CClaimPricePacket((ServerPlayerEntity) customer);
            NumismaticClaimServerPacket.writeS2COffererPacket((ServerPlayerEntity) customer, (VillagerEntity) (Object) this,
                    this.isNumismaticClaimTrader || NumismaticClaimMain.CONFIG.allow_all_villagers);
        }
    }

    @Override
    public boolean isNumismaticClaimTrader() {
        return this.isNumismaticClaimTrader;
    }

    @Override
    public void setNumismaticClaimTrader(boolean trader) {
        this.isNumismaticClaimTrader = trader;
    }
}
