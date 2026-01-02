package talonos.blightbuster;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

import talonos.blightbuster.api.BlightbusterAPI;
import talonos.blightbuster.compat.CompatFixes;
import talonos.blightbuster.items.ItemPurityFocus;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class BlightbusterConfig {

    // todo: add comments to config entries
    public static boolean enableWorldTainter = false;
    public static boolean enableSuperWorldTainter = false;
    public static boolean enableDawnMachine = true;
    public static boolean enablePurityFocus = true;
    public static boolean enableIsolationFocus = true;
    public static boolean enableSilverPotion = true;
    public static boolean enableDawnTotem = true;
    public static boolean enableDawnCharger = false;
    public static boolean enableDawnOffering = false;

    public static boolean registerResearch = true; // should only be false if using something like minetweaker to manage
                                                   // research
    public static boolean createThaumTab = false;

    public static boolean customNpcSupport = false;

    public static String purifiedMappingsString = "";
    public static String customNpcMappingsString = "TaintedOcelot:Ozelot," + "TaintedWolf:Wolf,"
        + "TaintedTownsfolk:Villager";

    public static boolean enableBlood = false;
    public static boolean enableRf = false;
    public static boolean enableMana = false;

    public static BiomeGenBase defaultBiome;
    public static int maxDawnMachineRadius = 50;
    public static int minDawnMachineRadius = 4;
    public static boolean useCorners = false;
    public static int[][] dawnMachineCorners = new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
    public static boolean enableThaumicEnergistics;

    public static boolean enableFluxVacuumUpgrade;
    public static boolean enableNodePurifierUpgrade;
    public static boolean enableCurativeUpgrade;
    public static boolean enableBlightBusterUpgrade;

    public static int attackStrength;
    public static int healStrength;

    public static void load(Configuration config) {
        enableWorldTainter = config.get("General", "Enable World Tainter", enableWorldTainter)
            .getBoolean(enableWorldTainter);
        enableSuperWorldTainter = config.get("General", "Enable Super World Tainter", enableSuperWorldTainter)
            .getBoolean(enableSuperWorldTainter);
        enableDawnMachine = config.get("General", "Enable Dawn Machine", enableDawnMachine)
            .getBoolean(enableDawnMachine);
        enablePurityFocus = config.get("General", "Enable Purity Focus", enablePurityFocus)
            .getBoolean(enablePurityFocus);
        enableIsolationFocus = config.get("General", "Enable Node Isolation Focus", enableIsolationFocus)
            .getBoolean(enableIsolationFocus);
        enableSilverPotion = config.get("General", "Enable Silver Potion", enableSilverPotion)
            .getBoolean(enableSilverPotion);
        enableDawnTotem = config.get("General", "Enable Dawn Totem", enableDawnTotem)
            .getBoolean(enableDawnTotem);
        enableDawnCharger = config.get("General", "Enable Dawn Charger", enableDawnCharger)
            .getBoolean(enableDawnCharger);
        enableDawnOffering = config.get("General", "Enable Dawn Offering", enableDawnOffering)
            .getBoolean(enableDawnOffering);

        registerResearch = config.get("General", "Register Research", registerResearch)
            .getBoolean(registerResearch);
        createThaumTab = config.get("General", "Create Thaumcraft Tab", createThaumTab)
            .getBoolean(createThaumTab);

        customNpcSupport = config.get("General", "Enable Custom NPC Support", customNpcSupport)
            .getBoolean(customNpcSupport);

        purifiedMappingsString = config.get("General", "purifiedMappings", purifiedMappingsString)
            .getString();

        customNpcMappingsString = config.get("General", "customNpcMappings", customNpcMappingsString)
            .getString();

        enableBlood = config.get("General", "Enable Blood Magic Integration", enableBlood)
            .getBoolean(enableBlood);
        enableRf = config.get("General", "Enable RF Integration", enableRf)
            .getBoolean(enableRf);
        enableMana = config.get("General", "Enable Botania Integration", enableMana)
            .getBoolean(enableMana);
        enableThaumicEnergistics = config
            .get("General", "Enable Thaumic Energistics Integration", enableThaumicEnergistics)
            .getBoolean(enableThaumicEnergistics);

        String buffer = config.get("Purification", "Default Biome", "Plains")
            .getString();
        for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if (biome != null && biome.biomeName.equals(buffer)) {
                defaultBiome = biome;
                break;
            }
        }
        maxDawnMachineRadius = config
            .get("Dawn Machine", "Dawn Machine radius when provided with Aer", maxDawnMachineRadius)
            .getInt(maxDawnMachineRadius);
        minDawnMachineRadius = config
            .get("Dawn Machine", "Dawn Machine radius when not provided with Aer", minDawnMachineRadius)
            .getInt(minDawnMachineRadius);

        useCorners = config.get("Dawn Machine", "Use Dawn Machine Corners", useCorners)
            .getBoolean(useCorners);

        buffer = config.get("Dawn Machine", "Dawn Machine Corners", "0,0:112,135")
            .getString();
        try {
            String[] split = buffer.split(":");
            dawnMachineCorners[0] = new int[] { Integer.parseInt(split[0].split(",")[0]),
                Integer.parseInt(split[0].split(",")[1]) };
            dawnMachineCorners[1] = new int[] { Integer.parseInt(split[1].split(",")[0]),
                Integer.parseInt(split[1].split(",")[1]) };
            dawnMachineCorners[2] = new int[] { Integer.parseInt(split[0].split(",")[0]),
                Integer.parseInt(split[1].split(",")[1]) };
            dawnMachineCorners[3] = new int[] { Integer.parseInt(split[1].split(",")[0]),
                Integer.parseInt(split[0].split(",")[1]) };
        } catch (Exception e) {
            BlightBuster.logger.error("Error parsing Dawn Machine Corners: {}", e);
        }

        if (enablePurityFocus) {
            enableBlightBusterUpgrade = config.get(
                "Focus Upgrades",
                "Blight Buster",
                true,
                "Whether the Blight Buster focus upgrade for the purity focus should be obtainable. This upgrade lets the focus do damage to tainted mobs in its AoE.")
                .getBoolean(true);
            enableCurativeUpgrade = config.get(
                "Focus Upgrades",
                "Curative",
                true,
                "Set whether the Curative focus upgrade for the purity focus should be obtainable. This upgrade lets the focus heal and cure all friendly mobs in its AoE in one action. It also makes its holder immune to taint poison.")
                .getBoolean(true);
            enableFluxVacuumUpgrade = config.get(
                "Focus Upgrades",
                "Flux Vacuum",
                true,
                "Set whether the Flux Vacuum focus upgrade for the purity focus should be obtainable. This upgrade sucks up flux goo/gas and thaumic slimes.")
                .getBoolean(true);
            enableNodePurifierUpgrade = config.get(
                "Focus Upgrades",
                "Node Purifier",
                true,
                "Set whether the Node Purifier focus upgrade for the purity focus should be obtainable. This upgrade turns tainted nodes into pure nodes which are immune to reinfection and works on all nodes in its AoE.")
                .getBoolean(true);

            attackStrength = config.get(
                "Purity Focus",
                "Blight Buster Attack Strength",
                20,
                "How many half-hearts of damage the Blight Buster focus upgrade will deal to tainted mobs (20 = 10 hearts of damage).")
                .getInt(20);
            String[] attackCost = config.get(
                "Purity Focus",
                "Blight Buster Attack Cost",
                new String[] { "ignis:500", "perditio:250" },
                "The cost to attack tainted mobs with the Blight Buster focus upgrade. List one vis cost per line in the format aspect:cost. Vis costs are divided by 100 (500 = 5 vis per cast).")
                .getStringList();
            String[] blockCost = config.get(
                "Purity Focus",
                "Block/Biome Cleaning Cost",
                new String[] { "ordo:15", "terra:10" },
                "The cost to clean blocks/biomes. List one vis cost per line in the format aspect:cost. Vis costs are divided by 100 (15 = .15 vis per cast).")
                .getStringList();
            healStrength = config
                .get(
                    "Purity Focus",
                    "Healing Strength",
                    4,
                    "How many half-hearts of healing the Curative focus will apply to mobs (4 = 2 hearts).")
                .getInt(4);
            String[] healCost = config.get(
                "Purity Focus",
                "Healing Cost",
                new String[] { "ordo:100", "terra:200", "aqua:200" },
                "The cost to purify mobs or heal them with the Curative upgrade. List one vis cost per line in the format aspect:cost. Vis costs are divided by 100 (200 = 2 vis per cast).")
                .getStringList();
            String[] nodeCost = config.get(
                "Purity Focus",
                "Node Purifying Cost",
                new String[] { "ordo:15000", "terra:10000" },
                "The cost to purify nodes regardless of the presence of the Node Purifier upgrade. List one vis cost per line in the format aspect:cost. Vis costs are divided by 100 (15000 = 150 vis per cast).")
                .getStringList();
            String[] vacuumCost = config.get(
                "Purity Focus",
                "Vacuum Cost",
                new String[] { "aer:25", "perditio:25" },
                "The cost to use the Flux Vacuum focus upgrade. List one vis cost per line in the format aspect:cost. Vis costs are divided by 100 (25 = .25 vis per cast).")
                .getStringList();

            ItemPurityFocus.setBlockVisCost(parseVisCost(blockCost));
            ItemPurityFocus.setNodeVisCost(parseVisCost(nodeCost));
            ItemPurityFocus.setHealVisCost(parseVisCost(healCost));
            if (enableBlightBusterUpgrade) {
                ItemPurityFocus.setAttackVisCost(parseVisCost(attackCost));
            }
            if (enableFluxVacuumUpgrade) {
                ItemPurityFocus.setVacuumVisCost(parseVisCost(vacuumCost));
            }
        }

        if (config.hasChanged()) {
            config.save();
        }

        CompatFixes.fixEnderIO(); // Workaround EnderIO crash when RF is disabled
    }

    private static AspectList parseVisCost(String[] config) {
        AspectList cost = new AspectList();
        for (String s : config) {
            String[] pair = s.split(":");
            if (pair.length < 2) {
                continue;
            }
            Aspect a = null;
            for (Aspect primal : Aspect.getPrimalAspects()) {
                if (primal.getTag()
                    .equals(pair[0].toLowerCase())) {
                    a = primal;
                    break;
                }
            }
            if (a == null) {
                BlightBuster.logger.warn("Unable to parse primal aspect for entry \"%s\".", s);
                continue;
            }
            int size;
            try {
                size = Integer.parseInt(pair[1]);
            } catch (NumberFormatException e) {
                BlightBuster.logger.warn("Unable to parse aspect size for entry \"%s\".", s);
                continue;
            }
            cost.add(a, size);
        }
        return cost;
    }

    public static void initConfigMaps() {
        if (customNpcSupport) {
            for (String s : customNpcMappingsString.split(",")) {
                String[] split = s.split(":");
                if (split.length == 2) {
                    if (!BlightbusterAPI.registerCustomNpcPurificationMapping(split[0], split[1])) {
                        BlightBuster.logger
                            .error("Error registering CustomNPC purification mapping: {} to {}", split[0], split[1]);
                    }
                }
            }
        }
        for (String s : purifiedMappingsString.split(",")) {
            String[] split = s.split(":");
            if (split.length == 2) {
                if (!BlightbusterAPI.registerEntityPurificationMapping(split[0], split[1])) {
                    BlightBuster.logger.error("Error registering purification mapping: {} to {}", split[0], split[1]);
                }
            }
        }
    }
}
