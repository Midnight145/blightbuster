package talonos.cavestokingdoms.proxies;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mantle.client.MProxyClient;
import mantle.client.gui.GuiManual;
import talonos.cavestokingdoms.client.pages.C2KArmorPage;
import talonos.cavestokingdoms.client.pages.C2KBowMaterialsPage;
import talonos.cavestokingdoms.client.pages.C2KClassicToolsPage;
import talonos.cavestokingdoms.client.pages.C2KContentsPage;
import talonos.cavestokingdoms.client.pages.C2KMiningPage;
import talonos.cavestokingdoms.client.pages.C2KModularToolsPage;
import talonos.cavestokingdoms.client.pages.ExtMaterialsUsagePage;

public class ClientProxy extends CommonProxy {

    private Field manualItemStack = null;

    public ClientProxy() {
        try {
            manualItemStack = GuiManual.class.getDeclaredField("itemstackBook");
            manualItemStack.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("Failed to find 'itemstackBook' field of GuiManual.", ex);
        }
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();

        MProxyClient.registerManualPage("c2kModularToolsPage", C2KModularToolsPage.class);
        MProxyClient.registerManualPage("extMaterialUsePage", ExtMaterialsUsagePage.class);
        MProxyClient.registerManualPage("bowMaterialsPage", C2KBowMaterialsPage.class);
        MProxyClient.registerManualPage("c2kContentsPage", C2KContentsPage.class);
        MProxyClient.registerManualPage("c2kClassicToolsPage", C2KClassicToolsPage.class);
        MProxyClient.registerManualPage("c2kArmorPage", C2KArmorPage.class);
        MProxyClient.registerManualPage("c2kMiningPage", C2KMiningPage.class);
    }

    public ItemStack getManualBook(GuiManual manual) {
        try {
            return (ItemStack) manualItemStack.get(manual);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to change accessibility for 'itemstackBook' field of GuiManual.", ex);
        }
    }

    @Override
    public EntityPlayer getPlayerFromContext(MessageContext context) {
        return Minecraft.getMinecraft().thePlayer;
    }
}
