package talonos.cavestokingdoms;

import java.util.logging.Logger;

import net.minecraft.init.Blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import exterminatorJeff.undergroundBiomes.api.UBAPIHook;

public class UBCIntegration {

    public static void init(FMLPreInitializationEvent e) {

        try {
            Logger.getAnonymousLogger()
                .warning("Trying to UBify Thaumcraft Ores...");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 0, "cavestokingdoms:misc/cinnibar", "ubc.cavestokingdoms.cinnabar");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 1, "cavestokingdoms:misc/air", "ubc.cavestokingdoms.air");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 2, "cavestokingdoms:misc/fire", "ubc.cavestokingdoms.fire");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 3, "cavestokingdoms:misc/water", "ubc.cavestokingdoms.water");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 4, "cavestokingdoms:misc/earth", "ubc.cavestokingdoms.earth");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 5, "cavestokingdoms:misc/order", "ubc.cavestokingdoms.order");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 6, "cavestokingdoms:misc/entropy", "ubc.cavestokingdoms.entropy");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ConfigBlocks.blockCustomOre,
            // 7, "cavestokingdoms:misc/amberore", "ubc.cavestokingdoms.amber");

            Logger.getAnonymousLogger()
                .warning("Trying to UBify Thermal ores...");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(TFBlocks.blockOre, 3,
            // "cavestokingdoms:misc/Ore_Lead", "ubc.cavestokingdoms.lead");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(TFBlocks.blockOre, 4,
            // "cavestokingdoms:misc/Ore_Nickel", "ubc.cavestokingdoms.nickel");

            Logger.getAnonymousLogger()
                .warning("Trying to UBify AE Ores...");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(AEApi.instance().blocks().blockQuartzOre.block(),
            // 7, "cavestokingdoms:misc/OreQuartz", "ubc.cavestokingdoms.certus");
            // UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(AEApi.instance().blocks().blockQuartzOreCharged.block(),
            // 7, "cavestokingdoms:misc/OreQuartzCharged",
            // "ubc.cavestokingdoms.certuscharged");

            UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(
                Blocks.quartz_ore,
                0,
                "cavestokingdoms:misc/quartz_ore",
                "ubc.cavestokingdoms.quartz");

        } catch (Exception ex) {
            Logger.global.severe("Problem with UBIfying Things: " + ex);
            ex.printStackTrace();
        }
    }
}
