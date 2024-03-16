package talonos.cavestokingdoms.client.pages;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.common.registry.GameRegistry;
import iguanaman.iguanatweakstconstruct.util.HarvestLevels;
import mantle.client.pages.BookPage;

public class ExtMaterialsUsagePage extends BookPage {

    // The title of the page
    String title = "";

    // Itemstacks representing the armors we'll end up drawing on the page.
    List<ItemStack> armors = new ArrayList<ItemStack>();
    List<ItemStack> tools = new ArrayList<ItemStack>();

    // 0 = none, 1 = normal, 2 = requires Smeltery (or wood, or prohibited by
    // executive mandate).
    int toolType = 0;
    int partType = 0;
    int armorType = 0;

    // I think this initializes the instance of a page, given an XML page element.
    @Override
    public void readPageFromXML(Element element) {
        try {
            // System.out.println("Reading page...");
            NodeList nodes = element.getElementsByTagName("title");
            if (nodes != null) {
                title = nodes.item(0)
                    .getTextContent();
            }
            // System.out.println("... Ext 1");
            nodes = element.getElementsByTagName("tools");
            if (nodes != null) {
                if (nodes.item(0)
                    .getNodeType() == Node.ELEMENT_NODE) {
                    Element toolElement = (Element) nodes.item(0);
                    toolType = Integer.parseInt(toolElement.getAttribute("type"));
                    NodeList toolNodes = toolElement.getElementsByTagName("icon");
                    if (toolNodes != null) {
                        for (int x = 0; x < toolNodes.getLength(); x++) {
                            Node n = toolNodes.item(x);
                            String total = n.getTextContent();
                            String mod = total.substring(0, total.indexOf(':'));
                            String itemName = total.substring(total.indexOf(':') + 1);
                            Item iconItem = GameRegistry.findItem(mod, itemName);
                            if (iconItem != null) {
                                tools.add(new ItemStack(GameRegistry.findItem(mod, itemName)));
                            }
                        }
                    }
                }
            }

            nodes = element.getElementsByTagName("mods");
            if (nodes != null) {
                if (nodes.item(0)
                    .getNodeType() == Node.ELEMENT_NODE) {
                    Element partElement = (Element) nodes.item(0);
                    partType = Integer.parseInt(partElement.getAttribute("type"));
                    NodeList toolNodes = partElement.getElementsByTagName("icon");
                    if (toolNodes != null) {
                        for (int x = 0; x < toolNodes.getLength(); x++) {
                            Node n = toolNodes.item(x);
                            String total = n.getTextContent();
                            String mod = total.substring(0, total.indexOf(':'));
                            String itemName = total.substring(total.indexOf(':') + 1);
                            Item iconItem = GameRegistry.findItem(mod, itemName);
                            if (iconItem != null) {
                                tools.add(new ItemStack(GameRegistry.findItem(mod, itemName)));
                            }
                        }
                    }
                }
            }

            nodes = element.getElementsByTagName("armor");
            if (nodes != null) {
                if (nodes.item(0)
                    .getNodeType() == Node.ELEMENT_NODE) {
                    Element armorElement = (Element) nodes.item(0);
                    armorType = Integer.parseInt(armorElement.getAttribute("type"));
                    NodeList armorNodes = armorElement.getElementsByTagName("icon");
                    if (armorNodes != null) {
                        for (int x = 0; x < armorNodes.getLength(); x++) {
                            Node n = armorNodes.item(x);
                            String total = n.getTextContent();
                            String mod = total.substring(0, total.indexOf(':'));
                            String itemName = total.substring(total.indexOf(':') + 1);
                            Item iconItem = GameRegistry.findItem(mod, itemName);
                            if (iconItem != null) {
                                armors.add(new ItemStack(GameRegistry.findItem(mod, itemName)));
                            }
                        }
                    }
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
        String[] toolStrings = new String[3];
        toolStrings[0] = new String("\u00a74Cannot make normal tools!");
        toolStrings[1] = new String("\u00a72Can be used to make normal tools:");
        toolStrings[2] = new String("\u00a74Construction and use of normal tools prohibited:");

        String[] partStrings = new String[3];
        partStrings[0] = new String("\u00a74Cannot be used to make tool parts!");
        partStrings[1] = new String("\u00a72Can be used to make parts in a part builder.");
        partStrings[2] = new String("\u00a72Can be cast into tool parts with a \u00a7lsmeltery.");

        String[] armorStrings = new String[3];
        armorStrings[0] = new String("\u00a74Cannot be used to make armor.");
        armorStrings[1] = new String("\u00a72Can be used to make normal armor:");
        armorStrings[2] = new String("\u00a72Must use \u00a7lLogs\u00a7r\u00a72 to make normal armor.");

        if (isTranslatable) {
            // translation stuff would go in here.
        }
        manual.fonts.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        // Put it in front of stuff. I think.
        manual.renderitem.zLevel = 100;

        for (int tool = 0; tool < tools.size(); tool++) {
            manual.renderitem.renderItemAndEffectIntoGUI(
                manual.fonts,
                manual.getMC().renderEngine,
                tools.get(tool),
                localWidth + 10 + (tool * 22),
                localHeight + 25);
        }

        for (int armor = 0; armor < armors.size(); armor++) {
            manual.renderitem.renderItemAndEffectIntoGUI(
                manual.fonts,
                manual.getMC().renderEngine,
                armors.get(armor),
                localWidth + 6,
                localHeight + 83 + (armor * 18));
        }
        // Tool Description builder:
        String toolDesc1 = "";
        String toolDesc2 = "";
        if (tools.size() != 0 && tools.get(0) != null && toolType != 2) {
            toolDesc1 += "Durability: " + tools.get(0)
                .getMaxDamage();
            if (tools.get(0)
                .getItem() instanceof ItemPickaxe) {
                ItemPickaxe pick = (ItemPickaxe) tools.get(0)
                    .getItem();
                int hlvl = pick.getHarvestLevel(tools.get(0), "pickaxe");
                Item.ToolMaterial mat = Item.ToolMaterial.valueOf(pick.getToolMaterialName());
                toolDesc1 += " - Harvest Level: " + HarvestLevels.getHarvestLevelName(hlvl);
                toolDesc2 += "Speed: " + mat.getEfficiencyOnProperMaterial();
                toolDesc2 += " - Free Ench. Levels: " + mat.getEnchantability();
            }
        }

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        // It seems as though each row is 10 high.
        manual.fonts.drawString(toolStrings[toolType], localWidth, localHeight + 15, 0);

        manual.fonts.drawString(toolDesc1, localWidth, localHeight + 43, 0);
        manual.fonts.drawString(toolDesc2, localWidth, localHeight + 53, 0);
        manual.fonts.drawString(partStrings[partType], localWidth, localHeight + 63, 0);
        manual.fonts.drawString(armorStrings[armorType], localWidth, localHeight + 73, 0);

        for (int armor = 0; armor < armors.size(); armor++) {
            ItemStack armorStack = armors.get(armor);
            if (armorStack.getItem() instanceof ItemArmor) {
                int maxDurability = ((ItemArmor) armorStack.getItem()).getMaxDamage(armorStack);
                int protection = ((ItemArmor) armorStack.getItem()).damageReduceAmount;
                manual.fonts.drawString(
                    "Durability: " + maxDurability + "   Armor Points: " + protection,
                    localWidth + 24,
                    localHeight + 87 + (armor * 18),
                    0);
            }
        }

    }
}
