package talonos.biomescanner.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import talonos.biomescanner.BSItems;
import talonos.biomescanner.tileentity.TileEntityIslandScanner;

public class GuiBadgePrinter extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(
        "biomescanner",
        "textures/gui/badgePrinter.png");

    private IInventory player;
    private IInventory printer;

    public GuiBadgePrinter(InventoryPlayer player, TileEntityIslandScanner scanner) {
        super(new ContainerBadgePrinter(player, scanner));

        this.player = player;
        this.printer = scanner;
        this.allowUserInput = false;
        short short1 = 222;
        int i = short1 - 108;
        this.ySize = i + 6 * 18;
        this.xSize = 230;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj.drawString(
            this.printer.hasCustomInventoryName() ? this.printer.getInventoryName()
                : I18n.format(this.printer.getInventoryName(), new Object[0]),
            8,
            6,
            4210752);
        this.fontRendererObj.drawString(
            this.player.hasCustomInventoryName() ? this.player.getInventoryName()
                : I18n.format(this.player.getInventoryName(), new Object[0]),
            34,
            this.ySize - 96 + 2,
            4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(background);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 6 * 18 + 17);
        this.drawTexturedModalRect(k, l + 6 * 18 + 17, 0, 126, this.xSize, 96);
        this.mc.getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);

        for (int badgeY = 0; badgeY < 6; badgeY++) {
            for (int badgeX = 0; badgeX < 12; badgeX++) {
                this.drawTexturedModelRectFromIcon(
                    k + 8 + (badgeX * 18),
                    l + 18 + (badgeY * 18),
                    BSItems.badge.getZoneSilhouette(),
                    16,
                    16);
            }
        }

        this.drawTexturedModelRectFromIcon(k + 8, l + 149, BSItems.badge.getBeginnerSilhouette(), 16, 16);
        this.drawTexturedModelRectFromIcon(k + 8, l + 176, BSItems.badge.getCompletionSilhouette(), 16, 16);
    }
}
