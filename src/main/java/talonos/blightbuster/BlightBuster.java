package talonos.blightbuster;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.entities.EntitySilverPotion;
import talonos.blightbuster.handlers.PurityFocusEventHandler;
import talonos.blightbuster.items.BBItems;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.rituals.RitualDawnMachine;
import talonos.blightbuster.tileentity.DawnMachineSpoutTileEntity;
import talonos.blightbuster.tileentity.dawnmachine.DawnMachineChunkLoader;
import thaumicenergistics.api.ThEApi;

@Mod(
    modid = BlightBuster.MODID,
    name = BlightBuster.MODNAME,
    version = BlightBuster.VERSION,
    dependencies = BlightBuster.DEPS)
public class BlightBuster {

    public static final String MODID = "blightbuster";
    public static final String MODNAME = "BlightBuster";
    public static final String VERSION = "2.0.8";
    public static final String DEPS = "after:Thaumcraft";
    public static final String COMMONPROXYLOCATION = "talonos." + MODID + ".CommonProxy";
    public static final String CLIENTPROXYLOCATION = "talonos." + MODID + ".ClientProxy";

    @SidedProxy(clientSide = BlightBuster.CLIENTPROXYLOCATION, serverSide = BlightBuster.COMMONPROXYLOCATION)
    public static CommonProxy proxy;

    public static BlightBuster instance;
    private DawnMachineChunkLoader dawnMachineChunkLoader;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        BBBlock.init();
        BBItems.init();
        proxy.registerTileEntities();
        EntityRegistry.registerModEntity(EntitySilverPotion.class, "silverPotion", 0, MODID, 250, 5, true);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BlightbusterNetwork.init();

        MinecraftForge.EVENT_BUS.register(new PurityFocusEventHandler()); // adds event handler
        FMLCommonHandler.instance()
            .bus()
            .register(new PurityFocusEventHandler());
        this.dawnMachineChunkLoader = new DawnMachineChunkLoader(); // creates chunk loader
        ForgeChunkManager.setForcedChunkLoadingCallback(this, this.dawnMachineChunkLoader); // adds chunkloader to forge
        if (Loader.isModLoaded("thaumicenergistics") && ThEApi.instance() != null) {
            ThEApi.instance()
                .transportPermissions() // adds Dawn Machine spout entity to Thaumic Energistics
                .addAspectContainerTileToInjectPermissions(DawnMachineSpoutTileEntity.class, 320);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        AddedResearch.initResearch();
        proxy.registerRenderers();
        if (Loader.isModLoaded("AWWayofTime")) {
            RitualDawnMachine.init();
        }
    }
}
