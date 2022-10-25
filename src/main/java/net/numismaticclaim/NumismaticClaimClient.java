package net.numismaticclaim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.numismaticclaim.network.NumismaticClaimClientPacket;

@Environment(EnvType.CLIENT)
public class NumismaticClaimClient implements ClientModInitializer {

    public static final Identifier CLAIM_GUI = new Identifier("numismaticclaim", "textures/gui/claim_gui.png");

    @Override
    public void onInitializeClient() {
        NumismaticClaimClientPacket.init();
    }

}
