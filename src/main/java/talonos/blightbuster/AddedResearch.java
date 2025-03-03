package talonos.blightbuster;

import java.text.DecimalFormat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.handlers.TalonosWandTriggerManager;
import talonos.blightbuster.items.BBItems;
import talonos.blightbuster.items.ItemPurityFocus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class AddedResearch {

    static ShapedArcaneRecipe purityFocusRecipe;

    public static void initResearch() {
        String category = "ALCHEMY";
        if (BlightbusterConfig.createThaumTab) {
            category = BlightBuster.MODID.toUpperCase();
            ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
            ResearchCategories.registerCategory(
                category,
                new ResourceLocation("blightbuster", "textures/items/purityFocus.png"),
                background);

        }

        if (BlightbusterConfig.enableSilverPotion) {
            // Creates the silverleaf potion recipe
            ShapedArcaneRecipe silverPotRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
                "SILVERPOTION",
                new ItemStack(BBItems.silverPotion, 6),
                new AspectList().add(Aspect.WATER, 15)
                    .add(Aspect.ORDER, 24),
                "FLF",
                'L',
                new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, 1),
                'F',
                new ItemStack(Items.glass_bottle));

            ResearchItem silverPotResearch = new ResearchItem(
                "SILVERPOTION", // research name
                category, // thaumonomicon page
                new AspectList().add(Aspect.WATER, 3)
                    .add(Aspect.HEAL, 6)
                    .add(Aspect.TAINT, 3)
                    .add(Aspect.PLANT, 3),
                -1,
                -4,
                2, // location in thaumonomicon
                new ItemStack(BBItems.silverPotion)); // research icon

            silverPotResearch
                .setPages(new ResearchPage("tc.research_page.SILVERPOTION.1"), new ResearchPage(silverPotRecipe));

            silverPotResearch.setConcealed(); // sets as hidden in thaumonomicon
            silverPotResearch.setParents("ETHEREALBLOOM"); // sets requirements for research
            silverPotResearch.registerResearchItem(); // actually registers it
        }

        if (BlightbusterConfig.enablePurityFocus) {
            purityFocusRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
                "PURITYFOCUS",
                new ItemStack(BBItems.purityFocus),
                new AspectList().add(Aspect.WATER, 5)
                    .add(Aspect.ORDER, 8),
                "SQS",
                "QFQ",
                "SQS",
                'S',
                new ItemStack(ConfigItems.itemShard, 1, 4),
                'Q',
                new ItemStack(Items.quartz),
                'F',
                new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4));

            ResearchItem purityFocusResearch = new ResearchItem(
                "PURITYFOCUS",
                category,
                new AspectList().add(Aspect.TOOL, 3)
                    .add(Aspect.HEAL, 6)
                    .add(Aspect.TAINT, 3)
                    .add(Aspect.MAGIC, 3),
                -3,
                -4,
                2,
                new ItemStack(BBItems.purityFocus));

            setPurityFocusPages(purityFocusResearch, purityFocusRecipe);

            purityFocusResearch.setConcealed();
            purityFocusResearch.setParents("ETHEREALBLOOM");
            purityFocusResearch.registerResearchItem();
        }

        if (BlightbusterConfig.enableDawnTotem) {
            InfusionRecipe dawnTotemRecipe = ThaumcraftApi.addInfusionCraftingRecipe(
                "DAWNTOTEM",
                new ItemStack(BBBlock.dawnTotem),
                6,
                new AspectList().add(Aspect.AURA, 16)
                    .add(Aspect.HEAL, 32)
                    .add(Aspect.LIFE, 48)
                    .add(Aspect.LIGHT, 16)
                    .add(Aspect.ARMOR, 32)
                    .add(Aspect.ORDER, 48),
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1), // center item, silverwood log
                new ItemStack[] { // items in recipe
                    new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4),
                    new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4),
                    new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3),
                    new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3),
                    new ItemStack(ConfigBlocks.blockCrystal, 1, 4) });

            ResearchItem dawnTotemResearch = new ResearchItem(
                "DAWNTOTEM",
                category,
                new AspectList().add(Aspect.AURA, 6)
                    .add(Aspect.HEAL, 8)
                    .add(Aspect.TAINT, 3)
                    .add(Aspect.MAGIC, 6),
                -2,
                -6,
                2,
                new ItemStack(BBBlock.dawnTotem));

            dawnTotemResearch
                .setPages(new ResearchPage("tc.research_page.DAWNTOTEM.1"), new ResearchPage(dawnTotemRecipe));

            dawnTotemResearch.setConcealed();
            dawnTotemResearch.setParents("SILVERPOTION", "PURITYFOCUS");
            dawnTotemResearch.registerResearchItem();
        }

        if (BlightbusterConfig.enableDawnMachine) {
            InfusionRecipe dawnMachineRecipe = ThaumcraftApi.addInfusionCraftingRecipe(
                "DAWNMACHINE",
                new ItemStack(BBBlock.cyberTotem),
                10,
                new AspectList().add(Aspect.MECHANISM, 64)
                    .add(Aspect.AIR, 64)
                    .add(Aspect.FIRE, 64)
                    .add(Aspect.ORDER, 64)
                    .add(Aspect.TREE, 64)
                    .add(Aspect.AURA, 64)
                    .add(Aspect.MIND, 64)
                    .add(Aspect.PLANT, 64)
                    .add(Aspect.HEAL, 64)
                    .add(Aspect.VOID, 64),
                new ItemStack(BBBlock.dawnTotem, 1),
                new ItemStack[] { new ItemStack(ConfigBlocks.blockJar), new ItemStack(ConfigBlocks.blockJar),
                    new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4),
                    new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4), new ItemStack(BBBlock.dawnTotem),
                    new ItemStack(BBBlock.dawnTotem), new ItemStack(BBItems.purityFocus),
                    new ItemStack(BBItems.purityFocus) });

            ResearchItem dawnMachineResearch = new ResearchItem(
                "DAWNMACHINE",
                category,
                new AspectList().add(Aspect.MECHANISM, 32)
                    .add(Aspect.AIR, 4)
                    .add(Aspect.FIRE, 4)
                    .add(Aspect.ORDER, 4)
                    .add(Aspect.TREE, 4)
                    .add(Aspect.AURA, 4)
                    .add(Aspect.MIND, 4)
                    .add(Aspect.PLANT, 4)
                    .add(Aspect.HEAL, 4)
                    .add(Aspect.VOID, 4)
                    .add(Aspect.CRAFT, 4)
                    .add(Aspect.ENERGY, 4),
                -2,
                -8,
                -4,
                new ItemStack(BBBlock.cyberTotem));

            dawnMachineResearch.setPages(
                new ResearchPage("tc.research_page.DAWNMACHINE.1"),
                new ResearchPage(dawnMachineRecipe),
                new ResearchPage("tc.research_page.DAWNMACHINE.2"),
                new ResearchPage("tc.research_page.DAWNMACHINE.3"),
                new ResearchPage("tc.research_page.DAWNMACHINE.4"),
                new ResearchPage("tc.research_page.DAWNMACHINE.5"),
                new ResearchPage("tc.research_page.DAWNMACHINE.6"),
                new ResearchPage("tc.research_page.DAWNMACHINE.7"),
                new ResearchPage("tc.research_page.DAWNMACHINE.8"),
                new ResearchPage("tc.research_page.DAWNMACHINE.9"));
            dawnMachineResearch.setConcealed();
            dawnMachineResearch.setParents("DAWNTOTEM");
            dawnMachineResearch.registerResearchItem();

            if (BlightbusterConfig.enableDawnOffering) {
                InfusionRecipe dawnOfferingRecipe = ThaumcraftApi.addInfusionCraftingRecipe(
                    "DAWNOFFERING",
                    new ItemStack(BBBlock.offering, 8),
                    4,
                    new AspectList().add(Aspect.MOTION, 64)
                        .add(Aspect.ELDRITCH, 32),
                    new ItemStack(Items.diamond, 1),
                    new ItemStack[] { new ItemStack(Blocks.obsidian, 1), new ItemStack(Blocks.obsidian, 1),
                        new ItemStack(Blocks.obsidian, 1), new ItemStack(Blocks.obsidian, 1),
                        new ItemStack(Blocks.obsidian, 1), new ItemStack(Blocks.obsidian, 1),
                        new ItemStack(Blocks.obsidian, 1), new ItemStack(Blocks.obsidian, 1) });

                ResearchItem dawnOfferingResearch = new ResearchItem(
                    "DAWNOFFERING",
                    category,
                    new AspectList().add(Aspect.MECHANISM, 16)
                        .add(Aspect.MOTION, 4)
                        .add(Aspect.ELDRITCH, 4),
                    -4,
                    -8,
                    4,
                    new ItemStack(BBBlock.offering));

                dawnOfferingResearch.setPages(
                    new ResearchPage("tc.research_page.DAWNOFFERING.1"),
                    new ResearchPage(dawnOfferingRecipe));
                dawnOfferingResearch.setConcealed();
                dawnOfferingResearch.setParents("DAWNMACHINE");
                dawnOfferingResearch.registerResearchItem();
            }
        }

        if (BlightbusterConfig.enableDawnCharger) {
            ShapedArcaneRecipe dawnChargerRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
                "DAWNCHARGER",
                new ItemStack(BBBlock.dawnCharger),
                new AspectList().add(Aspect.WATER, 25)
                    .add(Aspect.ORDER, 25)
                    .add(Aspect.ENTROPY, 25)
                    .add(Aspect.FIRE, 25)
                    .add(Aspect.EARTH, 25)
                    .add(Aspect.AIR, 25),
                "SSS",
                "SLS",
                "SSS",
                'S',
                new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1),
                'L',
                new ItemStack(ConfigItems.itemFocusPrimal, 1));

            ResearchItem dawnChargerResearch = new ResearchItem(
                "DAWNCHARGER",
                category,
                new AspectList().add(Aspect.ORDER, 4)
                    .add(Aspect.VOID, 5)
                    .add(Aspect.MECHANISM, 8)
                    .add(Aspect.ENERGY, 4)
                    .add(Aspect.MAGIC, 8),
                -4,
                2,
                -7,
                new ItemStack(BBBlock.dawnCharger));

            dawnChargerResearch
                .setPages(new ResearchPage("tc.research_page.DAWNCHARGER.1"), new ResearchPage(dawnChargerRecipe));
            dawnChargerResearch.setConcealed();
            dawnChargerResearch.setParents(BlightbusterConfig.enableDawnOffering ? "DAWNOFFERING" : "DAWNMACHINE");
            dawnChargerResearch.registerResearchItem();
        }
    }

    // Used to rebuild formatted research on language change.
    public static void setPurityFocusPages(ResearchItem purityFocusResearch, ShapedArcaneRecipe purityFocusRecipe) {
        DecimalFormat formatter = new DecimalFormat("#####.##");
        purityFocusResearch.setPages(
            new ResearchPage(
                StatCollector.translateToLocalFormatted(
                    "tc.research_page.PURITYFOCUS.1",
                    formatter.format(ItemPurityFocus.blockCost.getAmount(Aspect.EARTH) / 100F),
                    formatter.format(ItemPurityFocus.blockCost.getAmount(Aspect.ORDER) / 100F))),
            new ResearchPage(
                StatCollector.translateToLocalFormatted(
                    "tc.research_page.PURITYFOCUS.2",
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.EARTH) / 100F),
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.WATER) / 100F),
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.ORDER) / 100F),
                    formatter.format(ItemPurityFocus.nodeCost.getAmount(Aspect.EARTH) / 100F),
                    formatter.format(ItemPurityFocus.nodeCost.getAmount(Aspect.ORDER) / 100F))),
            new ResearchPage(purityFocusRecipe),
            new ResearchPage("FOCALMANIPULATION", "tc.research_page.PURITYFOCUS.basic_upgrades"),
            new ResearchPage("FOCALMANIPULATION", "tc.research_page.PURITYFOCUS.architect"),
            new ResearchPage(
                "FOCALMANIPULATION",
                StatCollector.translateToLocalFormatted(
                    "tc.research_page.PURITYFOCUS.vacuum",
                    formatter.format(ItemPurityFocus.vacuumCost.getAmount(Aspect.AIR) / 100F),
                    formatter.format(ItemPurityFocus.vacuumCost.getAmount(Aspect.ENTROPY) / 100F))),
            new ResearchPage("FOCALMANIPULATION", "tc.research_page.PURITYFOCUS.node"),
            new ResearchPage(
                "FOCALMANIPULATION",
                StatCollector.translateToLocalFormatted(
                    "tc.research_page.PURITYFOCUS.curative",
                    formatter.format(BlightbusterConfig.healStrength / 2F),
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.EARTH) / 100F),
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.WATER) / 100F),
                    formatter.format(ItemPurityFocus.healCost.getAmount(Aspect.ORDER) / 100F))),
            new ResearchPage(
                "FOCALMANIPULATION",
                StatCollector.translateToLocalFormatted(
                    "tc.research_page.PURITYFOCUS.blightBuster",
                    formatter.format(BlightbusterConfig.attackStrength / 2F),
                    formatter.format(ItemPurityFocus.attackCost.getAmount(Aspect.FIRE) / 100F),
                    formatter.format(ItemPurityFocus.attackCost.getAmount(Aspect.ENTROPY) / 100F))));
    }

    public static void initWandHandler() {
        TalonosWandTriggerManager wandTrigger = new TalonosWandTriggerManager();

        // Registers wand triggers, e.g. on right click
        // First parameter is the trigger, second parameter is the event number, third
        // is the block, fourth is the block metadata, fifth is the mod id

        if (BlightbusterConfig.enableDawnMachine) {
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 0, BBBlock.cyberTotem, -1, "blightbuster");
            WandTriggerRegistry
                .registerWandBlockTrigger(wandTrigger, 0, ConfigBlocks.blockMagicalLog, 1, "blightbuster");
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachine, -1, "blightbuster");
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachineInput, -1, "blightbuster");
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachineBuffer, -1, "blightbuster");
        }
        if (BlightbusterConfig.enableDawnOffering) {
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 2, BBBlock.offering, -1, "blightbuster");
        }
        if (BlightbusterConfig.enableDawnCharger) {
            WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 3, BBBlock.dawnCharger, -1, "blightbuster");
        }
    }

}
