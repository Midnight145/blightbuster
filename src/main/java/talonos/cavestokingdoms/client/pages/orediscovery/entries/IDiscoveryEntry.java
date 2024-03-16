package talonos.cavestokingdoms.client.pages.orediscovery.entries;

import net.minecraft.item.ItemStack;

public interface IDiscoveryEntry {

    String getDiscoveredOreData();

    boolean matches(ItemStack stack);
}
