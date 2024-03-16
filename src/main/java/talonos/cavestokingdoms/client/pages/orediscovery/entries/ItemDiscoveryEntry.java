package talonos.cavestokingdoms.client.pages.orediscovery.entries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDiscoveryEntry implements IDiscoveryEntry {

    private Item discoveryItem;
    private int discoveryMeta;
    private int discoveryMetaFlags;
    private String discoveredOreData;

    public ItemDiscoveryEntry(Item item, int meta, int metaFlags, String oreData) {
        this.discoveryItem = item;
        this.discoveryMeta = meta;
        this.discoveryMetaFlags = metaFlags;
        this.discoveredOreData = oreData;
    }

    public Item getDiscoveryItem() {
        return discoveryItem;
    }

    public int getDiscoveryMeta() {
        return discoveryMeta;
    }

    public int getDiscoveryMetaFlags() {
        return discoveryMetaFlags;
    }

    public String getDiscoveredOreData() {
        return discoveredOreData;
    }

    public boolean matches(ItemStack stack) {
        if (discoveryItem == null || stack.getItem() == null) return false;
        if (getDiscoveryItem() != stack.getItem()) return false;

        return ((getDiscoveryMeta() & getDiscoveryMetaFlags()) == (stack.getItemDamage() & getDiscoveryMetaFlags()));
    }
}
