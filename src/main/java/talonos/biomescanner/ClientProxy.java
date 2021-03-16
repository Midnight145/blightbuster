package talonos.biomescanner;

import talonos.biomescanner.client.TileEntityGaugeRenderer;
import talonos.biomescanner.tileentity.TileEntityGauge;
import talonos.biomescanner.tileentity.TileEntityIslandMapper;
import cpw.mods.fml.client.registry.ClientRegistry;
import talonos.biomescanner.client.TileEntityMapperRenderer;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers() 
    {
    	super.registerRenderers();	
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIslandMapper.class, new TileEntityMapperRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGauge.class, new TileEntityGaugeRenderer());
    }
}
