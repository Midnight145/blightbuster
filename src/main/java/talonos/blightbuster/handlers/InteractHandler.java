package talonos.blightbuster.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import tconstruct.smeltery.blocks.LavaTankBlock;
import tmechworks.blocks.FilterBlock;

public class InteractHandler {
	public InteractHandler() { MinecraftForge.EVENT_BUS.register(this); }
	
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) { return; }
		Block block = event.world.getBlock(event.x, event.y, event.z);
		if (block instanceof FilterBlock) {
			event.setCanceled(true);
		}
		else if (block instanceof LavaTankBlock) {
			// -61, 9, -66
			int chunkX = event.x >> 4;
			int chunkY = event.y >> 4;
			int chunkZ = event.z >> 4;
			if (chunkX == -61 && chunkY == 9 && chunkZ == -66) {
				event.setCanceled(true);
			}
		}
	}
}
