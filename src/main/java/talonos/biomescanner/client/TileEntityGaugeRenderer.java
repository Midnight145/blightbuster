package talonos.biomescanner.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.Zone;
import talonos.biomescanner.tileentity.TileEntityGauge;

@SideOnly(Side.CLIENT)
public class TileEntityGaugeRenderer extends TileEntitySpecialRenderer {

    private GaugeBlockModel block;

    public TileEntityGaugeRenderer() {
        block = new GaugeBlockModel();
    }

    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
        TileEntityGauge tileEntityGauge = (TileEntityGauge) entity;
        boolean shouldMixIcon = Math.abs(tileEntityGauge.getCompletion() - 1.0f) < 0.001f;

        float tickCycle = 0;

        if (shouldMixIcon) {
            tickCycle = (float) (entity.getWorldObj()
                .getWorldTime() % 40);
            tickCycle = Math.abs(tickCycle / 20.0f - 1.0f);

            if (tickCycle < 0.01f) shouldMixIcon = false;
        }

        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

        if (renderEngine == null) return;
        if (BiomeScanner.disableEverything) return;

        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y, (float) z);

        renderEngine.bindTexture(new ResourceLocation("biomescanner", getIcon(tileEntityGauge.getTargetZone())));
        block.render();

        if (shouldMixIcon) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1, 1, 1, tickCycle);
            renderEngine
                .bindTexture(new ResourceLocation("biomescanner", getEmptyIcon(tileEntityGauge.getTargetZone())));
            block.render();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
        }
        GL11.glPopMatrix();
    }

    private String getIcon(Zone targetZone) {
        String prefix = "textures/blocks/biomeMon-";

        return prefix + targetZone.toString() + ".png";
    }

    private String getEmptyIcon(Zone targetZone) {
        String prefix = "textures/blocks/biomeMon-empty";
        return prefix + Integer.toString(targetZone.ordinal() / 6) + ".png";
    }
}
