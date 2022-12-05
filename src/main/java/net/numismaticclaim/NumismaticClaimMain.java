package net.numismaticclaim;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.numismaticclaim.config.NumismaticClaimConfig;
import net.numismaticclaim.network.NumismaticClaimServerPacket;

public class NumismaticClaimMain implements ModInitializer {

    public static final boolean isVillagerQuestsLoaded = FabricLoader.getInstance().isModLoaded("villagerquests");
    public static NumismaticClaimConfig CONFIG = new NumismaticClaimConfig();

    @Override
    public void onInitialize() {
        AutoConfig.register(NumismaticClaimConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(NumismaticClaimConfig.class).getConfig();
        NumismaticClaimServerPacket.init();
    }

}
