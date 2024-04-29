package talonos.blightbuster;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.handlers.TalonosWandTriggerManager;
import talonos.blightbuster.items.BBItems;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class AddedResearch {

    public static void initResearch() {
        /*
         * ShapedArcaneRecipe dawnTotemRecipie =
         * ThaumcraftApi.addArcaneCraftingRecipe("DAWNTOTEM", new
         * ItemStack(CtKBlock.dawnTotem), new AspectList().add(Aspect.EARTH,
         * 60).add(Aspect.ORDER, 60), new Object[] {
         * "SSS",
         * "SFS",
         * "SSS",
         * 'S', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7),
         * 'F', new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4)
         * });
         */
        // ResearchCategories.registerCategory("ANTITAINT, arg1, arg2);

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

        ShapedArcaneRecipe purityFocusRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
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
                new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4), new ItemStack(ConfigBlocks.blockCustomPlant, 1, 4),
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3),
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3), new ItemStack(ConfigBlocks.blockCrystal, 1, 4) });

        // ShapedArcaneRecipe dawnChargerRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
        // "DAWNCHARGER",
        // new ItemStack(BBBlock.dawnCharger),
        // new AspectList().add(Aspect.WATER, 25)
        // .add(Aspect.ORDER, 25)
        // .add(Aspect.ENTROPY, 25)
        // .add(Aspect.FIRE, 25)
        // .add(Aspect.EARTH, 25)
        // .add(Aspect.AIR, 25),
        // "SSS",
        // "SLS",
        // "SSS",
        // 'S',
        // new ItemStack(ConfigBlocks.blockMagicalLog, 1, 1),
        // 'L',
        // new ItemStack(TEBlocks.blockFrame, 1, 7));

        ResearchItem silverPotResearch = new ResearchItem(
            "SILVERPOTION", // research name
            "ALCHEMY", // thaumonomicon page
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

        ResearchItem purityFocusResearch = new ResearchItem(
            "PURITYFOCUS",
            "ALCHEMY",
            new AspectList().add(Aspect.TOOL, 3)
                .add(Aspect.HEAL, 6)
                .add(Aspect.TAINT, 3)
                .add(Aspect.MAGIC, 3),
            -3,
            -4,
            2,
            new ItemStack(BBItems.purityFocus));

        purityFocusResearch
            .setPages(new ResearchPage("tc.research_page.PURITYFOCUS.1"), new ResearchPage(purityFocusRecipe));

        purityFocusResearch.setConcealed();
        purityFocusResearch.setParents("ETHEREALBLOOM");
        purityFocusResearch.registerResearchItem();

        ResearchItem dawnTotemResearch = new ResearchItem(
            "DAWNTOTEM",
            "ALCHEMY",
            new AspectList().add(Aspect.AURA, 6)
                .add(Aspect.HEAL, 8)
                .add(Aspect.TAINT, 3)
                .add(Aspect.MAGIC, 6),
            -2,
            -6,
            2,
            new ItemStack(BBBlock.dawnTotem));

        dawnTotemResearch.setPages(new ResearchPage("tc.research_page.DAWNTOTEM.1"), new ResearchPage(dawnTotemRecipe));

        dawnTotemResearch.setConcealed();
        dawnTotemResearch.setParents("SILVERPOTION", "PURITYFOCUS");
        dawnTotemResearch.registerResearchItem();

        ResearchItem dawnChargerResearch = new ResearchItem(
            "DAWNCHARGER",
            "ALCHEMY",
            new AspectList().add(Aspect.ORDER, 4)
                .add(Aspect.VOID, 5)
                .add(Aspect.MECHANISM, 8)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.MAGIC, 8),
            -4,
            -7,
            2,
            new ItemStack(BBBlock.dawnCharger));

        dawnChargerResearch
            .setPages(new ResearchPage("tc.research_page.DAWNCHARGER.1")/* , new ResearchPage(dawnChargerRecipe) */);

        dawnChargerResearch.setConcealed();
        dawnChargerResearch.setParents("DAWNOFFERING");
        dawnChargerResearch.registerResearchItem();

        TalonosWandTriggerManager wandTrigger = new TalonosWandTriggerManager();

        // Registers wand triggers, e.g. on right click
        // First parameter is the trigger, second parameter is the event number, third
        // is the block, fourth is the block metadata, fifth is the mod id
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 0, BBBlock.cyberTotem, -1, "cavestokingdoms");
        WandTriggerRegistry
            .registerWandBlockTrigger(wandTrigger, 0, ConfigBlocks.blockMagicalLog, 1, "cavestokingdoms");
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachine, -1, "cavestokingdoms");
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachineInput, -1, "cavestokingdoms");
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 1, BBBlock.dawnMachineBuffer, -1, "cavestokingdoms");
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 2, BBBlock.offering, -1, "cavestokingdoms");
        WandTriggerRegistry.registerWandBlockTrigger(wandTrigger, 3, BBBlock.dawnCharger, -1, "cavestokingdoms");
    }

}
