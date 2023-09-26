package talonos.blightbuster.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import thaumcraft.common.config.Config;

public class PurityFocusEventHandler {
	// called when the player right-clicks on an entity
	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.entity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.entity;
			if ((entity.isPotionActive(Config.potionTaintPoisonID))
					&& (entity instanceof EntityCow || entity instanceof EntitySheep || entity instanceof EntityChicken
							|| entity instanceof EntityCreeper || entity instanceof EntityVillager
							|| entity instanceof EntityPig)) {
				entity.capturedDrops.clear();
			}
		}
	}
}
