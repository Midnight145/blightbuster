package talonos.cavestokingdoms.client.pages.orediscovery;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import talonos.cavestokingdoms.client.pages.orediscovery.entries.IDiscoveryEntry;
import talonos.cavestokingdoms.client.pages.orediscovery.entries.ItemDiscoveryEntry;
import talonos.cavestokingdoms.client.pages.orediscovery.entries.OreDictionaryDiscoveryEntry;
import talonos.cavestokingdoms.network.CavesToKingdomsNetwork;
import talonos.cavestokingdoms.network.packets.AddDiscoveryPacket;

public class OreDiscoveryRegistry {

    private static OreDiscoveryRegistry instance = null;

    public static OreDiscoveryRegistry getInstance() {
        if (instance == null) instance = new OreDiscoveryRegistry();
        return instance;
    }

    private List<IDiscoveryEntry> discoverData = new ArrayList<IDiscoveryEntry>();

    public OreDiscoveryRegistry() {
        // Handle registry code.

        // Register all metals, first by ingot, then nugget, then block, then ore.
        // This will lead to some impossible combinations (Like void metal blocks or
        // damascus steel ore) but it should still work because of the OreDictionary
        registerAllMetalsWith("ingot");
        registerAllMetalsWith("block");
        registerAllMetalsWith("nugget");
        registerAllMetalsWith("ore");

        // Register some gems and stuff
        registerDiscovery("crystalCertusQuartz", "discover.cavestokingdoms.certusquartz");
        registerDiscovery("crystalNetherQuartz", "discover.cavestokingdoms.netherquartz");
        registerDiscovery("gemAmethyst", "discover.cavestokingdoms.amethyst");
        registerDiscovery("gemRuby", "discover.cavestokingdoms.ruby");
        registerDiscovery("gemSaphire", "discover.cavestokingdoms.sapphire");
        registerDiscovery("gravel", "discover.cavestokingdoms.gravel");
        registerDiscovery("netherrack", "discover.cavestokingdoms.netherrack");
        registerDiscovery("obsidian", "discover.cavestokingdoms.obsidian");

        // Register Wood. Finally
        registerDiscovery("logWood", "discover.cavestokingdoms.wood");
        registerDiscovery("plankWood", "discover.cavestokingdoms.wood");
        registerDiscovery("woodLog", "discover.cavestokingdoms.wood");

        // Some things lack oredict entries.
        registerDiscovery(
            GameRegistry.findItem("ExtraUtilities", "bedrockiumIngot"),
            "discover.cavestokingdoms.bedrockium");
        registerDiscovery(GameRegistry.findItem("Natura", "bloodwood"), 0, "discover.cavestokingdoms.bloodwood");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 4, "discover.cavestokingdoms.bloodwood");
        registerDiscovery(GameRegistry.findItem("Tconstruct", "strangeFood"), 1, "discover.cavestokingdoms.blueslime");
        registerDiscovery(GameRegistry.findItem("minecraft", "dye"), 15, "discover.cavestokingdoms.bonemeal");
        registerDiscovery(GameRegistry.findItem("minecraft", "cactus"), "discover.cavestokingdoms.cactus");
        registerDiscovery(
            GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"),
            1,
            "discover.cavestokingdoms.certusquartz");
        registerDiscovery(GameRegistry.findItem("minecraft", "chainmail_helmet"), "discover.cavestokingdoms.chain");
        registerDiscovery(GameRegistry.findItem("minecraft", "chainmail_chestplate"), "discover.cavestokingdoms.chain");
        registerDiscovery(GameRegistry.findItem("minecraft", "chainmail_leggings"), "discover.cavestokingdoms.chain");
        registerDiscovery(GameRegistry.findItem("minecraft", "chainmail_boots"), "discover.cavestokingdoms.chain");
        registerDiscovery(GameRegistry.findItem("Thaumcraft", "ItemBootsCultist"), "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemChestplateCultistLeaderPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemChestplateCultistPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemChestplateCultistRobe"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemHelmetCultistLeaderPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemHelmetCultistPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemHelmetCultistRobe"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemLeggingsCultistLeaderPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemLeggingsCultistPlate"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemLeggingsCultistRobe"),
            "discover.cavestokingdoms.crimson");
        registerDiscovery(GameRegistry.findItem("Natura", "Dark Tree"), 0, "discover.cavestokingdoms.darkwood");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 11, "discover.cavestokingdoms.darkwood");
        registerDiscovery(GameRegistry.findItem("Natura", "Dark Tree"), 1, "discover.cavestokingdoms.fusewood");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 12, "discover.cavestokingdoms.fusewood");
        registerDiscovery(GameRegistry.findItem("Natura", "tree"), 2, "discover.cavestokingdoms.ghostwood");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 2, "discover.cavestokingdoms.ghostwood");
        registerDiscovery(GameRegistry.findItem("Tconstruct", "materials"), 36, "discover.cavestokingdoms.glue");
        registerDiscovery(GameRegistry.findItem("minecraft", "slime_ball"), "discover.cavestokingdoms.greenslime");
        registerDiscovery(
            GameRegistry.findItem("ThaumicTinkerer", "kamiResource"),
            1,
            "discover.cavestokingdoms.ichorcloth");
        registerDiscovery(GameRegistry.findItem("minecraft", "leather"), "discover.cavestokingdoms.leather");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemResource"),
            7,
            "discover.cavestokingdoms.magiccloth");
        registerDiscovery(GameRegistry.findItem("ExtraTiC", "chunk"), 165, "discover.cavestokingdoms.peridot");
        registerDiscovery(GameRegistry.findItem("minecraft", "reeds"), "discover.cavestokingdoms.reeds");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcNanorumChest"), "discover.cavestokingdoms.scout");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcSoldierLegs"), "discover.cavestokingdoms.scout");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcSoldierHead"), "discover.cavestokingdoms.scout");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcWizardHead"), "discover.cavestokingdoms.wizard");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcWizardChest"), "discover.cavestokingdoms.wizard");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcWizardPants"), "discover.cavestokingdoms.wizard");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcAssassinHead"), "discover.cavestokingdoms.assassin");
        registerDiscovery(GameRegistry.findItem("customnpcs", "npcAssassinBoots"), "discover.cavestokingdoms.assassin");
        registerDiscovery(
            GameRegistry.findItem("customnpcs", "npcAssassinLeggings"),
            "discover.cavestokingdoms.assassin");
        registerDiscovery(
            GameRegistry.findItem("MineFactoryReloaded", "pinkslime"),
            "discover.cavestokingdoms.pinkslime");
        registerDiscovery(
            GameRegistry.findItem("MineFactoryReloaded", "rubber.raw"),
            "discover.cavestokingdoms.plastic");
        registerDiscovery(
            GameRegistry.findItem("MineFactoryReloaded", "plastic.raw"),
            "discover.cavestokingdoms.plastic");
        registerDiscovery(
            GameRegistry.findItem("MineFactoryReloaded", "plastic.sheets"),
            "discover.cavestokingdoms.plastic");

        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "blockStoneDevice"),
            2,
            "discover.cavestokingdoms.enhancedthaumium");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemSwordElemental"),
            "discover.cavestokingdoms.enhancedthaumium");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemAxeElemental"),
            "discover.cavestokingdoms.enhancedthaumium");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemPickaxeElemental"),
            "discover.cavestokingdoms.enhancedthaumium");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemHoeElemental"),
            "discover.cavestokingdoms.enhancedthaumium");
        registerDiscovery(
            GameRegistry.findItem("Thaumcraft", "ItemShovelElemental"),
            "discover.cavestokingdoms.enhancedthaumium");

        registerDiscovery(
            GameRegistry.findItem("ForbiddenMagic", "NetherShard"),
            1,
            "discover.cavestokingdoms.chameleon");
        registerDiscovery(
            GameRegistry.findItem("ThaumicTinkerer", "kamiResource"),
            2,
            "discover.cavestokingdoms.ichorium");
        registerDiscovery(
            GameRegistry.findItem("ExtraUtilities", "unstableingot"),
            0,
            "discover.cavestokingdoms.unstable");
        registerDiscovery(
            GameRegistry.findItem("ExtraUtilities", "decorativeBlock1"),
            8,
            "discover.cavestokingdoms.magicalwood");

        registerDiscovery(GameRegistry.findItem("AWWayofTime", "masterStone"), "discover.cavestokingdoms.bound");
        registerDiscovery(GameRegistry.findItem("AWWayofTime", "energySword"), "discover.cavestokingdoms.bound");
        registerDiscovery(GameRegistry.findItem("AWWayofTime", "boundPickaxe"), "discover.cavestokingdoms.bound");
        registerDiscovery(GameRegistry.findItem("AWWayofTime", "boundAxe"), "discover.cavestokingdoms.bound");
        registerDiscovery(GameRegistry.findItem("AWWayofTime", "boundShovel"), "discover.cavestokingdoms.bound");

        // Now do it all again, but for the mining levels.
        registerDiscovery("crystalCertusQuartz", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery("crystalNetherQuartz", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery("gemAmethyst", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery("gemRuby", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery("gemSaphire", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery("gravel", "discover.cavestokingdoms.harvestlevel1");
        registerDiscovery("netherrack", "discover.cavestokingdoms.harvestlevel1");
        registerDiscovery("obsidian", "discover.cavestokingdoms.harvestlevel4");

        // Some things lack oredict entries.
        registerDiscovery(GameRegistry.findItem("Natura", "bloodwood"), 0, "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 4, "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(
            GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial"),
            1,
            "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(GameRegistry.findItem("Natura", "Dark Tree"), 0, "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 11, "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(GameRegistry.findItem("Natura", "Dark Tree"), 1, "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 12, "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(GameRegistry.findItem("Natura", "tree"), 2, "discover.cavestokingdoms.harvestlevel1");
        registerDiscovery(GameRegistry.findItem("Natura", "planks"), 2, "discover.cavestokingdoms.harvestlevel1");
        registerDiscovery(GameRegistry.findItem("ExtraTiC", "chunk"), 165, "discover.cavestokingdoms.harvestlevel3");

        FMLCommonHandler.instance()
            .bus()
            .register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerAllMetalsWith(String type) {
        registerDiscovery(type + "Adamantine", "discover.cavestokingdoms.adamantine");
        registerDiscovery(type + "Alumite", "discover.cavestokingdoms.alumite");
        registerDiscovery(type + "Amordrine", "discover.cavestokingdoms.amordrine");
        registerDiscovery(type + "Angmallen", "discover.cavestokingdoms.angmallen");
        registerDiscovery(type + "Ardite", "discover.cavestokingdoms.ardite");
        registerDiscovery(type + "AstralSilver", "discover.cavestokingdoms.astralsilver");
        registerDiscovery(type + "Atlarus", "discover.cavestokingdoms.atlarus");
        registerDiscovery(type + "BlackSteel", "discover.cavestokingdoms.blacksteel");
        registerDiscovery(type + "Brass", "discover.cavestokingdoms.brass");
        registerDiscovery(type + "Bronze", "discover.cavestokingdoms.bronze");
        registerDiscovery(type + "Carmot", "discover.cavestokingdoms.carmot");
        registerDiscovery(type + "Celenegil", "discover.cavestokingdoms.celenegil");
        registerDiscovery(type + "Ceruclase", "discover.cavestokingdoms.ceruclase");
        registerDiscovery(type + "Cobalt", "discover.cavestokingdoms.cobalt");
        registerDiscovery(type + "Copper", "discover.cavestokingdoms.copper");
        registerDiscovery(type + "DamascusSteel", "discover.cavestokingdoms.damascussteel");
        registerDiscovery(type + "DeepIron", "discover.cavestokingdoms.deepiron");
        registerDiscovery(type + "Desichalkos", "discover.cavestokingdoms.desichalkos");
        registerDiscovery(type + "Electrum", "discover.cavestokingdoms.electrum");
        registerDiscovery(type + "ElvenElementium", "discover.cavestokingdoms.elementium");
        registerDiscovery(type + "Enderium", "discover.cavestokingdoms.enderium");
        registerDiscovery(type + "Eximite", "discover.cavestokingdoms.eximite");
        registerDiscovery(type + "Fairy", "discover.cavestokingdoms.fairy");
        registerDiscovery(type + "ElectrumFlux", "discover.cavestokingdoms.fluxinfused");
        registerDiscovery(type + "Gold", "discover.cavestokingdoms.gold");
        registerDiscovery(type + "Haderoth", "discover.cavestokingdoms.haderoth");
        registerDiscovery(type + "Hepatizon", "discover.cavestokingdoms.hepatizon");
        registerDiscovery(type + "Ignatius", "discover.cavestokingdoms.ignatius");
        registerDiscovery(type + "Inolashite", "discover.cavestokingdoms.inolashite");
        registerDiscovery(type + "Invar", "discover.cavestokingdoms.invar");
        registerDiscovery(type + "Iron", "discover.cavestokingdoms.iron");
        registerDiscovery(type + "Kalendrite", "discover.cavestokingdoms.kalendrite");
        registerDiscovery(type + "Lead", "discover.cavestokingdoms.lead");
        registerDiscovery(type + "Lumium", "discover.cavestokingdoms.lumium");
        registerDiscovery(type + "Manasteel", "discover.cavestokingdoms.manasteel");
        registerDiscovery(type + "Manyullyn", "discover.cavestokingdoms.manyullyn");
        registerDiscovery(type + "Midasium", "discover.cavestokingdoms.midasium");
        registerDiscovery(type + "Mithril", "discover.cavestokingdoms.mithril");
        registerDiscovery(type + "Nickel", "discover.cavestokingdoms.nickel");
        registerDiscovery(type + "Orichalcum", "discover.cavestokingdoms.orichalcum");
        registerDiscovery(type + "Oureclase", "discover.cavestokingdoms.oureclase");
        registerDiscovery(type + "PigIron", "discover.cavestokingdoms.pigiron");
        registerDiscovery(type + "Platinum", "discover.cavestokingdoms.platinum");
        registerDiscovery(type + "Pokefennium", "discover.cavestokingdoms.pokefennium");
        registerDiscovery(type + "Prometheum", "discover.cavestokingdoms.prometheum");
        registerDiscovery(type + "Sanguinite", "discover.cavestokingdoms.sanguinite");
        registerDiscovery(type + "ShadowIron", "discover.cavestokingdoms.shadowiron");
        registerDiscovery(type + "ShadowSteel", "discover.cavestokingdoms.shadowsteel");
        registerDiscovery(type + "Signalum", "discover.cavestokingdoms.signalum");
        registerDiscovery(type + "Silver", "discover.cavestokingdoms.silver");
        registerDiscovery(type + "Steel", "discover.cavestokingdoms.steel");
        registerDiscovery(type + "Tartarite", "discover.cavestokingdoms.tartarite");
        registerDiscovery(type + "Terrasteel", "discover.cavestokingdoms.terrasteel");
        registerDiscovery(type + "Thaumium", "discover.cavestokingdoms.thaumium");
        registerDiscovery(type + "Tin", "discover.cavestokingdoms.tin");
        registerDiscovery(type + "Void", "discover.cavestokingdoms.voidmetal");
        registerDiscovery(type + "Vulcanite", "discover.cavestokingdoms.vulcanite");
        registerDiscovery(type + "Vyroxeres", "discover.cavestokingdoms.vyroxeres");
        registerDiscovery(type + "Unstable", "discover.cavestokingdoms.unstable");

        // Also handle the mining level stuff
        registerDiscovery(type + "Adamantine", "discover.cavestokingdoms.harvestlevel8");
        registerDiscovery(type + "Alumite", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Amordrine", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Angmallen", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Ardite", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "AstralSilver", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Atlarus", "discover.cavestokingdoms.harvestlevel8");
        registerDiscovery(type + "BlackSteel", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Brass", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(type + "Bronze", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Carmot", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Celenegil", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Ceruclase", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Cobalt", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Copper", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(type + "DamascusSteel", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "DeepIron", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(type + "Desichalkos", "discover.cavestokingdoms.harvestlevel8");
        registerDiscovery(type + "Electrum", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "ElvenElementium", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Enderium", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Eximite", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Fairy", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "ElectrumFlux", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Haderoth", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Hepatizon", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "Ignatius", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Inolashite", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Invar", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "Kalendrite", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Manasteel", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Manyullyn", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Midasium", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "Mithril", "discover.cavestokingdoms.harvestlevel6");
        registerDiscovery(type + "Nickel", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Orichalcum", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Oureclase", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "PigIron", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Platinum", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Pokefennium", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Prometheum", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(type + "Sanguinite", "discover.cavestokingdoms.harvestlevel8");
        registerDiscovery(type + "ShadowIron", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "ShadowSteel", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "Signalum", "discover.cavestokingdoms.harvestlevel2");
        registerDiscovery(type + "Silver", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Steel", "discover.cavestokingdoms.harvestlevel4");
        registerDiscovery(type + "Tartarite", "discover.cavestokingdoms.harvestlevel8");
        registerDiscovery(type + "Terrasteel", "discover.cavestokingdoms.harvestlevel5");
        registerDiscovery(type + "Thaumium", "discover.cavestokingdoms.harvestlevel3");
        registerDiscovery(type + "Void", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Vulcanite", "discover.cavestokingdoms.harvestlevel7");
        registerDiscovery(type + "Vyroxeres", "discover.cavestokingdoms.harvestlevel5");

        // Hardcoded check to avoid derpiness from Thaumcraft Quicksilver.
        if (!type.equals("nugget")) {
            registerDiscovery(type + "Quicksilver", "discover.cavestokingdoms.quicksilver");
            registerDiscovery(type + "Quicksilver", "discover.cavestokingdoms.harvestlevel6");
        }
    }

    public void registerDiscovery(String oreDictionaryEntry, String discovery) {
        discoverData.add(new OreDictionaryDiscoveryEntry(oreDictionaryEntry, discovery));
    }

    public void registerDiscovery(Block block, String discovery) {
        this.registerDiscovery(block, 0, 0, discovery);
    }

    public void registerDiscovery(Block block, int meta, String discovery) {
        this.registerDiscovery(block, meta, 0xF, discovery);
    }

    public void registerDiscovery(Block block, int meta, int metaFlags, String discovery) {
        this.registerDiscovery(Item.getItemFromBlock(block), meta, metaFlags, discovery);
    }

    public void registerDiscovery(Item item, String discovery) {
        this.registerDiscovery(item, 0, 0, discovery);
    }

    public void registerDiscovery(Item item, int meta, String discovery) {
        this.registerDiscovery(item, meta, 0xF, discovery);
    }

    public void registerDiscovery(Item item, int meta, int metaFlags, String discovery) {
        discoverData.add(new ItemDiscoveryEntry(item, meta, metaFlags, discovery));
    }

    public List<String> findDiscoveries(ItemStack stack) {
        if (stack == null) return null;

        List<String> discoveries = new ArrayList<String>();

        int size = discoverData.size();
        for (int i = 0; i < size; i++) {
            if (discoverData.get(i)
                .matches(stack))
                discoveries.add(
                    discoverData.get(i)
                        .getDiscoveredOreData());
        }

        return discoveries;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerReceivedItem(PlayerEvent.ItemPickupEvent event) {
        ItemStack item = event.pickedUp.getEntityItem();

        checkDiscovery(item, event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        ItemStack item = event.crafting;

        checkDiscovery(item, event.player);
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.wasDeath) {
            if (event.original.getEntityData()
                .hasKey("cavesToKingdomsOreDiscoveries")) {
                event.entity.getEntityData()
                    .setTag(
                        "cavesToKingdomsOreDiscoveries",
                        event.original.getEntityData()
                            .getTag("cavesToKingdomsOreDiscoveries"));
            }
        }
    }

    protected void checkDiscovery(ItemStack item, EntityPlayer player) {
        if (player.worldObj.isRemote) return;

        List<String> discoveryOres = findDiscoveries(item);

        if (discoveryOres != null && discoveryOres.size() != 0) {
            int discoveryCount = discoveryOres.size();
            for (int i = 0; i < discoveryCount; i++) {
                String discovery = discoveryOres.get(i);
                addDiscovery(player, discovery);
            }
        }
    }

    public void scanPlayerForDiscoveries(EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            checkDiscovery(stack, player);
        }
    }

    public boolean hasDiscovery(EntityPlayer player, String discoveryOre) {
        return hasDiscovery(player.getEntityData(), discoveryOre);
    }

    public void addDiscovery(EntityPlayer player, String discoveryOre) {
        if (hasDiscovery(player, discoveryOre)) return;

        addDiscovery(player.getEntityData(), discoveryOre);

        if (!player.addedToChunk) return;

        if (!player.worldObj.isRemote) player.addChatMessage(
            new ChatComponentTranslation(
                "blightfallmanual.discovery.add",
                new Object[] { StatCollector.translateToLocal(discoveryOre) }));
        if (player instanceof EntityPlayerMP)
            CavesToKingdomsNetwork.sendToPlayer(new AddDiscoveryPacket(discoveryOre), (EntityPlayerMP) player);
    }

    public void addAllDiscoveries(NBTTagCompound tag) {
        for (IDiscoveryEntry entry : discoverData) {
            addDiscovery(tag, entry.getDiscoveredOreData());
        }
    }

    public void clearDiscoveries(NBTTagCompound tag) {
        NBTTagList list = tag.getTagList("cavesToKingdomsOreDiscoveries", 8);
        if (list.tagCount() > 0) tag.removeTag("cavesToKingdomsOreDiscoveries");
    }

    public boolean hasDiscovery(NBTTagCompound tag, String discoveryOre) {
        if (tag == null) {
            return false;
        }
        NBTTagList list = tag.getTagList("cavesToKingdomsOreDiscoveries", 8);
        for (int i = 0; i < list.tagCount(); i++) {
            if (list.getStringTagAt(i)
                .equals(discoveryOre)) return true;
        }

        return false;
    }

    public void addDiscovery(NBTTagCompound tag, String discoveryOre) {
        NBTTagList list = tag.getTagList("cavesToKingdomsOreDiscoveries", 8);
        list.appendTag(new NBTTagString(discoveryOre));
        tag.setTag("cavesToKingdomsOreDiscoveries", list);
    }

    public void copyDiscoveries(NBTTagCompound from, NBTTagCompound to) {
        NBTTagList list = from.getTagList("cavesToKingdomsOreDiscoveries", 8);
        for (int i = 0; i < list.tagCount(); i++) {
            String discovery = list.getStringTagAt(i);

            if (!hasDiscovery(to, discovery)) addDiscovery(to, discovery);
        }
    }
}
