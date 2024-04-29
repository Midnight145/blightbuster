package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract interface UBOreTexturizer {

    public static final String amber_overlay = "undergroundbiomes:amber_overlay";
    public static final String cinnabar_overlay = "undergroundbiomes:cinnabar_overlay";
    public static final String coal_overlay = "undergroundbiomes:coal_overlay";
    public static final String copper_overlay = "undergroundbiomes:copper_overlay";
    public static final String diamond_overlay = "undergroundbiomes:diamond_overlay";
    public static final String emerald_overlay = "undergroundbiomes:emerald_overlay";
    public static final String gold_overlay = "undergroundbiomes:gold_overlay";
    public static final String iron_overlay = "undergroundbiomes:iron_overlay";
    public static final String lapis_overlay = "undergroundbiomes:lapis_overlay";
    public static final String lead_overlay = "undergroundbiomes:lead_overlay";
    public static final String olivine_peridot_overlay = "undergroundbiomes:olivine-peridot_overlay";
    public static final String redstone_overlay = "undergroundbiomes:redstone_overlay";
    public static final String ruby_overlay = "undergroundbiomes:ruby_overlay";
    public static final String sapphire_overlay = "undergroundbiomes:sapphire_overlay";
    public static final String tin_overlay = "undergroundbiomes:tin_overlay";
    public static final String uranium_overlay = "undergroundbiomes:uranium_overlay";

    public abstract void setupUBOre(Block paramBlock, String paramString,
        FMLPreInitializationEvent paramFMLPreInitializationEvent);

    @Deprecated
    public abstract void setupUBOre(Block paramBlock, int paramInt, String paramString,
        FMLPreInitializationEvent paramFMLPreInitializationEvent);

    public abstract void setupUBOre(Block paramBlock, int paramInt, String paramString1, String paramString2,
        FMLPreInitializationEvent paramFMLPreInitializationEvent);

    public abstract void requestUBOreSetup(Block paramBlock, String paramString)
        throws UBOreTexturizer.BlocksAreAlreadySet;

    @Deprecated
    public abstract void requestUBOreSetup(Block paramBlock, int paramInt, String paramString)
        throws UBOreTexturizer.BlocksAreAlreadySet;

    public abstract void requestUBOreSetup(Block paramBlock, int paramInt, String paramString1, String paramString2)
        throws UBOreTexturizer.BlocksAreAlreadySet;

    public abstract void redoOres(int paramInt1, int paramInt2, World paramWorld);

    public static class BlocksAreAlreadySet extends RuntimeException {

        public final Block oreBlock;
        public final String overlayName;

        public BlocksAreAlreadySet(Block oreBlock, String overlayName) {
            this.oreBlock = oreBlock;
            this.overlayName = overlayName;
        }

        public String toString() {
            String blockDescription = "undefined block";
            String overlayDescription = "undefined overlay";
            if (this.oreBlock != null) blockDescription = this.oreBlock.getUnlocalizedName();
            if (this.overlayName != null) overlayDescription = this.overlayName;
            return "Attempt to create Underground Biomes ore for " + blockDescription
                + " with "
                + overlayDescription
                + " after blocks have already been defined";
        }
    }
}
