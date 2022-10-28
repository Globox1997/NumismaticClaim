package net.numismaticclaim.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.numismaticclaim.NumismaticClaimClient;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.network.NumismaticClaimClientPacket;

public class NumismaticClaimScreen extends Screen {

    private ButtonWidget buttonWidget;
    private boolean switchScreen;
    private final MerchantScreenHandler handler;
    private final PlayerInventory inventory;
    private final Text title;

    public NumismaticClaimScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(Text.translatable("numismaticclaim.screen"));
        this.handler = handler;
        this.inventory = inventory;
        this.title = title;
    }

    @Override
    protected void init() {
        this.buttonWidget = this.addDrawableChild(new ButtonWidget(this.width / 2 - 30, this.height / 2 + 48, 60, 20, Text.translatable("numismaticclaim.screen.buy_claim"), button -> {
            if (this.client != null && this.client.player != null) {
                NumismaticClaimClientPacket.writeC2SBuyClaimPacket(this.client);
                this.client.player.playSound(SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1.0f,
                        (this.client.world.getRandom().nextFloat() - this.client.world.getRandom().nextFloat()) * 0.2f + 1.0f);
            }
        }));

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);

        this.drawTexture(matrices, this.width / 2 - 84, this.height / 2 - 83, 0, 20, 168, 166);

        if (this.isPointWithinBounds(168, 0, 20, 20, (double) mouseX, (double) mouseY))
            this.drawTexture(matrices, this.width / 2 + 84, this.height / 2 - 83, 20, 0, 20, 20);
        else
            this.drawTexture(matrices, this.width / 2 + 84, this.height / 2 - 83, 0, 0, 20, 20);

        DrawableHelper.drawCenteredText(matrices, textRenderer, this.title, this.width / 2, this.height / 2 - 63, 0xFFFFFF);

        if (this.client != null && this.client.player != null) {
            CurrencyComponent playerBalance = ModComponents.CURRENCY.get(this.client.player);
            long claimPrice = ((VillagerAccess) this.client.player).getClaimPrice();
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.claim_cost"), this.width / 2, this.height / 2 - 43, 0xFFFFFF);
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.claim_cost_2", claimPrice / 10000, (claimPrice % 10000) / 100, claimPrice % 100),
                    this.width / 2, this.height / 2 - 30, 0xFFFFFF);

            long balance = playerBalance.getValue();
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.balance"), this.width / 2, this.height / 2 - 10, 0xFFFFFF);

            // Gold
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.balance_3", playerBalance.getValue() / 10000), this.width / 2, this.height / 2 + 4,
                    0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            this.drawTexture(matrices, this.width / 2 - 13 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_3", playerBalance.getValue() / 10000)) / 2, this.height / 2 + 4,
                    40, 0, 9, 9);

            // Silver
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.balance_2", (playerBalance.getValue() % 10000) / 100), this.width / 2,
                    this.height / 2 + 17, 0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            this.drawTexture(matrices, this.width / 2 - 11 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_2", (playerBalance.getValue() % 10000) / 100)) / 2,
                    this.height / 2 + 17, 49, 0, 7, 8);

            // Bronze
            DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("numismaticclaim.screen.balance_1", playerBalance.getValue() % 100), this.width / 2, this.height / 2 + 30,
                    0xFFFFFF);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);
            this.drawTexture(matrices, this.width / 2 - 11 - textRenderer.getWidth(Text.translatable("numismaticclaim.screen.balance_1", playerBalance.getValue() % 100)) / 2, this.height / 2 + 30,
                    56, 0, 7, 7);

            if (balance < 10000)
                buttonWidget.active = false;

        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client != null && this.client.options.inventoryKey.matchesKey(keyCode, scanCode))
            this.close();

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (this.client != null && this.client.player != null && !this.switchScreen)
            NumismaticClaimClientPacket.writeC2SCloseScreenPacket(client, ((VillagerAccess) this.client.player).getCurrentOfferer().getId());

        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isPointWithinBounds(168, 0, 20, 20, (double) mouseX, (double) mouseY)) {
            this.switchScreen = true;
            this.client.setScreen(new MerchantScreen(this.handler, this.inventory, this.title));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = (this.width - 168) / 2;
        int j = (this.height - 166) / 2;
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

}
