package net.numismaticclaim.screen.widget;

import org.jetbrains.annotations.Nullable;

import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.screen.NumismaticClaimScreen;

public class ClaimTab extends InventoryTab {

    public ClaimTab(Text title, @Nullable Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public boolean shouldShow(MinecraftClient client) {
        return ((VillagerAccess) client.player).getCurrentOfferer() != null && ((VillagerAccess) ((VillagerAccess) client.player).getCurrentOfferer()).isNumismaticClaimTrader();
    }

    @Override
    public void onClick(MinecraftClient client) {
        client.setScreen(new NumismaticClaimScreen((MerchantScreenHandler) ((HandledScreen<?>) client.currentScreen).getScreenHandler(), client.player.getInventory(),
                Text.translatable("numismaticclaim.screen")));
    }

}
