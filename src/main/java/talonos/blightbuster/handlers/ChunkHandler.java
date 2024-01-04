package talonos.blightbuster.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jds.bibliocraft.blocks.BlockFancySign;
import jds.bibliocraft.tileentities.TileEntityFancySign;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

public class ChunkHandler {
	public ChunkHandler() { MinecraftForge.EVENT_BUS.register(this); }
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		Chunk chunk = event.getChunk();
		ChunkCoordIntPair coords = chunk.getChunkCoordIntPair();
		if (coords.chunkXPos == -62 && coords.chunkZPos == -66) {
			Block block = event.world.getBlock(-977, 149, -1046);
			if (block instanceof BlockFancySign) {
				BlockFancySign sign = (BlockFancySign) block;
				
				TileEntityFancySign tile = (TileEntityFancySign) event.world.getTileEntity(-977, 149, -1046);
				tile.textScale[1] = 2;

				tile.text[1] = "Looting the Jaded, or";
				tile.text[2] = " breaking or placing";
				
				tile.markDirty();
			}
		}
	}
}
