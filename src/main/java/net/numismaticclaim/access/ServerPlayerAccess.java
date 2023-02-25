package net.numismaticclaim.access;

import java.util.List;

public interface ServerPlayerAccess {

    public void addClaimChunkTicker(int x, int z);

    public List<Integer> getClaimedChunkTicker();
}
