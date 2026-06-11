
package talonos.blightbuster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

public class StabilizedNodeRenderer {

    // i have zero idea how any of this works so if you come across this in the future please don't judge me
    // or ask me to explain it because i can't

    public static void render(int bx, int by, int bz, float partialTicks) {
        final float radius = 0.6f;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = mc.thePlayer;
        if (p == null) return;

        double px = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double py = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double pz = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        double x = bx - px + 0.5;
        double y = by - py + 0.5;
        double z = bz - pz + 0.5;

        float t = (System.currentTimeMillis() % 200000L) / 1000f;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);

        renderShimmeringLayers(t, radius);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private static void renderShimmeringLayers(float time, float radius) {
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(time * 0.04f, time * 0.02f, 0);
        GL11.glScalef(1.8f, 1.8f, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glColor4f(0.8f, 0.9f, 1f, 1f);
        drawSphere(radius, 24, 24);

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(time * -0.10f, time * 0.06f, 0);
        GL11.glScalef(3.5f, 3.5f, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glColor4f(0.9f, 1f, 1f, 1f);
        drawSphere(radius, 24, 24);

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private static void drawSphere(float radius, int stacks, int slices) {
        float time = (System.currentTimeMillis() % 100000) / 1000f;

        for (int i = 0; i < stacks; i++) {
            float lat0 = (float) Math.PI * (-0.5f + (float) i / stacks);
            float z0 = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = (float) Math.PI * (-0.5f + (float) (i + 1) / stacks);
            float z1 = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for (int j = 0; j <= slices; j++) {
                float lng = 2f * (float) Math.PI * (j == slices ? 0 : j) / slices;
                float x = (float) Math.cos(lng);
                float y = (float) Math.sin(lng);

                float bolt0 = Math.abs((float) Math.sin(time * 12 + lng * 6 + lat0 * 9));
                float bolt1 = Math.abs((float) Math.sin(time * 12 + lng * 6 + lat1 * 9));
                bolt0 = (float) Math.pow(bolt0, 2.2);
                bolt1 = (float) Math.pow(bolt1, 2.2);

                GL11.glColor4f(0.7f, 0.9f, 1f, bolt0 * 0.35f);
                GL11.glVertex3f(radius * x * zr0, radius * z0, radius * y * zr0);

                GL11.glColor4f(0.7f, 0.9f, 1f, bolt1 * 0.35f);
                GL11.glVertex3f(radius * x * zr1, radius * z1, radius * y * zr1);
            }
            GL11.glEnd();
        }
    }
}
