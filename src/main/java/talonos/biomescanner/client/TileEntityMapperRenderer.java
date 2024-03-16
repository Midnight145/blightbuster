package talonos.biomescanner.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.tileentity.TileEntityIslandMapper;

@SideOnly(Side.CLIENT)
public class TileEntityMapperRenderer extends TileEntitySpecialRenderer {

    private final ResourceLocation location;
    private final DynamicTexture bufferedImage;
    private int[] intArray = new int[176 * 180];

    public TileEntityMapperRenderer() {
        this.bufferedImage = new DynamicTexture(176, 180);
        this.location = Minecraft.getMinecraft().renderEngine
            .getDynamicTextureLocation("islandmapper", this.bufferedImage);
        this.intArray = this.bufferedImage.getTextureData();

        for (int i = 0; i < this.intArray.length; ++i) {
            this.intArray[i] = 0;
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
        TileEntityIslandMapper mapper = (TileEntityIslandMapper) entity;
        if (BiomeScanner.disableEverything) return;

        for (int pixY = 0; pixY < MapScanner.blockHeight; pixY++) {
            for (int pixX = 0; pixX < MapScanner.blockWidth; pixX++) {
                int worldX = mapper.getMapX() + pixX;
                int worldY = mapper.getMapY() + pixY;

                worldX = (worldX >= 0) ? worldX : 0;
                worldY = (worldY >= 0) ? worldY : 0;
                this.intArray[(pixY * MapScanner.blockWidth) + pixX] = MapScanner.instance.getColor(worldX, worldY);
            }
        }

        this.bufferedImage.updateDynamicTexture();

        this.bindTexture(location);

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        // GL11.glEnable(GL11.GL_BLEND);
        // GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, 0, -.1, 0, 1);
        tessellator.addVertexWithUV(0, 1, -.1, 0, 0);
        tessellator.addVertexWithUV(1, 1, -.1, 1, 0);
        tessellator.addVertexWithUV(1, 0, -.1, 1, 1);

        // tessellator.addVertexWithUV(0, 0, 0, 0, 0);
        // tessellator.addVertexWithUV(1, 0, 0, 1, 0);
        // tessellator.addVertexWithUV(1, 1, 0, 1, 1);
        // tessellator.addVertexWithUV(0, 1, 0, 0, 1);

        tessellator.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        // GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
