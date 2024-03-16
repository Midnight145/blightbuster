package talonos.biomescanner;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.biomescanner.item.ItemBadge;

public class BSItems {

    public static void init() {
        badge = (ItemBadge) new ItemBadge().setMaxStackSize(1)
            .setCreativeTab(BiomeScanner.badgesTab);
        GameRegistry.registerItem(badge, "biomescanner:badge");
    }

    public static ItemBadge badge;
}
