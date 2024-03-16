package talonos.cavestokingdoms.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryPage;

public class C2KArmorPage extends OreDiscoveryPage {

    // The title of the page
    String[] title = new String[2];

    // Itemstacks representing the tools we draw
    ItemStack[][] icons = new ItemStack[4][2];

    // Itemstacks representing the required items for ore discovery.
    ItemStack[] requiredIcon = new ItemStack[2];

    // Description of the material
    String[] description = new String[2];

    // What unlocks it?
    String[] requires = new String[2];

    @Override
    public void readPageFromXML(Element element) {
        for (int i = 0; i < 2; i++) {
            NodeList nodes = element.getElementsByTagName("title");
            if (nodes != null) {
                title[i] = nodes.item(i)
                    .getTextContent();
            }
            nodes = element.getElementsByTagName("text");
            if (nodes != null) {
                description[i] = nodes.item(i)
                    .getTextContent();
            }

            nodes = element.getElementsByTagName("requires");
            if (nodes != null && nodes.item(0) != null) {
                requires[i] = nodes.item(i)
                    .getTextContent();
            }

            // Get the icons
            nodes = element.getElementsByTagName("armors");
            if (nodes != null && nodes.item(i) != null) {
                Node n = nodes.item(i);
                Element toolElement = (Element) n;
                NodeList tools = toolElement.getElementsByTagName("armor");
                for (int j = 0; j < 5; j++) {
                    Node toolNode = tools.item(j);
                    if (toolNode == null) {
                        continue;
                    }
                    String total = toolNode.getTextContent();
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
                        icons[j][i] = new ItemStack(GameRegistry.findItem(mod, itemName), 1, meta);
                    }
                }
            }

            // Get the icons
            requiredIcon[i] = new ItemStack(Items.rotten_flesh);

            nodes = element.getElementsByTagName("requiresIcon");
            if (nodes != null && nodes.item(i) != null
                && nodes.item(i)
                    .getTextContent() != null) // who knows what
                                               // could go wrong?
                                               // :/
            {
                String total = nodes.item(i)
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
                    requiredIcon[i] = new ItemStack(GameRegistry.findItem(mod, itemName), 1, meta);
                }
            }
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
        for (int i = 0; i < 2; i++) if (isDiscovered(requires[i])) {
            drawNormal(localWidth, localHeight + (76 * i), i);
        } else {
            drawLocked(localWidth, localHeight + (76 * i), i);
        }
    }

    private void drawNormal(int localWidth, int localHeight, int i) {
        String armorpoints = StatCollector.translateToLocal("manual.cavestokingdoms.armorpoints");
        String durability = StatCollector.translateToLocal("manual.cavestokingdoms.durability");
        String freelevels = StatCollector.translateToLocal("manual.cavestokingdoms.freelevels");

        manual.fonts.drawString("\u00a7n" + title[i], localWidth + 70, localHeight + 4, 0);

        if (description[i] != null && !description[i].equals("")) {
            if (icons[0][i] != null && icons[0][i].getItem() instanceof ItemArmor) {
                manual.fonts.drawSplitString(
                    description[i] + " "
                        + freelevels
                        + ": "
                        + ((ItemArmor) icons[0][i].getItem()).getArmorMaterial()
                            .getEnchantability(),
                    localWidth + 0,
                    localHeight + 58,
                    178,
                    0);
            } else {
                manual.fonts.drawSplitString(description[i], localWidth, localHeight + 58, 178, 0);
            }
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        manual.renderitem.zLevel = 100;

        for (int armor = 0; armor < 4; armor++) {
            manual.renderitem.renderItemAndEffectIntoGUI(
                manual.fonts,
                manual.getMC().renderEngine,
                icons[armor][i],
                localWidth + 76 + (armor * 18),
                localHeight + 16);
        }

        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        manual.fonts.drawString(durability + ": ", localWidth + 25, localHeight + 36, 0);
        manual.fonts.drawString(armorpoints + ": ", localWidth + 15, localHeight + 46, 0);

        for (int armor = 0; armor < 4; armor++) {
            ItemStack armorStack = icons[armor][i];
            if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
                int maxDurability = ((ItemArmor) armorStack.getItem()).getMaxDamage(armorStack);
                int protection = ((ItemArmor) armorStack.getItem()).damageReduceAmount;
                manual.fonts.drawString("" + maxDurability, localWidth + 79 + (armor * 18), localHeight + 36, 0);
                manual.fonts.drawString("" + protection, localWidth + 82 + (armor * 18), localHeight + 46, 0);
            }
        }

    }

    private void drawLocked(int localWidth, int localHeight, int i) {
        String undiscovered = StatCollector.translateToLocal("manual.cavestokingdoms.undiscovered");
        String pleasetouch = StatCollector.translateToLocal("manual.cavestokingdoms.pleasetouch");
        String tounlock = StatCollector.translateToLocal("manual.cavestokingdoms.tounlock");

        manual.fonts.drawString("\u00a7n" + undiscovered, localWidth + 14, localHeight + 4, 0);
        manual.fonts.drawString(pleasetouch, localWidth + 18, localHeight + 16, 0);
        manual.fonts.drawString(tounlock, localWidth + 60, localHeight + 26, 0);

        String nameOfItem = requiredIcon[i].getDisplayName();

        manual.fonts.drawString(nameOfItem, localWidth + 83 - (int) (nameOfItem.length() * 1.7), localHeight + 62, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        // Put it in front of stuff. I think.
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(
            manual.fonts,
            manual.getMC().renderEngine,
            requiredIcon[i],
            localWidth + 75,
            localHeight + 38);

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
