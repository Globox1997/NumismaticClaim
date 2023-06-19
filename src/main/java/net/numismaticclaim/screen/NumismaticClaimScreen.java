package net.numismaticclaim.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.Nullable;

import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.numismaticclaim.NumismaticClaimClient;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.network.NumismaticClaimClientPacket;

public class NumismaticClaimScreen extends Screen implements Tab {

    private ButtonWidget buttonWidget;
    private final MerchantScreenHandler handler;
    private final Text title;
    private int x;
    private int y;

    public NumismaticClaimScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(Text.translatable("numismaticclaim.screen"));
        this.handler = handler;
        this.title = title;
    }

    @Override
    protected void init() {
        this.buttonWidget = this.addDrawableChild(ButtonWidget.builder(Text.translatable("numismaticclaim.screen.buy_claim"), button -> {
            if (this.client != null && this.client.player != null) {
                NumismaticClaimClientPacket.writeC2SBuyClaimPacket(this.client);
                this.client.player.playSound(SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1.0f,
                        (this.client.world.getRandom().nextFloat() - this.client.world.getRandom().nextFloat()) * 0.2f + 1.0f);
            }
        }).dimensions(this.width / 2 - 30, this.height / 2 + 48, 60, 20).build());
        this.x = (this.width - 168) / 2;
        this.y = (this.height - 166) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawTexture(NumismaticClaimClient.CLAIM_GUI, this.width / 2 - 84, this.height / 2 - 83, 0, 20, 168, 166);
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, this.height / 2 - 63, 0xFFFFFF);

        if (this.client != null && this.client.player != null) {
            CurrencyComponent playerBalance = ModComponents.CURRENCY.get(this.client.player);
            long claimPrice = ((VillagerAccess) this.client.player).getClaimPrice();
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.claim_cost"), this.width / 2, this.height / 2 - 43, 0xFFFFFF);
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.claim_cost_2", claimPrice / 10000, (claimPrice % 10000) / 100, claimPrice % 100),
                    this.width / 2, this.height / 2 - 30, 0xFFFFFF);

            long balance = playerBalance.getValue();
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.balance"), this.width / 2, this.height / 2 - 10, 0xFFFFFF);

            // Gold
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.balance_3", playerBalance.getValue() / 10000), this.width / 2, this.height / 2 + 4, 0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            context.drawTexture(NumismaticClaimClient.CLAIM_GUI,
                    this.width / 2 - 13 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_3", playerBalance.getValue() / 10000)) / 2, this.height / 2 + 4, 40, 0, 9, 9);

            // Silver
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.balance_2", (playerBalance.getValue() % 10000) / 100), this.width / 2, this.height / 2 + 17,
                    0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            context.drawTexture(NumismaticClaimClient.CLAIM_GUI,
                    this.width / 2 - 11 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_2", (playerBalance.getValue() % 10000) / 100)) / 2, this.height / 2 + 17, 49, 0, 7,
                    8);

            // Bronze
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("numismaticclaim.screen.balance_1", playerBalance.getValue() % 100), this.width / 2, this.height / 2 + 30, 0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            context.drawTexture(NumismaticClaimClient.CLAIM_GUI,
                    this.width / 2 - 11 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_1", playerBalance.getValue() % 100)) / 2, this.height / 2 + 30, 56, 0, 7, 7);

            if (balance < 10000)
                buttonWidget.active = false;

        }
        DrawTabHelper.drawTab(client, context, this, x, y, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client != null && this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            NumismaticClaimClientPacket.writeC2SCloseScreenPacket(client, ((VillagerAccess) this.client.player).getCurrentOfferer().getId());
            return true;

        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (this.client != null && this.client.player != null) {
            NumismaticClaimClientPacket.writeC2SCloseScreenPacket(client, ((VillagerAccess) this.client.player).getCurrentOfferer().getId());
        }
        super.close();
    }

    @Override
    public @Nullable Class<?> getParentScreenClass() {
        return MerchantScreen.class;
    }

    public MerchantScreenHandler getScreenHandler() {
        return this.handler;
    }

}
