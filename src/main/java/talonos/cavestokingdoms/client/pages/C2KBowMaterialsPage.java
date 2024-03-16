package talonos.cavestokingdoms.client.pages;

import java.util.HashMap;
import java.util.Map;

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
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryPage;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.ToolMaterial;

public class C2KBowMaterialsPage extends OreDiscoveryPage {

    private static final int NUMBER_OF_MATS_PER_PAGE = 2;
    private static final int MAT_OFFSET = 78;

    // The materials this page depicts.
    private ToolMaterial[] materials = new ToolMaterial[NUMBER_OF_MATS_PER_PAGE];
    private BowMaterial[] bowMaterials = new BowMaterial[NUMBER_OF_MATS_PER_PAGE];
    private ArrowMaterial[] arrowMaterials = new ArrowMaterial[NUMBER_OF_MATS_PER_PAGE];
    private String[] matNames = new String[NUMBER_OF_MATS_PER_PAGE];
    private String[] description = new String[NUMBER_OF_MATS_PER_PAGE];

    private String[] requires = new String[NUMBER_OF_MATS_PER_PAGE];
    private ItemStack[] requiredIcon = new ItemStack[NUMBER_OF_MATS_PER_PAGE];

    // Itemstacks representing the icons we'll end up drawing on the page.
    private ItemStack[] icons = new ItemStack[NUMBER_OF_MATS_PER_PAGE];
    private int[] iconMetadata = new int[NUMBER_OF_MATS_PER_PAGE];

    public static HashMap<String, Integer> mappings = new HashMap();
    public static boolean init = false;

    public static void init() {
        for (Map.Entry<Integer, ToolMaterial> entry : TConstructRegistry.toolMaterials.entrySet()) {
            mappings.put(entry.getValue().materialName, entry.getKey());
            init = true;
        }
    }

    // I think this initializes the instance of a page, given an XML page element.
    @Override
    public void readPageFromXML(Element element) {
        if (!init) {
            init();
        }

        try {
            for (int i = 0; i < 2; i++) {
                NodeList nodes = element.getElementsByTagName("mat");
                if (nodes != null && nodes.item(i) != null) {
                    matNames[i] = nodes.item(i)
                        .getTextContent();
                }
                nodes = element.getElementsByTagName("text");
                if (nodes != null && nodes.item(i) != null) {
                    description[i] = nodes.item(i)
                        .getTextContent();
                }

                nodes = element.getElementsByTagName("requires");
                if (nodes != null && nodes.item(i) != null) {
                    requires[i] = nodes.item(i)
                        .getTextContent();
                }

                nodes = element.getElementsByTagName("icon");
                if (nodes != null && nodes.item(i) != null
                    && nodes.item(i)
                        .getTextContent() != null) {
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
                        icons[i] = new ItemStack(GameRegistry.findItem(mod, itemName), 1, meta);
                    }
                }

                requiredIcon[i] = new ItemStack(Items.rotten_flesh);

                nodes = element.getElementsByTagName("requiresIcon");
                if (nodes != null && nodes.item(i) != null
                    && nodes.item(i)
                        .getTextContent() != null) {
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

                // Get the material
                nodes = element.getElementsByTagName("toolmaterial");
                if (nodes != null && nodes.getLength() > 0) {
                    System.out.println("Getting from Materials");
                    materials[i] = TConstructRegistry.getMaterial(
                        nodes.item(i)
                            .getTextContent());
                    if (materials[i] != null) {
                        bowMaterials[i] = TConstructRegistry.getBowMaterial(mappings.get(materials[i].name()));
                        arrowMaterials[i] = TConstructRegistry.getArrowMaterial(mappings.get(materials[i].name()));
                    }
                } else {
                    materials[i] = TConstructRegistry.getMaterial(matNames[i]);
                    if (materials[i] != null) {
                        bowMaterials[i] = TConstructRegistry.getBowMaterial(mappings.get(materials[i].name()));
                        arrowMaterials[i] = TConstructRegistry.getArrowMaterial(mappings.get(materials[i].name()));
                    }
                }

                if (materials[i] == null) {
                    System.err.println("Warning! " + matNames[i] + " could not be found as a material!");
                    // material = TConstructRegistry.getMaterial("Stone");
                }

            }
        } catch (Exception e) {
            System.out.println("An exception: ");
            e.printStackTrace();
        }
    }

    @Override
    public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
        // System.out.println("Req0"+requires[0]+"req1"+requires[1]);
        // System.out.println("Req0"+requiredIcon[0]+"req1"+requiredIcon[1]);
        for (int i = 0; i < NUMBER_OF_MATS_PER_PAGE; i++) {
            if (isDiscovered(requires[i])) {
                renderUnlocked(localWidth, localHeight + (MAT_OFFSET * i), i);
            } else {
                drawLocked(localWidth, localHeight + (MAT_OFFSET * i), i);
            }
        }
    }

    private void renderUnlocked(int localWidth, int localHeight, int i) {
        String durability = new String("Durability");
        String drawSpeed = new String("Draw Delay");
        String flightSpeed = new String("Launch Strength");
        String xpRequired = new String("XP Required");
        String mass = new String("Mass");
        String breakChance = new String("Break Chance");
        String baseAttack = new String("Attack Damage");
        String heart_ = new String("Heart");
        String hearts = new String("Hearts");

        if (materials[i] != null) {
            manual.fonts.drawString("\u00a7n" + matNames[i], localWidth + 45, localHeight + 4, 0);
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        // Put it in front of stuff. I think.
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(
            manual.fonts,
            manual.getMC().renderEngine,
            icons[i],
            localWidth + 150,
            localHeight + 4);

        // Switch back to normal layer.
        manual.renderitem.zLevel = 0;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        // It seems as though each row is 10 high.

        boolean bowsOn = bowMaterials[i] != null;
        boolean arrowsOn = arrowMaterials[i] != null;

        if (materials[i] != null) {
            manual.fonts.drawString(durability + ": " + materials[i].durability(), localWidth, localHeight + 20, 0);
            if (bowsOn) {
                manual.fonts.drawString(drawSpeed + ": " + bowMaterials[i].drawspeed, localWidth, localHeight + 30, 0);
                manual.fonts
                    .drawString(flightSpeed + ": " + bowMaterials[i].flightSpeedMax, localWidth, localHeight + 40, 0);
            }
            manual.fonts.drawString(
                xpRequired + ": " + (XPAdjustmentMap.get(materials[i].materialName) * 100f) + "% ",
                localWidth,
                localHeight + 50,
                0);

            manual.fonts.drawString(description[i], localWidth, localHeight + 65, 0);

            if (arrowsOn) {
                manual.fonts.drawString(mass + ": " + arrowMaterials[i].mass, localWidth + 85, localHeight + 20, 0);
                // So, it's dumb, but break chance is multipled by .15 before actually being
                // applied.
                // And that's just with wood. Other materials have other break rates.
                // We'll use wood as the staple here.
                manual.fonts.drawString(
                    breakChance + ": " + (arrowMaterials[i].breakChance * 15f) + "% ",
                    localWidth + 85,
                    localHeight + 30,
                    0);
            }

            // Attack is weird.
            int attack = materials[i].attack();
            String heart = (attack == 2 ? " " + heart_ : " " + hearts);
            // Only make it a decimal if it's not an even heart.
            String totalString = baseAttack + ": "
                + (attack % 2 == 0 ? materials[i].attack() / 2 : materials[i].attack() / 2f)
                + heart;
            manual.fonts.drawString(totalString, localWidth + 85, localHeight + 40, 0);
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
