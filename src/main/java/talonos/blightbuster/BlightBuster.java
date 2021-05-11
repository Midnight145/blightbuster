package talonos.blightbuster;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.entities.EntitySilverPotion;
import talonos.blightbuster.handlers.PurityFocusEventHandler;
import talonos.blightbuster.items.BBItems;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.tileentity.DawnMachineSpoutTileEntity;
import talonos.blightbuster.tileentity.dawnmachine.DawnMachineChunkLoader;
import thaumicenergistics.api.ThEApi;

@Mod(modid = BlightBuster.MODID, name = BlightBuster.MODNAME, version = BlightBuster.VERSION, dependencies = BlightBuster.DEPS)
public class BlightBuster
{
	
	public static final String MODID = "blightbuster";
	public static final String MODNAME = "BlightBuster";
    public static final String VERSION = "1.2.21";
    public static final String DEPS = "before:UndergroundBiomes;after:ThermalFoundation;after:appliedenergistics2;after:Thaumcraft";
	public static final String COMMONPROXYLOCATION = "talonos."+MODID+".CommonProxy";
	public static final String CLIENTPROXYLOCATION = "talonos."+MODID+".ClientProxy";
	
	
	
	@SidedProxy(clientSide = BlightBuster.CLIENTPROXYLOCATION, serverSide = BlightBuster.COMMONPROXYLOCATION)
	public static CommonProxy proxy;

	public static BlightBuster instance;

    public DawnMachineChunkLoader chunkLoader = null;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
		BBBlock.init();
		BBItems.init();
		proxy.registerTileEntities();
        EntityRegistry.registerModEntity(EntitySilverPotion.class, "silverPotion", 0, MODID, 250, 5, true);
    }
 
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        BlightbusterNetwork.init();
    	MinecraftForge.EVENT_BUS.register(new PurityFocusEventHandler()); // adds event handler
        FMLCommonHandler.instance().bus().register(new PurityFocusEventHandler());

        chunkLoader = new DawnMachineChunkLoader(); // creates chunk loader
        ForgeChunkManager.setForcedChunkLoadingCallback(this, chunkLoader); // adds chunkloader to forge

        if (ThEApi.instance() != null)
            ThEApi.instance().transportPermissions().addAspectContainerTileToInjectPermissions(DawnMachineSpoutTileEntity.class, 32); // adds Dawn Machine spout entity to Thaumic Energistics
    }
 
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	AddedResearch.initResearch();
        proxy.registerRenderers();
    }
}
