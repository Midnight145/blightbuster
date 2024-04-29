package talonos.blightbuster.items;

import net.minecraft.item.Item;

import talonos.blightbuster.BlightbusterConfig;

public class BBItems {

    public static Item purityFocus;
    public static Item silverPotion;
    public static Item worldTainter;
    public static Item worldSuperTainter;

    public static void init() {
        if (BlightbusterConfig.enablePurityFocus) {
            purityFocus = new ItemPurityFocus();
        }
        if (BlightbusterConfig.enableSilverPotion) {
            silverPotion = new ItemSilverPotion();
        }
        if (BlightbusterConfig.enableWorldTainter) {
            worldTainter = new ItemWorldTainter();
        }
        if (BlightbusterConfig.enableSuperWorldTainter) {
            worldSuperTainter = new ItemSuperTestWorldTainter();
        }
    }
}
