package talonos.blightbuster.compat;

import cpw.mods.fml.common.Loader;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;

public class CompatFixes {

    public static void fixEnderIO() {
        // EnderIO has a strange incompatibility where if RF is disabled in the config file, it will crash the game
        // while EnderIO is registering aspects.
        // This is a workaround to prevent that crash.
        if (!Loader.isModLoaded("EnderIO")) {
            return;
        }
        BlightBuster.logger.info("EnderIO detected, force-enabling RF support.");
        BlightbusterConfig.enableRf = true;
    }
}
