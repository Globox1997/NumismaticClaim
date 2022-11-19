package net.numismaticclaim.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.numismaticclaim.access.VillagerAccess;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements VillagerAccess {

    @Unique
    public VillagerEntity offerer;
    @Unique
    public long claimPrice;

    @Override
    public void setCurrentOfferer(VillagerEntity villagerEntity) {
        this.offerer = villagerEntity;
    }

    @Override
    public VillagerEntity getCurrentOfferer() {
        return this.offerer;
    }

    @Override
    public void setClaimPrice(long price) {
        this.claimPrice = price;
    }

    @Override
    public long getClaimPrice() {
        return this.claimPrice;
    }

}
