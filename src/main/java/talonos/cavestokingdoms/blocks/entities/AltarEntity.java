package talonos.cavestokingdoms.blocks.entities;

import net.minecraft.tileentity.TileEntity;

public class AltarEntity extends TileEntity {

    private boolean isInited;

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (!isInited) {
                initialize();
                isInited = true;
            }

            // Periodic validation if needed
            if (worldObj.getTotalWorldTime() % 200 == 0)// && (!isIntegratedIntoStructure() || isMaster()))
            {
                validateStructure();
            }
        }
    }

    private void initialize() {

    }

    private void validateStructure() {
        int xPos = this.xCoord;
        int yPos = this.yCoord;
        int zPos = this.zCoord;

        for (int x = xPos; x < xPos + 2; x++) {
            for (int z = zPos; z < zPos + 2; z++) {
                if (!worldObj.blockExists(x, yPos, z)) {
                    throw new IllegalStateException(
                        "Tell Talonos his assumptions about altar chunk loading were untrue!");
                }
                TileEntity tile = worldObj.getTileEntity(x, yPos, z);
                if (tile == null || !(tile instanceof AltarEntity)) {
                    return;
                }
            }
        }

        // The three other blocks are altar blocks. Hooray!
        System.out.println("Valid altar.");
    }
}
