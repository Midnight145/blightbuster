package talonos.blightbuster;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.blightbuster.tileentity.DawnChargerTileEntity;
import talonos.blightbuster.tileentity.DawnMachineSpoutTileEntity;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import talonos.blightbuster.tileentity.DawnTotemTileEntity;

public class CommonProxy {

    public void registerTileEntities() {
        if (BlightbusterConfig.enableDawnTotem) {
            GameRegistry.registerTileEntity(DawnTotemTileEntity.class, "DawnTotemEntity");
        }
        if (BlightbusterConfig.enableDawnMachine) {
            GameRegistry.registerTileEntity(DawnMachineSpoutTileEntity.class, "DawnMachineSpout");
            GameRegistry.registerTileEntity(DawnMachineTileEntity.class, "DawnMachine");
            if (BlightbusterConfig.enableDawnCharger && BlightbusterConfig.enableRf) {
                GameRegistry.registerTileEntity(DawnChargerTileEntity.class, "DawnCharger");
            }
        }
    }

    public void registerRenderers() {}

    public double getBestCleanseSpawnHeight() {
        return 0;
    }
}
