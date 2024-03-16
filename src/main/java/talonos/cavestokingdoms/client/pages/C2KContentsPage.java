package talonos.cavestokingdoms.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.client.pages.BookPage;

public class C2KContentsPage extends BookPage {

    String text;
    String[] iconText;
    ItemStack[] icons;

    @Override
    public void readPageFromXML(Element element) {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null) text = nodes.item(0)
            .getTextContent();

        nodes = element.getElementsByTagName("link");
        iconText = new String[nodes.getLength()];
        icons = new ItemStack[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            NodeList children = nodes.item(i)
                .getChildNodes();
            iconText[i] = children.item(1)
                .getTextContent();
            icons[i] = new ItemStack(Items.rotten_flesh);

            String total = children.item(3)
                .getTextContent();
            String mod = total.substring(0, total.indexOf(':'));
            String itemName = total.substring(total.indexOf(':') + 1);
            int secondColonPosition = itemName.indexOf(':');
            int meta = 0;
            if (secondColonPosition != -1) {
                meta = Integer.parseInt(itemName.substring(itemName.indexOf(':') + 1));
                itemName = itemName.substring(0, itemName.indexOf(':'));
            }
            Item iconItem = GameRegistry.findItem(mod, itemName);
            if (iconItem != null) {
                icons[i] = new ItemStack(GameRegistry.findItem(mod, itemName), 1, meta);
            }
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
        if (text != null) {
            if (isTranslatable) text = StatCollector.translateToLocal(text);
            manual.fonts.drawString(
                "\u00a7n" + text,
                localWidth + 25 + manual.fonts.getStringWidth(text) / 2,
                localHeight + 4,
                0);
        }
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        for (int i = 0; i < icons.length; i++) {
            manual.renderitem.renderItemIntoGUI(
                manual.fonts,
                manual.getMC().renderEngine,
                icons[i],
                localWidth + 16,
                localHeight + 18 * i + 18);
            int yOffset = 18;
            if (isTranslatable) iconText[i] = StatCollector.translateToLocal(iconText[i]);

            if (iconText[i].length() > 40) yOffset = 13;
            manual.fonts.drawString(iconText[i], localWidth + 38, localHeight + 18 * i + yOffset, 0);
        }
        manual.renderitem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
