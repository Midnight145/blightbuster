package talonos.cavestokingdoms.client.pages.orediscovery.entries;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryDiscoveryEntry implements IDiscoveryEntry {

    private int oreId;
    private String discoveredOreData;

    public OreDictionaryDiscoveryEntry(String oreDictionaryEntry, String discoveredOreData) {
        this.oreId = OreDictionary.getOreID(oreDictionaryEntry);
        this.discoveredOreData = discoveredOreData;
    }

    @Override
    public String getDiscoveredOreData() {
        return discoveredOreData;
    }

    @Override
    public boolean matches(ItemStack stack) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (id == this.oreId) return true;
        }

        return false;
    }
}
