package talonos.biomescanner.client;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GaugeBlockModel {

    /**
     * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8
     * points on a cube
     */
    private PositionTextureVertex[] vertexPositions;
    /** An array of 6 TexturedQuads, one for each face of a cube */
    private TexturedQuad[] quadList;

    private int displayList;
    private boolean builtDisplayList = false;

    public GaugeBlockModel() {
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(0, 0, 0, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(1, 0, 0, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(1, 1, 0, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(0, 1, 0, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(0, 0, 1, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(1, 0, 1, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(1, 1, 1, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(0, 1, 1, 0.0F, 8.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex, positiontexturevertex1,
                positiontexturevertex5 },
            16,
            16,
            0,
            0,
            16,
            16);
        this.quadList[1] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex7, positiontexturevertex3, positiontexturevertex6,
                positiontexturevertex2 },
            16,
            16,
            0,
            0,
            16,
            16);
        this.quadList[2] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex3, positiontexturevertex7,
                positiontexturevertex },
            16,
            16,
            0,
            0,
            16,
            16);
        this.quadList[3] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex2, positiontexturevertex6,
                positiontexturevertex5 },
            16,
            16,
            0,
            0,
            16,
            16);
        this.quadList[4] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex7, positiontexturevertex2,
                positiontexturevertex1 },
            16,
            16,
            0,
            0,
            16,
            16);
        this.quadList[5] = new TexturedQuad(
            new PositionTextureVertex[] { positiontexturevertex3, positiontexturevertex4, positiontexturevertex5,
                positiontexturevertex6 },
            16,
            16,
            0,
            0,
            16,
            16);

    }

    @SideOnly(Side.CLIENT)
    private void buildDisplayList() {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);

        for (int i = 0; i < this.quadList.length; ++i) {
            this.quadList[i].draw(Tessellator.instance, 1.0f);
        }

        GL11.glEndList();
    }

    /**
     * Draw the six sided box defined by this ModelBox
     */
    @SideOnly(Side.CLIENT)
    public void render() {
        if (!builtDisplayList) {
            buildDisplayList();
            builtDisplayList = true;
        }
        GL11.glCallList(this.displayList);
    }
}
