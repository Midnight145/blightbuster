package talonos.blightbuster.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import talonos.blightbuster.tileentity.DawnMachineSpoutTileEntity;
import talonos.blightbuster.tileentity.dawnmachine.DawnMachineResource;
import thaumcraft.api.aspects.Aspect;

public class DawnMachineSpoutRenderer extends TileEntitySpecialRenderer {

    private ResourceLocation normalTexture = new ResourceLocation("blightbuster:textures/aspects/rawAspects.png");
    private ResourceLocation glowTexture = new ResourceLocation("blightbuster:textures/aspects/glowAspects.png");
    private boolean initializedTexFilters = false;

    public DawnMachineSpoutRenderer() {

    }

    private void initializeTexFilters() {
        bindTexture(normalTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        bindTexture(glowTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        initializedTexFilters = true;
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        DawnMachineSpoutTileEntity te = (DawnMachineSpoutTileEntity) tile;

        if (!initializedTexFilters) initializeTexFilters();

        GL11.glPushMatrix();
        ForgeDirection dir = ForgeDirection.NORTH;
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        for (int i = 0; i < 4; i++) {
            GL11.glPushMatrix();

            Aspect dirAspect = te.getEssentiaType(dir);
            float minU = 0.1f;
            float maxU = 0.4f;
            float minV = -0.4f;
            float maxV = -0.1f;

            if (dirAspect != null) {
                int value = te.getEssentiaAmount(dir);
                if (value > 0) {
                    bindTexture(glowTexture);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    minU = 0.0625f;
                    maxU = 0.4375f;
                    minV = -0.4375f;
                    maxV = -0.0625f;
                } else {
                    bindTexture(normalTexture);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }

                if (isRightSide(dirAspect)) {
                    float tmpMinU = minU;
                    minU = maxU * -1;
                    maxU = tmpMinU * -1;
                }

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                DawnMachineResource resource = DawnMachineResource.getResourceFromAspect(dirAspect);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2f(resource.getU(), resource.getV() + 0.5f);
                GL11.glNormal3f(0, 0, -1);
                GL11.glVertex3f(minU, minV, -0.5001f);
                GL11.glTexCoord2f(resource.getU(), resource.getV());
                GL11.glNormal3f(0, 0, -1);
                GL11.glVertex3f(minU, maxV, -0.5001f);
                GL11.glTexCoord2f(resource.getU() + 0.2f, resource.getV());
                GL11.glNormal3f(0, 0, -1);
                GL11.glVertex3f(maxU, maxV, -0.5001f);
                GL11.glTexCoord2f(resource.getU() + 0.2f, resource.getV() + 0.5f);
                GL11.glNormal3f(0, 0, -1);
                GL11.glVertex3f(maxU, minV, -0.5001f);
                GL11.glEnd();
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();
            GL11.glRotatef(90, 0, 1, 0);
            dir = dir.getRotation(ForgeDirection.DOWN);
        }
        GL11.glPopMatrix();
    }

    private boolean isRightSide(Aspect aspect) {
        return aspect == Aspect.MIND || aspect == Aspect.MECHANISM || aspect == Aspect.TREE || aspect == Aspect.PLANT;
    }
}
