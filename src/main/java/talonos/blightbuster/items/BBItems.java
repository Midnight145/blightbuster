package talonos.blightbuster.items;

import net.minecraft.item.Item;

public class BBItems {

    public static Item purityFocus;
    public static Item silverPotion;

    public static void init() {
        purityFocus = new ItemPurityFocus();
        silverPotion = new ItemSilverPotion();
    }
}
