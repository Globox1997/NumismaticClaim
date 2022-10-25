package net.numismaticclaim.access;

import net.minecraft.entity.passive.VillagerEntity;

public interface VillagerAccess {

    public boolean isNumismaticClaimTrader();

    public void setNumismaticClaimTrader(boolean trader);

    public VillagerEntity getCurrentOfferer();

    public void setCurrentOfferer(VillagerEntity villagerEntity);

    public long getClaimPrice();

    public void setClaimPrice(long price);

}
