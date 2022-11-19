package net.numismaticclaim.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.numismaticclaim.NumismaticClaimClient;
import net.numismaticclaim.NumismaticClaimMain;
import net.numismaticclaim.access.VillagerAccess;
import net.numismaticclaim.screen.NumismaticClaimScreen;

@Environment(EnvType.CLIENT)
@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    @Unique
    private boolean isNumismaticClaimTrader = false;
    @Unique
    private final boolean isVillagerQuestsLoaded = NumismaticClaimMain.isVillagerQuestsLoaded;
    @Unique
    private PlayerInventory playerInventory;

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen;<init>(Lnet/minecraft/screen/MerchantScreenHandler;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/text/Text;)V", at = @At(value = "TAIL"))
    private void initialMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo info) {
        this.playerInventory = inventory;
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    protected void initMixin(CallbackInfo info) {
        if (((VillagerAccess) this.client.player).getCurrentOfferer() != null)
            this.isNumismaticClaimTrader = ((VillagerAccess) ((VillagerAccess) this.client.player).getCurrentOfferer()).isNumismaticClaimTrader();
    }

    @Inject(method = "drawBackground", at = @At(value = "TAIL"))
    protected void drawBackgroundMixin(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        if (this.client != null && this.client.player != null && this.isNumismaticClaimTrader) {
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;

            RenderSystem.setShaderTexture(0, NumismaticClaimClient.CLAIM_GUI);

            if (this.isPointWithinBounds(276, isVillagerQuestsLoaded ? 20 : 0, 20, 20, (double) mouseX, (double) mouseY))
                MerchantScreenMixin.drawTexture(matrices, i + 276, j + (isVillagerQuestsLoaded ? 20 : 0), 20, 0, 20, 20, 256, 256);
            else
                MerchantScreenMixin.drawTexture(matrices, i + 276, j + (isVillagerQuestsLoaded ? 20 : 0), 0, 0, 20, 20, 256, 256);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.isPointWithinBounds(276, isVillagerQuestsLoaded ? 20 : 0, 20, 20, (double) mouseX, (double) mouseY) && this.isNumismaticClaimTrader) {
            this.client.setScreen(new NumismaticClaimScreen(this.handler, this.playerInventory, this.title));
            info.cancel();
        }
    }
}
