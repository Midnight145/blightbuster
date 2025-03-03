package talonos.blightbuster.handlers;

import static talonos.blightbuster.items.ItemPurityFocus.curative;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;

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

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!player.isPotionActive(Config.potionTaintPoisonID)) {
            return;
        }
        for (ItemStack is : player.inventory.mainInventory) {
            if (is != null && is.getItem() instanceof ItemWandCasting wand) {
                ItemFocusBasic focus = wand.getFocus(is);
                if (focus != null && focus.isUpgradedWith(wand.getFocusItem(is), curative)) {
                    player.removePotionEffect(Config.potionTaintPoisonID);
                }
            }
        }
    }
}
