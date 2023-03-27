package net.numismaticclaim.screen.widget;

import org.jetbrains.annotations.Nullable;

import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.screen.NumismaticClaimScreen;

public class MerchantTab extends InventoryTab {

    public MerchantTab(Text title, @Nullable Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public void onClick(MinecraftClient client) {
        client.setScreen(new MerchantScreen(((NumismaticClaimScreen) client.currentScreen).getScreenHandler(), client.player.getInventory(),
                ((VillagerAccess) client.player).getCurrentOfferer().getDisplayName()));
    }

}
