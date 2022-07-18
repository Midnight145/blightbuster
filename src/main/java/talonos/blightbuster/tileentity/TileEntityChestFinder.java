package talonos.blightbuster.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import talonos.blightbuster.BlightBuster;
import tconstruct.items.tools.Broadsword;
import tconstruct.weaponry.items.Boneana;

public class TileEntityChestFinder extends TileEntity {
	Ticket chunkTicket;
	boolean isInvalid = false;
	boolean hasInitializedChunkloading = false;
	int chunkX = 0, chunkZ = 0;
	private boolean waiting = false;

	public TileEntityChestFinder() {

	}

	@Override
	public void updateEntity() {
		if (this.isInvalid) { return; }
		if (!this.hasInitializedChunkloading) {
			this.initalizeChunkloading();
			return;
		}
		if (!this.isChunkLoaded(this.chunkX, this.chunkZ)) { this.loadChunk(); }
		if (!this.worldObj.getChunkProvider().chunkExists(this.chunkX, this.chunkZ)) {
			this.waiting = true;
			return;
		}
		this.waiting = false;
		Chunk chunk = this.worldObj.getChunkFromChunkCoords(this.chunkX, this.chunkZ);
		for (List list : chunk.entityLists) {
			for (Object entityObj : list) {
				Entity entity = (Entity) entityObj;
				if (entity instanceof EntityMinecartContainer) {
					System.out.println(
							"Minecart Chest found! Coords: " + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ);
					EntityMinecartContainer minecart = (EntityMinecartContainer) entity;
					for (int i = 0; i < minecart.getSizeInventory(); i++) {
						ItemStack stack = minecart.getStackInSlot(i);
						if (stack == null) { continue; }
						if (stack.getItem() instanceof Broadsword) {

							System.out.println(
									"Broadsword Found! Coords: " + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ);
							minecart.setInventorySlotContents(i, null);

							System.out.println("Broadsword deleted.");
						}
					}
				}
			}
		}
		for (int x = 0; x < 16; x++) {
			for (int y = 3; y < 255; y++) {
				for (int z = 0; z < 16; z++) {
					int blockX = x + this.chunkX * 16;
					int blockZ = z + this.chunkZ * 16;
					TileEntity tileEntity = this.getWorldObj().getTileEntity(blockX, y, blockZ);
					if (tileEntity == null) {

						continue;

					}
					if (!(tileEntity instanceof TileEntityChest)) { continue; }
					System.out.println("Chest found! Coords: " + blockX + ", " + y + ", " + blockZ);
					TileEntityChest chest = (TileEntityChest) tileEntity;
					for (int i = 0; i < chest.getSizeInventory(); i++) {
						ItemStack stack = chest.getStackInSlot(i);
						if (stack == null) { continue; }
						if (stack.getItem() instanceof Boneana) {

							System.out.println("Broadsword Found! Coords: " + blockX + ", " + y + ", " + blockZ);
							chest.setInventorySlotContents(i, null);

							System.out.println("Broadsword deleted.");
						}
					}
				}
			}
		}
		if (!this.waiting) {
			this.unloadChunk();
			this.genNextCoords();
		}
	}

	public void genNextCoords() {
		this.chunkX++;
		if (this.chunkX > 111) {
			this.chunkX = 0;
			this.chunkZ++;
		}
		if (this.chunkZ > 136) {
			System.out.println("Done!");
			this.invalidate();
		}
	}

	private void initalizeChunkloading() {
		this.chunkTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, this.getWorldObj(), ForgeChunkManager.Type.NORMAL);
		this.chunkTicket.getModData().setString("id", "ChestFinder");
		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(0, 0));
		this.hasInitializedChunkloading = true;
	}

	private void loadChunk() {
		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkX, this.chunkZ));
	}

	private void unloadChunk() {
		ForgeChunkManager.unforceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkX, this.chunkZ));
	}

	private boolean isChunkLoaded(int x, int z) {
		for (ChunkCoordIntPair coords : this.chunkTicket.getChunkList()) {
			if (coords.chunkXPos == x && coords.chunkZPos == z) { return true; }
		}
		return false;
	}

	public void forceChunkLoading(Ticket ticket) {
		if (this.chunkTicket == null) { this.chunkTicket = ticket; }
	}

	@Override
	public void invalidate() {
		ForgeChunkManager.releaseTicket(this.chunkTicket);
		this.isInvalid = true;
		super.invalidate();
	}
}
