package talonos.blightbuster.tileentity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import noppes.npcs.entity.EntityCustomNpc;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.network.packets.SpawnCleanseParticlesPacket;
import talonos.blightbuster.tileentity.dawnmachine.DawnMachineResource;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;
import thaumcraft.common.entities.monster.EntityTaintVillager;
import thaumcraft.common.tiles.TileNode;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

public class DawnMachineTileEntity extends TileEntity
		implements IAspectSource, IAspectContainer, IEnergyReceiver, IEnergyStorage, ISparkAttachable, IFluidTank, IFluidHandler {

	// FLUID INTEGRATION (Blood Magic)
	private static final int MAX_BLOOD = 100000;
	private static final Fluid blood = AlchemicalWizardry.lifeEssenceFluid;
	private FluidStack fluid = new FluidStack(blood, 0);
	private FluidTankInfo tankInfo = new FluidTankInfo(this.getFluid(), MAX_BLOOD);
	private FluidTankInfo[] tankInfoArray = { this.tankInfo };
	int bloodLastTick = 0;

	// END FLUID INTEGRATION

	// BOTANIA INTEGRATION

	private static final int MAX_MANA = 1000000;
	private int currentMana = 0;

	// END BOTANIA INTEGRATION

	// RF INTEGRATION

	public static final int DEAD_RF = 150; // Used in talonos.blightbuster.client.DawnMachineControllerRenderer
	protected static final int MAX_RF = 1280000;
	protected static final int FULLGREEN_RF = 80000;
	protected static final int FULLYELLOW_RF = 40000;
	protected static final int FULLRED_RF = 20000;
	protected static final Vec3 COLOR_GREEN = Vec3.createVectorHelper(0, 0.9, 0);
	protected static final Vec3 COLOR_YELLOW = Vec3.createVectorHelper(0.9, 0.9, 0);
	protected static final Vec3 COLOR_RED = Vec3.createVectorHelper(0.9, 0, 0);
	private int currentRF = 0;

	// END RF INTEGRATION

	// THAUMCRAFT INTEGRATION

	private AspectList internalAspectList = new AspectList();

	// END THAUMCRAFT INTEGRATION

	// Current chunk being cleaned
	public int chunkX = Integer.MAX_VALUE;
	public int chunkZ = Integer.MAX_VALUE;

	// Last cleansed chunk
	public int lastChunkX = Integer.MAX_VALUE;
	public int lastChunkZ = Integer.MAX_VALUE;

	// Map size
	public final int MAP_WIDTH_CHUNKS = 111;
	public final int MAP_HEIGHT_CHUNKS = 136;

	private int dx = 0, dz = -1;

	private int ticksSinceLastCleanse = 0;

	// Offsets for all adjacent chunks, used to check if still clean
	public static final int[][] OFFSETS = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
	int[] dawnMachineChunkCoords = null;
	int[][] scanlineCoords = null;
	int[][] scanlineAerCoords = null;
	int[] dawnMachineBlockCoords = null;
	int index = 0;
	int aerIndex = 0;

	// Variables used in chunkloading
	private ForgeChunkManager.Ticket dawnMachineTicket;
	private boolean hasInitializedChunkloading = false;

	// Denotes if waiting for chunk to load
	private boolean waiting = false;

	// Used in cleanseSingleMob for nicer purifying of animals
	// Class<?> is tainted mob, constructor is the purified version
	static private HashMap<Class<?>, Constructor<?>> taint_purified_constructors = new HashMap<Class<?>, Constructor<?>>();
//	static private HashSet<Long> cleansedChunks = new HashSet<Long>();
	static private HashMap<Long, CleansedChunk> cleansedChunks = new HashMap<Long, CleansedChunk>();

	private boolean firstTick = true, doInit = true;

	static {
		final Class<?>[] taintedEntities = { EntityTaintSheep.class, EntityTaintChicken.class, EntityTaintCow.class, EntityTaintPig.class,
				EntityTaintVillager.class, EntityTaintCreeper.class };

		final Class<?>[] purifiedEntities = { EntitySheep.class, EntityChicken.class, EntityCow.class, EntityPig.class,
				EntityVillager.class, EntityCreeper.class };

		for (int i = 0; i < taintedEntities.length; i++) {
			try {
				taint_purified_constructors.put(taintedEntities[i], purifiedEntities[i].getConstructor(new Class[] { World.class }));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Used for generating coordinates and desert chance placement
	Random rand;
	private boolean didClean;

	public DawnMachineTileEntity() {
		this.rand = new Random(System.currentTimeMillis());
	}

	@Override
	public void updateEntity() {
		if (this.firstTick) {
			this.firstTick = false;
			return;
		}
		if (this.doInit) {
			this.dawnMachineBlockCoords = new int[] { this.xCoord, this.zCoord };
			this.scanlineCoords = this.generateScanlineCoords();
			this.scanlineAerCoords = this.generateScanlineAerCoords();
			this.doInit = false;
		}
		if (this.getWorldObj().isRemote) { return; }

		this.dawnMachineChunkCoords = this.getDawnMachineChunkCoords();
		if (this.dawnMachineTicket == null || !this.hasInitializedChunkloading) { this.initalizeChunkloading(); }

		// Check for redstone signal
		if ((this.getWorldObj().getIndirectPowerLevelTo(this.xCoord, this.yCoord, this.zCoord, 1) > 0)) { return; }

		for (CleansedChunk chunk : cleansedChunks.values()) {
			chunk.exists = this.isChunkLoaded(chunk.x, chunk.z);
			if (!chunk.exists && chunk.lastTick != chunk.exists) { chunk.isDirty = true; }
			chunk.lastTick = chunk.exists;
		}

		if (!(this.currentMana >= MAX_MANA)) { this.recieveMana(1000); }
		if (this.dawnMachineBlockCoords[0] != this.xCoord || this.dawnMachineBlockCoords[1] != this.zCoord) {
			this.dawnMachineChunkCoords = this.getDawnMachineChunkCoords();
			this.dawnMachineBlockCoords = new int[] { this.xCoord, this.zCoord };
		}

		// Determines cleanse speed in ticks
		int cleanseLength = this.haveEnoughFor(DawnMachineResource.MACHINA) ? 4 : 12;

		this.ticksSinceLastCleanse %= cleanseLength;

		if (this.ticksSinceLastCleanse == 0 || this.waiting) {
			Chunk chunk = null;
			if (!this.waiting) {
				this.unloadChunk();
				this.getNewCleanseCoords();
				this.loadChunk();
			}
			chunk = getChunk(this.worldObj, this.chunkX, this.chunkZ);

			if (chunk == null) {
				this.waiting = true;
				return;
			}
			else {
				this.waiting = false;
			}

			boolean anythingToDo = this.hasAnythingToCleanseHere(chunk);
			int count = 0;
			while (!anythingToDo && count <= 10) {
				if (this.haveEnoughFor(DawnMachineResource.COGNITIO)) {
					this.spend(DawnMachineResource.COGNITIO);
					if (!this.waiting) {
						this.unloadChunk();
						this.getNewCleanseCoords();
						this.loadChunk();
					}
					chunk = getChunk(this.worldObj, this.chunkX, this.chunkZ);
					if (chunk == null) {
						this.waiting = true;
						return;
					}
					this.waiting = false;
					anythingToDo = this.hasAnythingToCleanseHere(chunk);
				}
				else {
					break;
				}
				count++;
			}

			if (cleanseLength == 4) { this.spend(DawnMachineResource.MACHINA); }
			this.executeCleanse(chunk);

			for (int i = 0; i < 8; i++) {
				for (Object entity : this.getWorldObj().playerEntities) {
					EntityPlayer player = null;
					if (entity instanceof EntityPlayer) { player = (EntityPlayer) entity; }
					if (player == null) { return; }
				}
				int[] newChunkCoords = { this.chunkX + OFFSETS[i][0], this.chunkZ + OFFSETS[i][1] };
				long hash = this.getHash(newChunkCoords[0], newChunkCoords[1]);
//				if (cleansedChunks.contains(hash)) {
//					if (this.isChunkLoaded(newChunkCoords[0], newChunkCoords[1])) {
//						Chunk chunk_ = getChunk(this.worldObj, newChunkCoords[0], newChunkCoords[1]);
//						if (chunk_ != null && this.hasAnythingToCleanseHere(chunk_)) {
//							this.executeCleanse(chunk);
//						}
//					}
//				}

				CleansedChunk cchunk = cleansedChunks.get(hash);
				if (cchunk != null && cchunk.isDirty && cchunk.exists) {
					Chunk chunk_ = getChunk(this.worldObj, newChunkCoords[0], newChunkCoords[1]);
					if (chunk_ != null && this.hasAnythingToCleanseHere(chunk_)) {
						this.executeCleanse(chunk);
						cchunk.isDirty = false;
					}
				}
			}

			this.ticksSinceLastCleanse++;
		}
		else {
			this.ticksSinceLastCleanse++;
		}

	}

	// CLEANSING FUNCTIONS

	protected class CleansedChunk {
		CleansedChunk(int x, int z) {
			this.x = x;
			this.z = z;
		}

		int x, z;
		boolean isDirty = false;
		boolean lastTick = false;
		boolean exists = false;
	}

	protected void executeCleanse(Chunk chunk) {
		// TODO: THIS WILL CRASH
		// TODO: Loop needs to be like cleanseMobs
		List<Entity>[] entityLists = chunk.entityLists;

		for (List<Entity> list : entityLists) {
			for (Entity entity : list) {
				if (entity instanceof EntityTaintSporeSwarmer || entity instanceof EntityFallingTaint) {
					if (this.haveEnoughFor(DawnMachineResource.IGNIS)) {
						this.spend(DawnMachineResource.IGNIS);

						if (this.haveEnoughFor(DawnMachineResource.VACUOS)) {
							this.spend(DawnMachineResource.VACUOS);
						}
						else {
							this.getWorldObj().setBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ, ConfigBlocks.blockFluxGoo,
									((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta(), 3);
						}
						entity.setDead();
					}
				}
			}
		}

		this.cleanseBiome(chunk);
		boolean didUseIgnis = this.cleanseBlocks(chunk);
		if (this.haveEnoughFor(DawnMachineResource.SANO)) { this.cleanseMobs(chunk); }
//		cleansedChunks.add(this.getHash(this.chunkX, this.chunkZ));
		cleansedChunks.put(this.getHash(this.chunkX, this.chunkZ), new CleansedChunk(this.chunkX, this.chunkZ));

		this.sendParticlePackets(didUseIgnis);
	}

	void sendParticlePackets(boolean didUseIgnis) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				BlightbusterNetwork.sendToNearbyPlayers(
						new SpawnCleanseParticlesPacket(this.lastChunkX * 16 + x, this.lastChunkZ * 16 + z, didUseIgnis, true),
						this.worldObj.provider.dimensionId, this.lastChunkX * 16 + x, 128.0F, this.lastChunkZ * 16 + z, 150);
			}
		}
	}

	protected boolean hasAnythingToCleanseHere(Chunk chunk) {
		// Can cleanse biome?
		BiomeGenBase[] origBiome = null;
		for (int z = 0; z <= 16; z++) {
			for (int x = 0; x <= 16; x++) {
				int[] coords = this.getBlockCoordsFromChunk(chunk, x, z);
				origBiome = this.getWorldObj().getWorldChunkManager().loadBlockGeneratorData(origBiome, coords[0], coords[1], 1, 1);
				BiomeGenBase biome = this.getWorldObj().getBiomeGenForCoords(coords[0], coords[1]);
				if (biome.biomeID == Config.biomeTaintID && origBiome[0].biomeID != Config.biomeTaintID
						|| biome.biomeID == Config.biomeEerieID && origBiome[0].biomeID != Config.biomeEerieID
						|| biome.biomeID == Config.biomeMagicalForestID && origBiome[0].biomeID != Config.biomeMagicalForestID) {
					return true;
				}
			}
		}

		if (this.haveEnoughFor(DawnMachineResource.SANO)) {
			for (List<Entity> list : chunk.entityLists) {
				for (Entity entity : list) {
					if (entity instanceof ITaintedMob) { return true; }
				}
			}
		}

		for (int z = 0; z <= 16; z++) {
			for (int x = 0; x <= 16; x++) {
				int[] coords = this.getBlockCoordsFromChunk(chunk, x, z);
				int herbaTopBlock = -1;
				boolean canHerba = this.haveEnoughFor(DawnMachineResource.HERBA);
				if (canHerba) { herbaTopBlock = this.getWorldObj().getTopSolidOrLiquidBlock(coords[0], coords[1]); }

				boolean canIgnis = this.haveEnoughFor(DawnMachineResource.IGNIS);
				boolean canVacuos = this.haveEnoughFor(DawnMachineResource.VACUOS);
				boolean canAura = this.haveEnoughFor(DawnMachineResource.AURAM);
				if (canIgnis || canVacuos || canHerba || canAura) {
					// Are there any taint blocks to cleanse?
					for (int i = 0; i < 256; i++) {
						Block block = this.getWorldObj().getBlock(coords[0], i, coords[1]);
						int meta = this.getWorldObj().getBlockMetadata(coords[0], i, coords[1]);

						if (canIgnis && block == ConfigBlocks.blockTaintFibres) { return true; }
						if (canIgnis && block == ConfigBlocks.blockTaint && meta != 2) { return true; }
						if (canVacuos && block == ConfigBlocks.blockFluxGoo) { return true; }
						if (canHerba && i == herbaTopBlock && block == Blocks.dirt) { return true; }
						if (canAura && block == ConfigBlocks.blockAiry) {
							if (meta == 0) {
								TileNode node = (TileNode) this.getWorldObj().getTileEntity(coords[0], i, coords[1]);
								if (node != null) { if (node.getNodeType() == NodeType.TAINTED) { return true; } }
							}
						}
					}
				}
			}
		}

		return false;
	}

	protected void cleanseBiome(Chunk chunk) {
		BiomeGenBase[] genBiomes = null;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int[] coords = this.getBlockCoordsFromChunk(chunk, x, z);
				genBiomes = this.getWorldObj().getWorldChunkManager().loadBlockGeneratorData(genBiomes, coords[0], coords[1], 1, 1);
				BiomeGenBase biome = this.getWorldObj().getBiomeGenForCoords(coords[0], coords[1]);
				if (biome.biomeID == Config.biomeTaintID || biome.biomeID == Config.biomeEerieID
						|| biome.biomeID == Config.biomeMagicalForestID) {

					if (genBiomes != null && genBiomes.length > 0 && genBiomes[0] != null) {
						BlightbusterNetwork.setBiomeAt(this.getWorldObj(), coords[0], coords[1], genBiomes[0]);
					}
				}
			}
		}
	}

	protected boolean cleanseBlocks(Chunk chunk) {

		boolean haveUsedIgnis = false;

		// iterates over entire chunk
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				// get the map coords
				int[] coords = this.getBlockCoordsFromChunk(chunk, x, z);

				// - Ignis is used to cleanse tainted blocks
				// - Vacuos will remove crusted taint blocks when ignis acts on them, instead of
				// converting to flux goo
				// - Herba will convert the top dirt block to grass/top tainted soil block to
				// grass instead of dirt
				// - Auram will convert tained node blocks to normal node blocks
				// - Arbor will detect when we've found a stack of crusted taint 3 high and put
				// a sapling there
				boolean canArbor = this.haveEnoughFor(DawnMachineResource.ARBOR);
				boolean canHerba = this.haveEnoughFor(DawnMachineResource.HERBA);

				// Get the y-value of the block herba might want to act on
				boolean foundTopBlock = false;
				int topBlock = -1;
				// We do the cleanse from top to bottom and every time we find crusted taint, we
				// count how many consecutive
				// ones we cleanse. When we find something that isn't crusted taint, after
				// cleansing it, we plant a sapling on
				// it if there was enough crusted taint above it
				int columnCrustedTaint = 0;
				for (int y = 255; y >= 0; y--) {
					Block block = chunk.getBlock(x, y, z);
					int meta = chunk.getBlockMetadata(x, y, z);

					boolean thisIsCrustedTaint = (block == ConfigBlocks.blockTaint && meta == 0);

					if (thisIsCrustedTaint && this.haveEnoughFor(DawnMachineResource.IGNIS)
							&& this.haveEnoughFor(DawnMachineResource.VACUOS)) {
						columnCrustedTaint++;
					}

					if (!foundTopBlock && (canHerba || canArbor) && block.isOpaqueCube()) {
						foundTopBlock = true;
						topBlock = y;
					}

					boolean didUseIgnis = this.cleanseSingleBlock(coords[0], y, coords[1], block, meta,
							canHerba && foundTopBlock && y == topBlock);
					haveUsedIgnis = didUseIgnis || haveUsedIgnis;

					if (didUseIgnis && this.getWorldObj().getBlock(coords[0], y, coords[1]) != Blocks.dirt) { foundTopBlock = false; }
				}

				if (columnCrustedTaint >= 3 && foundTopBlock) {

					BiomeGenBase biome = this.getWorldObj().getBiomeGenForCoords(coords[0], coords[1]);
					String biomeName = biome.biomeName.toLowerCase(Locale.ENGLISH);

					// Default to oak
					int treeType = 0;
					if (biomeName.contains("taiga") || biomeName.contains("tundra")) {
						treeType = 1; // Spruce trees
					}
					else if (biomeName.contains("birch")) {
						treeType = 2; // Birch trees
					}
					else if (biomeName.contains("jungle")) {
						treeType = 3; // Jungle tree
					}
					else if (biomeName.contains("savanna")) {
						treeType = 4; // Acacia trees
					}
					else if (biomeName.contains("roof")) { treeType = 5; }

					this.getWorldObj().setBlock(coords[0], topBlock + 1, coords[1], Blocks.sapling, treeType, 3);
					this.spend(DawnMachineResource.ARBOR);
				}

				if (foundTopBlock && haveUsedIgnis) {
					BiomeGenBase biome = this.getWorldObj().getBiomeGenForCoords(coords[0], coords[1]);
					String biomeName = biome.biomeName.toLowerCase(Locale.ENGLISH);

					if (biomeName.contains("desert") || biomeName.contains("msdune")) {
						Block block = this.getWorldObj().getBlock(coords[0], topBlock, coords[1]);
						if (block == Blocks.sand) {
							int chance = this.rand.nextInt(500) + 1;
							boolean blockToSet = this.rand.nextBoolean();
							if (chance == 500) {
								if (blockToSet) {
									this.getWorldObj().setBlock(coords[0], topBlock + 1, coords[1], Blocks.cactus);
								}
								else {
									this.getWorldObj().setBlock(coords[0], topBlock + 1, coords[1], ConfigBlocks.blockCustomPlant, 3, 3); // cinderpearl
								}
								this.spend(DawnMachineResource.ARBOR);
							}
						}
					}
				}
			}
		}
		return haveUsedIgnis;
	}

	private boolean cleanseSingleBlock(int x, int y, int z, Block block, int meta, boolean doHerbaCheck) {
		boolean canIgnis = this.haveEnoughFor(DawnMachineResource.IGNIS);
		boolean canVacuos = this.haveEnoughFor(DawnMachineResource.VACUOS);

		// Cleanse crusted taint
		if (canIgnis && block == ConfigBlocks.blockTaint && meta == 0) {
			this.spend(DawnMachineResource.IGNIS);

			Block replaceBlock = ConfigBlocks.blockFluxGoo;
			int replaceMeta = ((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta();

			if (this.haveEnoughFor(DawnMachineResource.VACUOS)) {
				this.spend(DawnMachineResource.VACUOS);
				replaceBlock = Blocks.air;
				replaceMeta = 0;
			}

			this.getWorldObj().setBlock(x, y, z, replaceBlock, replaceMeta, 3);
			return true;
		}

		// Cleanse tainted soil
		if (canIgnis && block == ConfigBlocks.blockTaint && meta == 1) {
			this.spend(DawnMachineResource.IGNIS);

			Block replaceBlock = Blocks.dirt;
			if (doHerbaCheck) {
				this.spend(DawnMachineResource.HERBA);
				if (this.getWorldObj().getBiomeGenForCoords(x, z).biomeID == 14
						|| this.getWorldObj().getBiomeGenForCoords(x, z).biomeID == 15) {
					replaceBlock = Blocks.mycelium;
				}
				else {
					replaceBlock = Blocks.grass;
				}
			}

			this.getWorldObj().setBlock(x, y, z, replaceBlock);

			return true;
		}

		// Cleanse taint fibres
		if (canIgnis && block == ConfigBlocks.blockTaintFibres) {
			this.spend(DawnMachineResource.IGNIS);
			this.getWorldObj().setBlock(x, y, z, Blocks.air);
			return true;
		}

		if (canVacuos && block == ConfigBlocks.blockFluxGoo) {
			this.spend(DawnMachineResource.VACUOS);

			this.getWorldObj().setBlock(x, y, z, Blocks.air);

			return false;
		}

		if (doHerbaCheck && block == Blocks.dirt) {
			this.spend(DawnMachineResource.HERBA);

			Block replaceBlock = Blocks.grass;

			if (this.getWorldObj().getBiomeGenForCoords(x, z).biomeID == 14
					|| this.getWorldObj().getBiomeGenForCoords(x, z).biomeID == 15) {
				replaceBlock = Blocks.mycelium;
			}

			this.getWorldObj().setBlock(x, y, z, replaceBlock);

			return false;
		}

		if (this.haveEnoughFor(DawnMachineResource.AURAM) && GameRegistry.findUniqueIdentifierFor(block).modId.equals("Thaumcraft")) {
			TileEntity tile = this.getWorldObj().getTileEntity(x, y, z);

			if (tile != null && tile instanceof TileNode) {
				TileNode node = (TileNode) tile;
				if (node != null && node.getNodeType() == NodeType.TAINTED) {
					this.spend(DawnMachineResource.AURAM);
					node.setNodeType(NodeType.NORMAL);
					node.markDirty();
					this.getWorldObj().markBlockForUpdate(x, y, z);
				}
			}
			return false;
		}

		return false;
	}

	protected void cleanseMobs(Chunk chunk) {

		List<Entity>[] entityLists = chunk.entityLists.clone();
		for (int i = 0; i < entityLists.length; i++) {
			for (int j = 0; j < entityLists[i].size(); j++) {
				boolean spend = false;
				if (DawnMachineTileEntity.taint_purified_constructors.containsKey(chunk.entityLists[i].get(j).getClass())) {
					spend = true;
					try {
						this.cleanseSingleMob((Entity) chunk.entityLists[i].get(j), (EntityLivingBase) taint_purified_constructors
								.get(chunk.entityLists[i].get(j).getClass()).newInstance(this.getWorldObj()));
					} catch (Exception e) {
					}
				}

				Entity entity = (Entity) chunk.entityLists[i].get(j);
				if (entity instanceof EntityCustomNpc) {
					EntityCustomNpc npc = (EntityCustomNpc) entity;

					if (npc.linkedName.equals("TaintedOcelot")) {
						this.cleanseSingleMob(entity, new EntityOcelot(entity.worldObj));
						spend = true;
					}
					else if (npc.linkedName.equals("TaintedWolf")) {
						this.cleanseSingleMob(entity, new EntityWolf(entity.worldObj));
						spend = true;
					}
					else if (npc.linkedName.equals("TaintedTownsfolk")) {
						this.cleanseSingleMob(entity, new EntityVillager(entity.worldObj));
						spend = true;
					}
				}

				if (spend) { this.spend(DawnMachineResource.SANO); }
				if (!this.haveEnoughFor(DawnMachineResource.SANO)) { return; }
			}
		}
	}

	private void cleanseSingleMob(Entity tainted, EntityLivingBase cleansed) {
		// new entity copies original entity location
		cleansed.copyLocationAndAnglesFrom(tainted);
		// original entity spawns new entity into the world
		tainted.worldObj.spawnEntityInWorld(cleansed);
		// new entity removes the old entity
		cleansed.worldObj.removeEntity(tainted);
	}

	// END CLEANSING FUNCTIONS

	// COORD GENERATION

	private void getNewCleanseCoords() {
		// reset last scanned chunk
		this.lastChunkX = this.chunkX;
		this.lastChunkZ = this.chunkZ;

		boolean usedOrdo = false;
		if (this.haveEnoughFor(DawnMachineResource.ORDO)) {
		    usedOrdo = true;
		    this.getNextScanlineCoords(true);
		}
		else {
		    this.generateRandomCoords(true);
		}

		boolean haveEnoughForAer = this.haveEnoughFor(DawnMachineResource.AER, this.getAerCost(this.chunkX, this.chunkZ));

		if(!haveEnoughForAer)
		{
		    if (usedOrdo) {
			this.index--;
			this.getNextScanlineCoords(false);
		    }
		    else {
			this.generateRandomCoords(false);
		    }
		}

		if (this.haveEnoughFor(DawnMachineResource.ORDO)) {
		    this.spend(DawnMachineResource.ORDO);
		    this.getNextScanlineCoords(haveEnoughForAer);
		}
		else {
		    this.generateRandomCoords(haveEnoughForAer);
		}
		if (usedOrdo) { this.spend(DawnMachineResource.ORDO); }
		if (haveEnoughForAer) { this.spendAer(); }
	    }

	private void getNextScanlineCoords(boolean haveEnoughForAer) {
		int[] coords;
		if (haveEnoughForAer) {
			this.index++;
			if (this.index == this.scanlineAerCoords.length) { this.index = 0; } // bounds checking
			coords = this.scanlineAerCoords[this.index];
		}
		else {
			this.aerIndex++;
			if (this.aerIndex == this.scanlineCoords.length) { this.aerIndex = 0; }
			coords = this.scanlineCoords[this.aerIndex];
		}
		this.chunkX = coords[0];
		this.chunkZ = coords[1];
	}

	private int[][] generateScanlineAerCoords() {
		List<int[]> coords = new ArrayList<int[]>();
		coords.add(this.getDawnMachineChunkCoords());
		int[] startCoords = this.getDawnMachineChunkCoords();
		int startX = startCoords[0], startZ = startCoords[1];
		int currentLocX = startX, currentLocZ = startZ;
		int dx = 0, dz = -1;
		int found_corners = 0;
		int[][] corner_array = { { 0, 0 }, { 112, 135 }, { 0, 135 }, { 112, 0 } };
		List<int[]> corners = Arrays.asList(corner_array);

		while (found_corners != 4) {
			int[] current = { currentLocX, currentLocZ };

			if (currentLocX >= 0 && currentLocX <= 112 && currentLocZ >= 1 && currentLocZ <= 135) { coords.add(current); }

			boolean found = this.doesContain(corners, current);
			if (found) { found_corners++; }

			currentLocX += dx;
			currentLocZ += dz;

			if (dz == -1 ? (currentLocX - startX - 1 == currentLocZ - startZ)
					: (Math.abs(currentLocX - startX) == Math.abs(currentLocZ - startZ))) {
				int t = dz;
				dz = dx;
				dx = -t;
			}
		}
		int[][] returnval = new int[coords.size()][];
		for (int i = 0; i < coords.size(); i++) {
			returnval[i] = coords.get(i);
		}

		return returnval;
	}

	private boolean doesContain(List<int[]> corners, int[] coords) {
		for (int[] corner : corners) {
			if (Arrays.equals(corner, coords)) { return true; }
		}
		return false;
	}

	private int[][] generateScanlineCoords() {
		int[] dawnMachineChunkCoords = this.getDawnMachineChunkCoords();
		int maxWidth = dawnMachineChunkCoords[0] + 4;
		int minWidth = dawnMachineChunkCoords[0] - 4;

		int maxHeight = dawnMachineChunkCoords[1] + 4;
		int minHeight = dawnMachineChunkCoords[1] - 4;

		int x = Math.abs(maxWidth - minWidth), z = Math.abs(maxHeight - minHeight);
		int maxVal = x * z; // x and z change in the loop, causes indexoutofbounds
		int[][] coords = new int[x * z][2];
		int dx = 0, dz = -1;
		for (int i = 0; i < maxVal; ++i) {
			x += dx;
			z += dz;
			coords[i] = new int[] { x - dawnMachineChunkCoords[0], z - dawnMachineChunkCoords[1] };
			if (dz == -1 ? (z == x - 1) : (Math.abs(z) == Math.abs(x))) {
				int ndx = -dz;
				int ndz = dx;
				dx = ndx;
				dz = ndz;
			}
		}
		return coords;

	}

	private void generateRandomCoords(boolean haveEnoughForAer) {
		if (haveEnoughForAer) {
			this.chunkX = this.rand.nextInt(this.MAP_WIDTH_CHUNKS + 1);
			this.chunkZ = this.rand.nextInt(this.MAP_HEIGHT_CHUNKS + 1);
		}
		else {
			this.chunkX = (this.rand.nextInt(9) - 4) + this.getDawnMachineChunkCoords()[0];
			this.chunkZ = (this.rand.nextInt(9) - 4) + this.getDawnMachineChunkCoords()[1];
		}
	}

	// END COORD GENERATION

	// ASPECT MANAGEMENT

	private void spendAer() {
		int aerCost = this.getAerCost(this.chunkX, this.chunkZ);
		this.spend(DawnMachineResource.AER, aerCost);
//		this.spend(DawnMachineResource.AER, 1); // TODO: remove, debugging purposes
	}

	public int getAerCost(int x, int z) {
        	// sqrt again for balancing
        	int cost = (int) Math.sqrt(Math.hypot(this.dawnMachineChunkCoords[0] - x, this.dawnMachineChunkCoords[1] - z));
        	return Math.max(cost, 8);
    	}

	private int getDiscount(DawnMachineResource resource, boolean simulate) {
		int energyCost = resource.getEnergyCost();
		int manaCost = resource.getManaCost();
		int bloodCost = resource.getBloodCost();

		// why
		int enoughRF = this.currentRF >= energyCost ? 1 : 0, enoughMana = this.currentMana >= manaCost ? 1 : 0,
				enoughBlood = this.fluid.amount >= bloodCost ? 1 : 0;

		// just no

		int discountMultiplier = 1 << (enoughRF + enoughMana + enoughBlood);

		// this sucks
		if (!simulate) {
			int oldBlood = this.fluid.amount;
			int oldMana = this.currentMana;
			this.currentRF = enoughRF > 0 ? this.currentRF - energyCost : this.currentRF;
			this.currentMana = enoughMana > 0 ? this.currentMana - manaCost : this.currentMana;
			this.fluid.amount = enoughBlood > 0 ? this.fluid.amount - bloodCost : this.fluid.amount;
		}

		return discountMultiplier;
	}

	public boolean haveEnoughFor(DawnMachineResource resource) {

		return this.haveEnoughFor(resource, resource.getAspectCost());
	}

	public boolean haveEnoughFor(DawnMachineResource resource, int cost) {
		int discountMultiplier = this.getDiscount(resource, true);
		cost /= discountMultiplier;

		return this.internalAspectList.getAmount(resource.getAspect()) >= cost;
	}

	public void spend(DawnMachineResource resource) {
		this.spend(resource, resource.getAspectCost());
	}

	public void spend(DawnMachineResource resource, int cost) {
		if (!this.haveEnoughFor(resource)) { return; }

		int discountMultiplier = this.getDiscount(resource, false);
		cost /= discountMultiplier;

		this.internalAspectList.remove(resource.getAspect(), cost);
		this.signalUpdate();
	}

	public boolean spendAndCheck(DawnMachineResource resource) {
		this.spend(resource);
		return this.haveEnoughFor(resource);
	}

	public boolean needsMore(Aspect aspect) {
		DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

		if (relevantResource == null) { return false; }

		return (relevantResource.getMaximumValue() - this.internalAspectList.getAmount(aspect)) >= relevantResource.getValueMultiplier();
	}

	// END ASPECT MANAGEMENT

	// HELPER FUNCTIONS

	// Takes chunk coords x, z and returns chunk.
	public static Chunk getChunk(World world, int x, int z) {
		if (world.getChunkProvider().chunkExists(x, z)) {
			Chunk chunk = world.getChunkFromChunkCoords(x, z);
			return chunk;
		}
		return null;
	}

	private int[] getDawnMachineChunkCoords() {
		return new int[] { (int) Math.floor(this.xCoord / 16.0), (int) Math.floor(this.zCoord / 16.0) };
	}

	private long getHash(int x, int z) {
		long hashedCoords = (this.chunkX << 32);
		hashedCoords |= this.chunkZ;
		return hashedCoords;
	}

	// Adds chunk x, z coordinate to get "true" coordinate in relation to the map,
	// not the chunk
	public int[] getBlockCoordsFromChunk(Chunk chunk, int x, int z) {
		return new int[] { chunk.xPosition * 16 + x, chunk.zPosition * 16 + z };
	}

	// END HELPER FUNCTIONS

	// FORGE OVERRIDES

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.readCustomNBT(tag);
	}

	private void readCustomNBT(NBTTagCompound tag) {
		this.index = tag.getInteger("Index");
		this.aerIndex = tag.getInteger("AerIndex");

		this.internalAspectList.readFromNBT(tag.getCompoundTag("Essentia"));
		this.currentRF = tag.getInteger("CurrentRF");

		this.fluid.amount = tag.getInteger("Blood");
		this.currentMana = tag.getInteger("Mana");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		this.writeCustomNBT(tag);
	}

	private void writeCustomNBT(NBTTagCompound tag) {
		NBTTagCompound essentia = new NBTTagCompound();
		this.internalAspectList.writeToNBT(essentia);
		tag.setTag("Essentia", essentia);
		tag.setInteger("Index", this.index);
		tag.setInteger("AerIndex", this.aerIndex);
		tag.setInteger("CurrentRF", this.currentRF);
		tag.setInteger("Blood", this.fluid.amount);
		tag.setInteger("Mana", this.currentMana);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeCustomNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -999, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.readCustomNBT(pkt.func_148857_g());
	}

	protected void signalUpdate() {
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		this.markDirty();
	}

	// END FORGE OVERRIDES

	// THAUMCRAFT INTEGRATION

	// IAspectContainer

	@Override
	public AspectList getAspects() {
		AspectList aspectList = new AspectList();

		for (DawnMachineResource resource : DawnMachineResource.values()) {
			int value = this.internalAspectList.getAmount(resource.getAspect());
			value /= resource.getValueMultiplier();
			aspectList.add(resource.getAspect(), value);
		}

		return aspectList;
	}

	@Override
	public void setAspects(AspectList aspectList) {
		// Ehhhhh
	}

	@Override
	public boolean doesContainerAccept(Aspect aspect) {
		return DawnMachineResource.getResourceFromAspect(aspect) != null;
	}

	@Override
	public int addToContainer(Aspect aspect, int i) {
		DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

		if (relevantResource == null) { return i; }

		int currentValue = this.internalAspectList.getAmount(aspect);
		int remainingRoom = relevantResource.getMaximumValue() - currentValue;

		int essentiaRemaining = remainingRoom / relevantResource.getValueMultiplier();

		if (essentiaRemaining > 0) {
			int essentiaToMove = Math.min(i, essentiaRemaining);
			i -= essentiaToMove;
			this.internalAspectList.add(aspect, essentiaToMove * relevantResource.getValueMultiplier());
			this.signalUpdate();
		}

		return i;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int i) {
		// This container is input-only, we're working here!
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList aspectList) {
		// This container is input-only, we're working here!
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int i) {
		if (i == 0) { return true; }

		DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

		if (relevantResource == null) { return false; }

		int currentValue = this.internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();

		return (currentValue >= i);
	}

	@Override
	public boolean doesContainerContain(AspectList aspectList) {
		boolean successful = true;
		for (Aspect aspect : aspectList.getAspects()) {
			successful = this.doesContainerContainAmount(aspect, aspectList.getAmount(aspect)) && successful;
		}

		return successful;
	}

	@Override
	public int containerContains(Aspect aspect) {
		DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

		if (relevantResource == null) { return 0; }

		return this.internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();
	}

	// END THAUMCRAFT INTEGRATION

	// RF INTEGRATION

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (from != ForgeDirection.DOWN) { return 0; }

		int room = MAX_RF - this.currentRF;

		int actualReceive = Math.min(maxReceive, room);

		if (!simulate) { this.currentRF += actualReceive; }

		this.signalUpdate();

		return actualReceive;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (from != ForgeDirection.DOWN) { return 0; }

		return this.currentRF;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (from != ForgeDirection.DOWN) { return 0; }

		return MAX_RF;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return (from == ForgeDirection.DOWN);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return this.receiveEnergy(ForgeDirection.DOWN, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() { return this.currentRF; }

	@Override
	public int getMaxEnergyStored() { return MAX_RF; }

	// END RF INTEGRATION

	// FLUID INTEGRATION (BLOOD MAGIC)

	// IFluidTank

	@Override
	public FluidStack getFluid() { return this.fluid; }

	@Override
	public int getFluidAmount() { return this.fluid.amount; }

	@Override
	public int getCapacity() { return MAX_BLOOD; }

	@Override
	public FluidTankInfo getInfo() { return this.tankInfo; }

	@Override
	public int fill(FluidStack fluidstack, boolean simulate) {
		if (fluidstack.getFluid().getID() == blood.getID()) {
			int change = Math.max(Math.min(MAX_BLOOD - this.fluid.amount, fluidstack.amount), 0);
			if (!simulate) { this.fluid.amount += change; }
			return change;
		}
		return 0;
	}

	@Override
	public FluidStack drain(int amount, boolean simulate) {
		return null;
	}

	// IFluidHandler

	@Override
	public int fill(ForgeDirection direction, FluidStack fluidstack, boolean simulate) {
		return this.fill(fluidstack, simulate);
	}

	@Override
	public FluidStack drain(ForgeDirection direction, FluidStack fluidstack, boolean simulate) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection direction, int amount, boolean simulate) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection direction, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction) {
		return this.tankInfoArray;
	}

	// END FLUID INTEGRATION

	// BLOODMAGIC INTEGRATION

	public int addBlood(int bloodAmount, boolean simulate) {
		FluidStack blood = new FluidStack(DawnMachineTileEntity.blood, bloodAmount);
		return this.fill(blood, simulate);
	}

	// END BLOOGMAGIC INTEGRATION

	// BOTANIA INTEGRATION

	// IManaReceiver

	@Override
	public boolean isFull() { return this.currentMana >= MAX_MANA; }

	@Override
	public void recieveMana(int mana) {
		this.currentMana = Math.max(0, Math.min(MAX_MANA, this.currentMana + mana));
		// i
		// have
		// no
		// idea
		// what
		// this
		// does
		this.worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return false;
	}

	// ISparkAttachable

	@Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public void attachSpark(ISparkEntity entity) {}

	@Override
	public ISparkEntity getAttachedSpark() {
		// Code used from the TA Plate in Botania
		List<ISparkEntity> sparks = this.worldObj.getEntitiesWithinAABB(ISparkEntity.class,
				AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 1, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1));
		if (sparks.size() == 1) {
			Entity e = (Entity) sparks.get(0);
			return (ISparkEntity) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() {
		return this.currentMana >= MAX_MANA;
	}

	public int getAvailableSpaceForMana() { return MAX_MANA - this.currentMana; }

	// IManaBlock

	@Override
	public int getCurrentMana() { return this.currentMana; }

	// END BOTANIA INTEGRATION

	// CHUNKLOADING FUNCTIONS

	private void initalizeChunkloading() {
		this.dawnMachineTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, this.getWorldObj(), ForgeChunkManager.Type.NORMAL);
		this.dawnMachineTicket.getModData().setString("id", "DawnMachine");
		int[] dawnMachineCoords = this.getDawnMachineChunkCoords();
		ForgeChunkManager.forceChunk(this.dawnMachineTicket, new ChunkCoordIntPair(dawnMachineCoords[0], dawnMachineCoords[1]));
		this.hasInitializedChunkloading = true;
	}

	private void loadChunk() {
		if (Arrays.equals(new int[] { this.chunkX, this.chunkZ }, this.getDawnMachineChunkCoords())) { return; }
		ForgeChunkManager.forceChunk(this.dawnMachineTicket, new ChunkCoordIntPair(this.chunkX, this.chunkZ));
	}

	private void unloadChunk() {
		if (Arrays.equals(new int[] { this.chunkX, this.chunkZ }, this.getDawnMachineChunkCoords())) { return; }
		ForgeChunkManager.unforceChunk(this.dawnMachineTicket, new ChunkCoordIntPair(this.chunkX, this.chunkZ));
	}

	private boolean isChunkLoaded(int x, int z) {
		for (ChunkCoordIntPair coords : this.dawnMachineTicket.getChunkList()) {
			if (coords.chunkXPos == x && coords.chunkZPos == z) { return true; }
		}
		return false;
	}

	public void forceChunkLoading(Ticket ticket) {
		if (this.dawnMachineTicket == null) { this.dawnMachineTicket = ticket; }
		int[] dawnMachineCoords = this.getDawnMachineChunkCoords();
		ForgeChunkManager.forceChunk(this.dawnMachineTicket, new ChunkCoordIntPair(dawnMachineCoords[0], dawnMachineCoords[1]));
	}

	@Override
	public void invalidate() {
		ForgeChunkManager.releaseTicket(this.dawnMachineTicket);
		super.invalidate();
	}

	// END CHUNKLOADING FUNCTIONS

	// MISC. FUNCTIONS

	public void pairDawnCharger(NBTTagCompound tag) {
		this.writeToNBT(tag);
	}

	public Vec3 getGlowColor(double partialTicks) {
		if (this.currentRF >= FULLGREEN_RF) {
			return COLOR_GREEN;
		}
		else if (this.currentRF >= FULLYELLOW_RF) {
			double progress = (double) (this.currentRF - FULLYELLOW_RF) / (double) (FULLGREEN_RF - FULLYELLOW_RF);
			return this.interpColor(COLOR_GREEN, COLOR_YELLOW, progress);
		}
		else if (this.currentRF >= FULLRED_RF) {
			double progress = (double) (this.currentRF - FULLRED_RF) / (double) (FULLYELLOW_RF - FULLRED_RF);
			return this.interpColor(COLOR_YELLOW, COLOR_RED, progress);
		}
		else if (this.currentRF > DEAD_RF) {
			return COLOR_RED;
		}
		else {
			return null;
		}
	}

	protected Vec3 interpColor(Vec3 left, Vec3 right, double progress) {
		double red = (left.xCoord - right.xCoord) * progress;
		double green = (left.yCoord - right.yCoord) * progress;
		double blue = (left.zCoord - right.zCoord) * progress;

		return Vec3.createVectorHelper(right.xCoord + red, right.yCoord + green, right.zCoord + blue);
	}

	// END MISC. FUNCTIONS

}
