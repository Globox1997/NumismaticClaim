package net.numismaticclaim;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.registry.TabRegistry;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.numismaticclaim.network.NumismaticClaimClientPacket;
import net.numismaticclaim.screen.NumismaticClaimScreen;
import net.numismaticclaim.screen.widget.*;

@Environment(EnvType.CLIENT)
public class NumismaticClaimClient implements ClientModInitializer {

    public static final Identifier CLAIM_GUI = new Identifier("numismaticclaim", "textures/gui/claim_gui.png");
    public static final Identifier CLAIM_TAB_ICON = new Identifier("numismaticclaim", "textures/gui/claim_tab_icon.png");
    public static final Identifier TRADE_TAB_ICON = new Identifier("numismaticclaim", "textures/gui/trade_tab_icon.png");

    @Override
    public void onInitializeClient() {
        NumismaticClaimClientPacket.init();
        TabRegistry.registerOtherTab(new MerchantTab(Text.translatable("merchant.trades"), TRADE_TAB_ICON, 0, MerchantScreen.class), MerchantScreen.class);
        TabRegistry.registerOtherTab(new ClaimTab(Text.translatable("numismaticclaim.screen"), CLAIM_TAB_ICON, 1, NumismaticClaimScreen.class), MerchantScreen.class);
    }

}
