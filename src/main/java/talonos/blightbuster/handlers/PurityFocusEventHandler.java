package talonos.blightbuster.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;

import static talonos.blightbuster.items.ItemPurityFocus.curative;
import static talonos.blightbuster.items.ItemPurityFocus.getHealVisCost;

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
        if (!BlightbusterConfig.enableCurativeUpgrade) {
            return;
        }
        boolean taintPoison = player.isPotionActive(Config.potionTaintPoisonID);

        // If it's just flux flu, only do the effect once every other second to prevent rapid vis drain.
        if (!(taintPoison || player.ticksExisted % 40 == 0)) {
            return;
        }

        boolean fluxFlu = player.isPotionActive(Config.potionVisExhaustID);
        int amplifier = -1;
        int duration = 0;
        if (fluxFlu) {
            amplifier = player.getActivePotionEffect(PotionVisExhaust.instance).getAmplifier();
            duration = player.getActivePotionEffect(PotionVisExhaust.instance).getDuration();
        } else if (!taintPoison) {
            return;
        }

        for (ItemStack is : player.inventory.mainInventory) {
            if (is != null && is.getItem() instanceof ItemWandCasting wand && wand.consumeAllVis(is, player, getHealVisCost(), false, false)) {
                ItemFocusBasic focus = wand.getFocus(is);
                if (focus != null && focus.isUpgradedWith(wand.getFocusItem(is), curative)) {
                    if (taintPoison && !wand.consumeAllVis(is, player, getHealVisCost(), true, false)) {
                        return;
                    }
                    player.removePotionEffect(Config.potionTaintPoisonID);

                    // Remove as many levels of flux flu as possible with each level costing the heal cost
                    // Remember that flux flu will only be cleared once every 40 ticks.
                    if (fluxFlu) {
                        int c = -1;
                        while (c < amplifier) {
                            if (!wand.consumeAllVis(is, player, getHealVisCost(), true, false)) {
                                break;
                            }
                            c++;
                        }
                        player.removePotionEffect(Config.potionVisExhaustID);
                        if (c < amplifier) {
                            player.addPotionEffect(new PotionEffect(Config.potionVisExhaustID, duration, amplifier - c));
                        }
                    }
                    return;
                }
            }
        }
    }
}
