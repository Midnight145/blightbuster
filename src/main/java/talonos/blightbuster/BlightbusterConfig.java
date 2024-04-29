package talonos.blightbuster;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public class BlightbusterConfig {

    public static boolean enableWorldTainter = false;
    public static boolean enableSuperWorldTainter = false;
    public static boolean enableDawnMachine = true;
    public static boolean enablePurityFocus = true;
    public static boolean enableSilverPotion = true;
    public static boolean enableDawnTotem = true;
    public static boolean enableDawnCharger = true;
    public static boolean enableDawnOffering = false;

    public static boolean createThaumTab = false;

    public static boolean customNpcSupport = false;
    // public static String purifiedMappingsString =
    // "thaumcraft.common.entities.monster.EntityTaintSheep:net.minecraft.entity.passive.EntitySheep,"
    // + "thaumcraft.common.entities.monster.EntityTaintCow:net.minecraft.entity.passive.EntityCow,"
    // + "thaumcraft.common.entities.monster.EntityTaintChicken:net.minecraft.entity.passive.EntityChicken,"
    // + "thaumcraft.common.entities.monster.EntityTaintPig:net.minecraft.entity.passive.EntityPig,"
    // + "thaumcraft.common.entities.monster.EntityTaintVillager:net.minecraft.entity.passive.EntityVillager,"
    // + "thaumcraft.common.entities.monster.EntityTaintCreeper:net.minecraft.entity.monster.EntityCreeper";
    public static String purifiedMappingsString = "Thaumcraft.TaintedSheep:Sheep," + "Thaumcraft.TaintedCow:Cow,"
        + "Thaumcraft.TaintedChicken:Chicken,"
        + "Thaumcraft.TaintedPig:Pig,"
        + "Thaumcraft.TaintedVillager:Villager,"
        + "Thaumcraft.TaintedCreeper:Creeper";
    public static HashMap<Class<?>, Constructor<?>> purifiedMappings = new HashMap<>();
    public static String customNpcMappingsString = "TaintedOcelot:Ozelot," + "TaintedWolf:Wolf,"
        + "TaintedTownsfolk:Villager";
    public static HashMap<String, Constructor<?>> customNpcMappings = new HashMap<>();

    public static boolean enableBlood = false;
    public static boolean enableRf = false;
    public static boolean enableMana = false;

    public static BiomeGenBase defaultBiome;
    public static int maxDawnMachineRadius = 50;
    public static int minDawnMachineRadius = 4;
    public static boolean useCorners = false;
    public static int[][] dawnMachineCorners = new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
    public static boolean enableThaumicEnergistics;

    public static void load(Configuration config) {
        enableWorldTainter = config.get("General", "Enable World Tainter", enableWorldTainter)
            .getBoolean(enableWorldTainter);
        enableSuperWorldTainter = config.get("General", "Enable Super World Tainter", enableSuperWorldTainter)
            .getBoolean(enableSuperWorldTainter);
        enableDawnMachine = config.get("General", "Enable Dawn Machine", enableDawnMachine)
            .getBoolean(enableDawnMachine);
        enablePurityFocus = config.get("General", "Enable Purity Focus", enablePurityFocus)
            .getBoolean(enablePurityFocus);
        enableSilverPotion = config.get("General", "Enable Silver Potion", enableSilverPotion)
            .getBoolean(enableSilverPotion);
        enableDawnTotem = config.get("General", "Enable Dawn Totem", enableDawnTotem)
            .getBoolean(enableDawnTotem);
        enableDawnCharger = config.get("General", "Enable Dawn Charger", enableDawnCharger)
            .getBoolean(enableDawnCharger);
        enableDawnOffering = config.get("General", "Enable Dawn Offering", enableDawnOffering)
            .getBoolean(enableDawnOffering);

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

        String buffer = config.get("Purification", "Default Biome", "plains")
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

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static Class<? extends EntityLivingBase> getEntityConstructorByStringId(String entityId) {
        return (Class<? extends EntityLivingBase>) EntityList.stringToClassMapping.get(entityId);
    }

    public static void init_mappings() {
        if (customNpcSupport) {
            for (String s : customNpcMappingsString.split(",")) {
                String[] split = s.split(":");
                if (split.length == 2) {
                    Class<? extends EntityLivingBase> clazz = getEntityConstructorByStringId(split[1]);
                    if (clazz != null) {
                        try {
                            customNpcMappings.put(split[0], clazz.getConstructor(World.class));
                            continue;
                        } catch (NoSuchMethodException e) {
                            BlightBuster.logger.error(e);

                        }
                    }
                    BlightBuster.logger.error("Error finding entity: {}", split[1]);
                }
            }
        }
        for (String s : purifiedMappingsString.split(",")) {
            String[] split = s.split(":");
            if (split.length == 2) {
                try {
                    Class<?> tainted = getEntityConstructorByStringId(split[0]);
                    Class<?> purified = getEntityConstructorByStringId(split[1]);
                    purifiedMappings.put(tainted, purified.getConstructor(World.class));
                } catch (NoSuchMethodException e) {
                    BlightBuster.logger.error(e);
                    BlightBuster.logger.error("Error parsing class: {} or {}", split[0], split[1]);
                }
            }
        }
    }
}
