package talonos.biomescanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.block.BSBlock;
import talonos.biomescanner.command.ResetBaselineCommand;
import talonos.biomescanner.gui.GuiHandlerBadgePrinter;
import talonos.biomescanner.map.BiomeMapColors;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.map.Zone;

@Mod(
    modid = BiomeScanner.MODID,
    name = BiomeScanner.MODNAME,
    version = BiomeScanner.VERSION,
    dependencies = BiomeScanner.DEPS)
public class BiomeScanner {

    public static final String MODID = "biomescanner";
    public static final String MODNAME = "Biome Scanner";
    public static final String VERSION = "2.1.0";
    public static final String DEPS = "before:UndergroundBiomes;after:ThermalFoundation;after:appliedenergistics2;after:Thaumcraft";
    public static final String COMMONPROXYLOCATION = "talonos." + MODID + ".CommonProxy";
    public static final String CLIENTPROXYLOCATION = "talonos." + MODID + ".ClientProxy";

    public static File baselineFile;

    public static final CreativeTabs badgesTab = new CreativeTabs("badges") {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return BSItems.badge;
        }
    };

    @SidedProxy(clientSide = BiomeScanner.CLIENTPROXYLOCATION, serverSide = BiomeScanner.COMMONPROXYLOCATION)
    public static CommonProxy proxy;

    @Mod.Instance(BiomeScanner.MODID)
    public static BiomeScanner instance;

    public static Integer[] zoneBaselines = new Integer[Zone.values().length];

    public static boolean disableEverything = false;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        BSBlock.init();
        BSItems.init();
        proxy.registerTileEntities();

        final File configFile = event.getSuggestedConfigurationFile();
        final Configuration config = new Configuration(configFile);

        final boolean useBaselineFile = config.get(Configuration.CATEGORY_GENERAL, "useBaselineFile", false)
            .getBoolean();
        final String baselineFileName = config.get(Configuration.CATEGORY_GENERAL, "baselineFileName", "baseline.dat")
            .getString();
        disableEverything = config.get(Configuration.CATEGORY_GENERAL, "disableEverything", false)
            .getBoolean();
        config.save();

        Arrays.fill(zoneBaselines, null);

        if (useBaselineFile) {
            BiomeScanner.baselineFile = new File(configFile.getParent(), baselineFileName);
            if (baselineFile.exists()) {
                try {
                    final NBTTagCompound loadedData = CompressedStreamTools
                        .readCompressed(new FileInputStream(baselineFile));
                    loadBaselineData(loadedData);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void loadBaselineData(NBTTagCompound loadedData) {
        final NBTTagCompound regionMap = loadedData.getCompoundTag("RegionMap");
        final NBTTagCompound baseline = regionMap.getCompoundTag("BaselineBlocks");

        for (int i = 0; i < zoneBaselines.length; i++) {
            if (baseline.hasKey(Integer.toString(i))) {
                zoneBaselines[i] = baseline.getInteger(Integer.toString(i));
            } else {
                zoneBaselines[i] = null;
            }
        }

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(MapScanner.instance);
        MinecraftForge.EVENT_BUS.register(MapScanner.instance);

    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        BiomeMapColors.initColors();
        proxy.registerRenderers();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandlerBadgePrinter());
    }

    @Mod.EventHandler
    public static void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ResetBaselineCommand());
    }
}
