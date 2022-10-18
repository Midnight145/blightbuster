package talonos.blightbuster.handlers;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class MooshroomSpawnEventHandler {

	public MooshroomSpawnEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void checkSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if (!(event.entity instanceof EntityMooshroom)) { return; }
		EntityAnimal ent = (EntityAnimal) event.entity;
		World world = event.world;
		int x = (int) event.x, y = (int) event.y, z = (int) event.z;

		if (world.getBlock(x, y - 1, z) == Blocks.mycelium && world.getFullBlockLightValue(x, y, z) > 8
				&& ent.getBlockPathWeight(x, y, z) >= 0.0F && (world.checkNoEntityCollision(ent.boundingBox)
						&& world.getCollidingBoundingBoxes(ent, ent.boundingBox).isEmpty() && !world.isAnyLiquid(ent.boundingBox))) {
			event.setResult(Result.ALLOW);
			return;
		}
		event.setResult(Result.DENY);
	}
}
