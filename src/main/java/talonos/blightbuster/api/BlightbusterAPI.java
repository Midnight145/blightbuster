package talonos.blightbuster.api;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import talonos.blightbuster.BlightBuster;

public class BlightbusterAPI {

    public static String purifiedMappingsString = "Thaumcraft.TaintedSheep:Sheep," + "Thaumcraft.TaintedCow:Cow,"
        + "Thaumcraft.TaintedChicken:Chicken,"
        + "Thaumcraft.TaintedPig:Pig,"
        + "Thaumcraft.TaintedVillager:Villager,"
        + "Thaumcraft.TaintedCreeper:Creeper";
    private static HashMap<Class<?>, Constructor<?>> purifiedMappings = new HashMap<>();
    public static String customNpcMappingsString = "TaintedOcelot:Ozelot," + "TaintedWolf:Wolf,"
        + "TaintedTownsfolk:Villager";
    private static HashMap<String, Constructor<?>> customNpcMappings = new HashMap<>();

    public static boolean registerEntityPurificationMapping(Class<?> taintedClass, Class<?> purifiedConstructor) {
        return registerMapping(purifiedMappings, taintedClass, purifiedConstructor);
    }

    public static boolean registerCustomNpcPurificationMapping(String linkedName, Class<?> purifiedConstructor) {
        return registerMapping(customNpcMappings, linkedName, purifiedConstructor);
    }

    public static boolean registerEntityPurificationMapping(String taintedEntity, String entityName) {
        Class<? extends EntityLivingBase> tainted = getEntityConstructorByStringId(taintedEntity);
        Class<? extends EntityLivingBase> purified = getEntityConstructorByStringId(entityName);
        if (tainted == null || purified == null) {
            return false;
        }

        return BlightbusterAPI.registerEntityPurificationMapping(tainted, purified);
    }

    public static boolean registerCustomNpcPurificationMapping(String linkedName, String entityName) {

        Class<? extends EntityLivingBase> clazz = getEntityConstructorByStringId(entityName);
        if (clazz != null) {
            return BlightbusterAPI.registerCustomNpcPurificationMapping(linkedName, clazz);

        }
        return false;
    }

    public static Constructor<?> getPurifiedEntityConstructor(Class<?> taintedClass) {
        return purifiedMappings.get(taintedClass);
    }

    public static Constructor<?> getCustomNpcPurifiedEntityConstructor(String linkedName) {
        return customNpcMappings.get(linkedName);
    }

    public static boolean isMapped(Class<?> clazz) {
        try {
            return purifiedMappings.containsValue(clazz.getConstructor(World.class));
        } catch (NoSuchMethodException e) {
            BlightBuster.logger.error("Error checking mapped class {}", clazz);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends EntityLivingBase> getEntityConstructorByStringId(String entityId) {
        Class<? extends EntityLivingBase> clazz;
        try {
            clazz = (Class<? extends EntityLivingBase>) EntityList.stringToClassMapping.get(entityId);
        } catch (Exception e) {
            BlightBuster.logger.error("Could not find entity class for name {}", entityId);
            return null;
        }
        return clazz;
    }

    private static <T> boolean registerMapping(HashMap<T, Constructor<?>> mapping, T key, Class<?> purified) {
        if (mapping.containsKey(key)) {
            return false;
        }
        try {
            mapping.put(key, purified.getConstructor(World.class));
        } catch (NoSuchMethodException e) {
            BlightBuster.logger.error(e);
            return false;
        }
        return true;
    }
}
