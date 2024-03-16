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
import iguanaman.iguanatweakstconstruct.override.XPAdjustmentMap;
import iguanaman.iguanatweakstconstruct.util.HarvestLevels;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryPage;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

public class C2KModularToolsPage extends OreDiscoveryPage {

    // The title of the page
    String title = "";

    // The material this page represents.
    ToolMaterial material;

    // Itemstacks representing the icons we'll end up drawing on the page.
    ItemStack[] icons;

    // Description of the material
    String description;

    // What unlocks it?
    String requires;

    // I think this initializes the instance of a page, given an XML page element.
    @Override
    public void readPageFromXML(Element element) {
        icons = new ItemStack[2];

        NodeList nodes = element.getElementsByTagName("title");
        if (nodes != null) {
            title = nodes.item(0)
                .getTextContent();
        }

        nodes = element.getElementsByTagName("text");
        if (nodes != null) {
            description = nodes.item(0)
                .getTextContent();
        }

        nodes = element.getElementsByTagName("requires");
        if (nodes != null && nodes.item(0) != null) {
            requires = nodes.item(0)
                .getTextContent();
        }

        // Get the icons
        icons[0] = new ItemStack(Items.rotten_flesh);
        icons[1] = new ItemStack(Items.rotten_flesh);

        for (int i = 0; i < 2; i++) {
            String nodeToGet = (i == 0 ? "icon" : "requiresIcon");
            nodes = element.getElementsByTagName(nodeToGet);
            if (nodes != null && nodes.item(0) != null
                && nodes.item(0)
                    .getTextContent() != null) // who knows what
                                               // could go wrong?
                                               // :/
            {
                String total = nodes.item(0)
                    .getTextContent();
                String mod = total.substring(0, total.indexOf(':'));
                String itemName = total.substring(total.indexOf(':') + 1);
                int secondColonPosition = itemName.indexOf(':');
                int meta = 0;
                // System.out.println("mod: "+mod+", itemName: "+itemName);
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

        nodes = element.getElementsByTagName("toolmaterial");
        if (nodes != null && nodes.getLength() > 0) {
            material = TConstructRegistry.getMaterial(
                nodes.item(0)
                    .getTextContent());
        } else {
            material = TConstructRegistry.getMaterial(title);
        }

        if (material == null) {
            System.err.println("Warning! " + title + " could not be found as a material!");
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
        System.out.println(requires);
        if (isDiscovered(requires)) {
            drawNormal(localWidth, localHeight);
        } else {
            drawLocked(localWidth, localHeight);
        }
    }

    private void drawNormal(int localWidth, int localHeight) {
        String harvestLevel = StatCollector.translateToLocal("manual.cavestokingdoms.harvestlevel");
        String durability = StatCollector.translateToLocal("manual.cavestokingdoms.durability");
        String handleModifier = StatCollector.translateToLocal("manual.cavestokingdoms.handlemod");
        String miningSpeed = StatCollector.translateToLocal("manual.cavestokingdoms.miningspeed");
        String xpRequired = StatCollector.translateToLocal("manual.cavestokingdoms.xprequired");
        String baseAttack = StatCollector.translateToLocal("manual.cavestokingdoms.attackdamage");
        String heart_ = StatCollector.translateToLocal("manual.cavestokingdoms.heart");
        String hearts = StatCollector.translateToLocal("manual.cavestokingdoms.hearts");
        String materialTrait = StatCollector.translateToLocal("manual.cavestokingdoms.materialtraits");
        String extraMod = StatCollector.translateToLocal("manual.cavestokingdoms.extramod");
        String normal = StatCollector.translateToLocal("manual.cavestokingdoms.normal");

        manual.fonts.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        manual.fonts.drawSplitString(description, localWidth, localHeight + 16, 178, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        // Put it in front of stuff. I think.
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(
            manual.fonts,
            manual.getMC().renderEngine,
            icons[0],
            localWidth + 150,
            localHeight + 75);

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        // It seems as though each row is 10 high.
        if (material != null) {
            manual.fonts.drawString(
                harvestLevel + ": "
                    + material.harvestLevel()
                    + " ("
                    + HarvestLevels.getHarvestLevelName(material.harvestLevel())
                    + "\u00a70)",
                localWidth,
                localHeight + 60,
                0);
            manual.fonts.drawString(durability + ": " + material.durability(), localWidth, localHeight + 70, 0);
            manual.fonts
                .drawString(handleModifier + ": " + material.handleDurability() + "x", localWidth, localHeight + 80, 0);
            manual.fonts.drawString(miningSpeed + ": " + material.toolSpeed() / 100f, localWidth, localHeight + 90, 0);
            int attack = material.attack();
            String heart = (attack == 2 ? " " + heart_ : " " + hearts); // What attention to detail! Thanks,
                                                                        // Slimeknights!
            if (attack % 2 == 0) {
                manual.fonts
                    .drawString(baseAttack + ": " + material.attack() / 2 + heart, localWidth, localHeight + 100, 0);
            } else {
                manual.fonts
                    .drawString(baseAttack + ": " + material.attack() / 2f + heart, localWidth, localHeight + 100, 0);
            }
            manual.fonts.drawString(
                xpRequired + ": " + (XPAdjustmentMap.get(material.materialName) * 100f) + "% " + normal,
                localWidth,
                localHeight + 110,
                0);

            // Here starts a list of attributes.
            int offset = 0;
            String ability = material.ability();
            if (!ability.equals("")) {
                manual.fonts.drawString(materialTrait + ": " + ability, localWidth, localHeight + 125 + 16 * offset, 0);
                offset++;
                if (material.name()
                    .equals("Paper")
                    || material.name()
                        .equals("Thaumium"))
                    manual.fonts.drawString(extraMod, localWidth, localHeight + 120 + 16 * offset, 0);
            }
        }
    }

    private void drawLocked(int localWidth, int localHeight) {
        String undiscovered = StatCollector.translateToLocal("manual.cavestokingdoms.undiscovered");
        String pleasetouch = StatCollector.translateToLocal("manual.cavestokingdoms.pleasetouch");
        String tounlock = StatCollector.translateToLocal("manual.cavestokingdoms.tounlock");

        manual.fonts.drawString("\u00a7n" + undiscovered, localWidth + 14, localHeight + 4, 0);
        manual.fonts.drawString(pleasetouch, localWidth + 18, localHeight + 21, 0);
        manual.fonts.drawString(tounlock, localWidth + 60, localHeight + 32, 0);

        String nameOfItem = icons[1].getDisplayName();

        manual.fonts.drawString(nameOfItem, localWidth + 83 - (int) (nameOfItem.length() * 1.7), localHeight + 94, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        // Put it in front of stuff. I think.
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(
            manual.fonts,
            manual.getMC().renderEngine,
            icons[1],
            localWidth + 75,
            localHeight + 75);

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
