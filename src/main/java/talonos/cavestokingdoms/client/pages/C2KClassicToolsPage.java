package talonos.cavestokingdoms.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.common.registry.GameRegistry;
import iguanaman.iguanatweakstconstruct.util.HarvestLevels;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryPage;

public class C2KClassicToolsPage extends OreDiscoveryPage {

    // The title of the page
    String[] title = new String[2];

    // Itemstacks representing the tools we draw
    ItemStack[][] icons = new ItemStack[5][2];

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
            nodes = element.getElementsByTagName("tools");
            if (nodes != null && nodes.item(i) != null) {
                Node n = nodes.item(i);
                Element toolElement = (Element) n;
                NodeList tools = toolElement.getElementsByTagName("tool");
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
        for (int i = 0; i < 2; i++) {
            if (isDiscovered(requires[i])) {
                drawNormal(localWidth, localHeight + (70 * i), i);
            } else {
                drawLocked(localWidth, localHeight + (70 * i), i);
            }
        }
    }

    private void drawNormal(int localWidth, int localHeight, int i) {
        String harvestLevel = StatCollector.translateToLocal("manual.cavestokingdoms.harvestlevel");
        String durability = StatCollector.translateToLocal("manual.cavestokingdoms.durability");
        String freelevels = StatCollector.translateToLocal("manual.cavestokingdoms.freelevels");
        String miningSpeed = StatCollector.translateToLocal("manual.cavestokingdoms.miningspeed");
        String baseAttack = StatCollector.translateToLocal("manual.cavestokingdoms.attackdamage");
        String heart_ = StatCollector.translateToLocal("manual.cavestokingdoms.heart");
        String hearts = StatCollector.translateToLocal("manual.cavestokingdoms.hearts");

        if (title[i] != null) {
            manual.fonts.drawString("\u00a7n" + title[i], localWidth + 70, localHeight + 4, 0);
        }

        if (description[i] != null && !description[i].equals("")) {
            manual.fonts.drawSplitString(description[i], localWidth, localHeight + 16, 178, 0);
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        manual.renderitem.zLevel = 100;
        ItemPickaxe pickItem = null;

        for (int j = 0; j < 5; j++) {
            if (icons[j][i] != null) {
                manual.renderitem.renderItemAndEffectIntoGUI(
                    manual.fonts,
                    manual.getMC().renderEngine,
                    icons[j][i],
                    localWidth + 22 * j,
                    localHeight + 28);
                if (icons[j][i].getItem() instanceof ItemPickaxe) {
                    pickItem = (ItemPickaxe) icons[j][i].getItem();
                }
            }
        }

        String toolDesc1 = "";
        String toolDesc2 = "";

        if (pickItem != null) {
            Item.ToolMaterial mat = Item.ToolMaterial.valueOf(pickItem.getToolMaterialName());

            int hLevel = mat.getHarvestLevel();
            int dura = pickItem.getMaxDamage();
            int levels = mat.getEnchantability();
            float speed = mat.getEfficiencyOnProperMaterial();
            float attack = mat.getDamageVsEntity();

            toolDesc1 += durability + ": " + dura;
            toolDesc1 += " - " + harvestLevel + ": " + HarvestLevels.getHarvestLevelName(hLevel);
            toolDesc2 += miningSpeed + ": " + speed;
            toolDesc2 += " - " + freelevels + ": " + levels;
        }

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        manual.fonts.drawString(toolDesc1, localWidth, localHeight + 45, 0);
        manual.fonts.drawString(toolDesc2, localWidth, localHeight + 55, 0);
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
