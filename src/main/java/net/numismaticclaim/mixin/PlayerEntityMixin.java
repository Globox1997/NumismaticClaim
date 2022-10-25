package net.numismaticclaim.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.numismaticclaim.access.VillagerAccess;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements VillagerAccess {

    public VillagerEntity villagerEntity;
    public long claimPrice;

    @Override
    public void setCurrentOfferer(VillagerEntity villagerEntity) {
        this.villagerEntity = villagerEntity;
    }

    @Override
    public VillagerEntity getCurrentOfferer() {
        return this.villagerEntity;
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
