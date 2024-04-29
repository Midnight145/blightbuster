package talonos.blightbuster.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import thaumcraft.common.config.Config;

public class PurityFocusEventHandler {

    // called when the player right-clicks on an entity
    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        if (event.entity instanceof EntityLivingBase entity) {
            if (entity.isPotionActive(Config.potionTaintPoisonID)) {
                try {
                    if (BlightbusterConfig.purifiedMappings.containsValue(
                        entity.getClass()
                            .getConstructor(World.class))) {
                        entity.capturedDrops.clear();
                    }
                } catch (NoSuchMethodException e) {
                    BlightBuster.logger.error("Error capturing drops from class {}", entity.getClass());
                }
            }
        }
    }
}
