package talonos.blightbuster.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.network.packets.SpawnCleanseParticlesPacket;
import thaumcraft.common.config.Config;

public class DawnTotemEntity extends TileEntity {
	public long queuedTicks = 0;
	public long offset = -1;
	
	public boolean canUpdate() { return true; }
	
	public void updateEntity() {
		super.updateEntity();
		
		if (worldObj.isRemote)
			return;
		
		if (this.offset < 0)
			this.offset = worldObj.rand.nextInt(20);
		
		queuedTicks++;
		
		int cleansedSquares = 0;
		while (cleansedSquares < 5 && queuedTicks > 0) {
			queuedTicks--;
			offset = (offset + 1) % 20;
			if (offset == 0) {
				cleansedSquares++;
				cleanseSquare(queuedTicks == 0);
			}
		}
	}
	
	private long onValidateLastTick = -1;
	
	@Override
	public void validate() {
		super.validate();
		
		if (this.onValidateLastTick >= 0) {
			long passedTicks = Math.max(getWorldObj().getWorldTime() - this.onValidateLastTick, 0);
			queuedTicks += passedTicks;
			this.onValidateLastTick = -1;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		long lastWorldTick = tag.getLong("LastWorldTick");
		queuedTicks = tag.getLong("QueuedTicks");
		
		if (getWorldObj() != null) {
			long passedTicks = Math.max(getWorldObj().getWorldTime() - lastWorldTick, 0);
			queuedTicks += passedTicks;
		}
		else {
			this.onValidateLastTick = lastWorldTick;
		}
		offset = tag.getLong("TickOffset");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setLong("QueuedTicks", queuedTicks);
		tag.setLong("LastWorldTick", getWorldObj().getWorldTime());
		tag.setLong("TickOffset", offset);
	}
	
	protected void cleanseSquare(boolean sendParticleSpawn) {
		if (this.worldObj.isRemote)
			return;
		
		int centerX = this.worldObj.rand.nextInt(51) - 25;
		int centerZ = this.worldObj.rand.nextInt(51) - 25;
		
		if (sendParticleSpawn) {
			int absolutePointX = centerX + this.xCoord;
			int absolutePointZ = centerZ + this.zCoord;
			BlightbusterNetwork.sendToNearbyPlayers(new SpawnCleanseParticlesPacket(absolutePointX, absolutePointZ),
					worldObj.provider.dimensionId, absolutePointX, 128.0f, absolutePointZ, 150);
		}
		
		for (int x = centerX - 1; x < centerX + 2; x++) {
			for (int z = centerZ - 1; z < centerZ + 2; z++) {
				if ((this.worldObj.getBiomeGenForCoords(x + this.xCoord,
						z + this.zCoord).biomeID == Config.biomeTaintID)
						|| (this.worldObj.getBiomeGenForCoords(x + this.xCoord,
								z + this.zCoord).biomeID == Config.biomeEerieID)
						|| (this.worldObj.getBiomeGenForCoords(x + this.xCoord,
								z + this.zCoord).biomeID == Config.biomeMagicalForestID)) {
					BiomeGenBase[] biomesForGeneration = null;
					biomesForGeneration = this.worldObj.getWorldChunkManager()
							.loadBlockGeneratorData(biomesForGeneration, x + this.xCoord, z + this.zCoord, 1, 1);
					if ((biomesForGeneration != null) && (biomesForGeneration[0] != null)) {
						BiomeGenBase biome = biomesForGeneration[0];
						BlightbusterNetwork.setBiomeAt(this.worldObj, x + this.xCoord, z + this.zCoord, biome);
					}
				}
			}
		}
	}
}
