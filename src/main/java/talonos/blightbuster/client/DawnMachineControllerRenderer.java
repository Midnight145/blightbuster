package talonos.blightbuster.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;

public class DawnMachineControllerRenderer extends TileEntitySpecialRenderer {
    public DawnMachineControllerRenderer() {
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        DawnMachineTileEntity te = ((DawnMachineTileEntity)tile);

        IIcon top = BBBlock.dawnMachine.getTopIcon();
        IIcon bottom = BBBlock.dawnMachine.getBottomIcon();
        IIcon side = (te.getEnergyStored() >= DawnMachineTileEntity.DEAD_RF)?BBBlock.dawnMachine.getLiveSideIcon():BBBlock.dawnMachine.getDeadSideIcon();

        GL11.glPushMatrix();
        bindTexture(TextureMap.locationBlocksTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(top.getMaxU(), top.getMaxV());
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(0.5f, 0.5f, 0.5f);
        GL11.glTexCoord2f(top.getMaxU(), top.getMinV());
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(0.5f, 0.5f, -0.5f);
        GL11.glTexCoord2f(top.getMinU(), top.getMinV());
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
        GL11.glTexCoord2f(top.getMinU(), top.getMaxV());
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(-0.5f, 0.5f, 0.5f);

        GL11.glTexCoord2f(bottom.getMaxU(), bottom.getMaxV());
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3f(0.5f, -0.5f, -0.5f);
        GL11.glTexCoord2f(bottom.getMaxU(), bottom.getMinV());
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3f(0.5f, -0.5f, 0.5f);
        GL11.glTexCoord2f(bottom.getMinU(), bottom.getMinV());
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
        GL11.glTexCoord2f(bottom.getMinU(), bottom.getMaxV());
        GL11.glNormal3f(0, -1, 0);
        GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
        GL11.glEnd();

        for (int i = 0; i < 4; i++) {
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(side.getMinU(), side.getMaxV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(-0.5f, -0.5f, -0.5f);
            GL11.glTexCoord2f(side.getMinU(), side.getMinV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
            GL11.glTexCoord2f(side.getMaxU(), side.getMinV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(0.5f, 0.5f, -0.5f);
            GL11.glTexCoord2f(side.getMaxU(), side.getMaxV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(0.5f, -0.5f, -0.5f);
            GL11.glEnd();
        }
        GL11.glPopMatrix();

        Vec3 color = te.getGlowColor(f);

        if (color == null)
            return;

        IIcon glow = BBBlock.dawnMachine.getGlowIcon();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glColor4d(color.xCoord, color.yCoord, color.zCoord, 1);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < 4; i++) {
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(glow.getMinU(), glow.getMaxV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(-0.5f, -0.5f, -0.5001f);
            GL11.glTexCoord2f(glow.getMinU(), glow.getMinV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(-0.5f, 0.5f, -0.5001f);
            GL11.glTexCoord2f(glow.getMaxU(), glow.getMinV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(0.5f, 0.5f, -0.5001f);
            GL11.glTexCoord2f(glow.getMaxU(), glow.getMaxV());
            GL11.glNormal3f(0, 0, -1);
            GL11.glVertex3f(0.5f, -0.5f, -0.5001f);
            GL11.glEnd();
        }

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
