package talonos.cavestokingdoms.client.pages.orediscovery;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.client.pages.BookPage;
import talonos.cavestokingdoms.CavesToKingdoms;
import talonos.cavestokingdoms.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public abstract class OreDiscoveryPage extends BookPage {

    private String discoveryName;

    protected OreDiscoveryPage(String discoveryName) {
        this.discoveryName = discoveryName;
    }

    protected OreDiscoveryPage() {}

    protected boolean isDiscovered() {
        return isDiscovered(discoveryName);
    }

    protected boolean isDiscovered(String neededDiscovery) {
        if (neededDiscovery == null || neededDiscovery.equals("")) {
            // No discovery needed; default to true;
            return true;
        }
        ItemStack book = ((ClientProxy) CavesToKingdoms.proxy).getManualBook(manual);
        if (OreDiscoveryRegistry.getInstance()
            .hasDiscovery(book.getTagCompound(), neededDiscovery)) return true;

        return OreDiscoveryRegistry.getInstance()
            .hasDiscovery(manual.getMC().thePlayer, neededDiscovery);
    }
}
