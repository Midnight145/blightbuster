package talonos.biomescanner;

import talonos.biomescanner.tileentity.TileEntityGauge;
import talonos.biomescanner.tileentity.TileEntityIslandMapper;
import cpw.mods.fml.common.registry.GameRegistry;
import talonos.biomescanner.tileentity.TileEntityIslandScanner;

public class CommonProxy 
{
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityIslandMapper.class, "islandMapper");
        GameRegistry.registerTileEntity(TileEntityIslandScanner.class, "islandScanner");
        GameRegistry.registerTileEntity(TileEntityGauge.class, "scannerGauge");
    }
    
    public void registerRenderers() 
    {
    }
}
