package talonos.blightbuster.tileentity;

import java.lang.reflect.Constructor;
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
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.tileentity.dawnmachine.DawnMachineResource;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
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

public class DawnMachineTileEntity extends TileEntity implements 
IAspectSource, IAspectContainer, IEnergyReceiver, IEnergyStorage,
ISparkAttachable, 
IFluidTank, IFluidHandler {
	
	public int[] NW_chunk;
	public int[] SE_chunk;
	public boolean isActive = true;

	// FLUID INTEGRATION (Blood Magic)
	
	private static final int MAX_BLOOD = 10000;
	private static final Fluid blood = AlchemicalWizardry.lifeEssenceFluid;
	private FluidStack fluid = new FluidStack(blood, 0);
	private FluidTankInfo tankInfo = new FluidTankInfo(this.getFluid(), MAX_BLOOD);
	private FluidTankInfo[] tankInfoArray = {tankInfo};

	// BOTANIA INTEGRATION
	
	private static final int MAX_MANA = 100000;
	private int currentMana = 0;
	
	// RF INTEGRATION
	
    public static final int DEAD_RF = 150; // Used in talonos.blightbuster.client.DawnMachineControllerRenderer
    protected static final int MAX_RF = 128000;
    protected static final int FULLGREEN_RF = 80000;
    protected static final int FULLYELLOW_RF = 40000;
    protected static final int FULLRED_RF = 20000;
    protected static final Vec3 COLOR_GREEN = Vec3.createVectorHelper(0, 0.9, 0);
    protected static final Vec3 COLOR_YELLOW = Vec3.createVectorHelper(0.9, 0.9, 0);
    protected static final Vec3 COLOR_RED = Vec3.createVectorHelper(0.9, 0, 0);
    private int currentRf = 0;
    

    
    // THAUMCRAFT INTEGRATION
    
    private AspectList internalAspectList = new AspectList();

    // aer fields
    
    private boolean aerIsActive = false;
    private int aerCooldownRemaining = 0;
    public static final int AER_COOLDOWN = 20 * 30;
    private boolean spendAer = false;
    private boolean hasInitializedChunkloading = false;

    public int chunkX;
    public int chunkZ;
    public final int MAP_WIDTH_CHUNKS = 112;
	public final int MAP_HEIGHT_CHUNKS = 135;
    
    private int ticksSinceLastCleanse = 0;

    public int lastChunkX = 0; // last cleansed x coord
    public int lastChunkZ = 1; // last cleansed z coord

    static private HashMap<Class<?>, Constructor<?>> taint_purified_constructors = new HashMap<Class<?>, Constructor<?>>();
	
	static {
	    final Class<?>[] taintedEntities = {
				EntityTaintSheep.class, 
				EntityTaintChicken.class, 
				EntityTaintCow.class, 
				EntityTaintPig.class, 
				EntityTaintVillager.class, 
				EntityTaintCreeper.class};
	    
		final Class<?>[] purifiedEntities = {
				EntitySheep.class,
				EntityChicken.class,
				EntityCow.class,
				EntityPig.class,
				EntityVillager.class,
				EntityCreeper.class
		};
		Constructor<?>[] purifiedConstructors = new Constructor<?>[6];
	    
		
    	for (int i = 0; i < taintedEntities.length; i++) {
    		try {
    			taint_purified_constructors.put(taintedEntities[i], purifiedEntities[i].getConstructor(new Class[] {World.class}));
			} catch (Exception e) { e.printStackTrace(); }
    	}
	}
	
    
    // Used for generating coordinates
    Random rand;

    public DawnMachineTileEntity() {
    	rand = new Random(System.currentTimeMillis());

    }
    public static Chunk getChunk(World world, int x, int z) {
    	x *= 16;
    	z *= 16;
    	if (world.isRemote || world.getChunkProvider().chunkExists(x >> 4, z >> 4)) {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            System.out.println("getChunk: " + chunk.xPosition + " " + chunk.zPosition);
            return chunk;
    	}
    	return null;
    }

    @Override
     // 13, 2
    public void updateEntity() {

    	if (getWorldObj().isRemote) {
    		return;
    	}
    	
        if (getWorldObj().getIndirectPowerLevelTo(xCoord, yCoord, zCoord, 1) > 0) {
            return;
        }
        
        if (!(currentMana >= MAX_MANA)) {
    		recieveMana(1000);
    	}

        if (!hasInitializedChunkloading) {
//	            BlightBuster.instance.chunkLoader.newDawnMachine(this);
            hasInitializedChunkloading = true;
        }
        if (aerCooldownRemaining > 0) {
            aerCooldownRemaining--;
        }

        int cleanseLength = haveEnoughFor(DawnMachineResource.MACHINA) ? 4 : 12;

        ticksSinceLastCleanse %= cleanseLength;

        if (ticksSinceLastCleanse == 0) {
            getNewCleanseCoords();
            Chunk chunk = getChunk(worldObj, chunkX, chunkZ);
            if (chunk != null) {
                setUpAerRange();
    	        if (cleanseLength == 4) {
    	            spend(DawnMachineResource.MACHINA);
    	        }
                executeCleanse(chunk);
                ticksSinceLastCleanse++;
            }
        }
        else {
            ticksSinceLastCleanse++;
        }
    }


	protected void executeCleanse(Chunk chunk) {

        if (spendAer) {
            spendAer = false;
            spend(DawnMachineResource.AER);
        }
        List<Entity>[] entityLists = chunk.entityLists;
        for (List<Entity> list : entityLists) {
	        for (Entity entity : list) {
	        	if (entity instanceof EntityTaintSporeSwarmer || entity instanceof EntityFallingTaint) {
		            if (haveEnoughFor(DawnMachineResource.IGNIS)) {
		                spend(DawnMachineResource.IGNIS);
		
		                if (haveEnoughFor(DawnMachineResource.VACUOS)) {
		                    spend(DawnMachineResource.VACUOS);
		                }
		                else {
		                    getWorldObj().setBlock((int)entity.posX, (int)entity.posY, (int)entity.posZ, ConfigBlocks.blockFluxGoo, ((BlockFluxGoo)ConfigBlocks.blockFluxGoo).getQuanta(), 3);
		                }
	                    entity.setDead();
		            }
	        	}
	        }
        }
        cleanseBiome(chunk);
        boolean didUseIgnis = cleanseBlocks(chunk);
        if (haveEnoughFor(DawnMachineResource.SANO)) {
//            cleanseMobs(chunk);
        }

//        BlightbusterNetwork.sendToNearbyPlayers(new SpawnCleanseParticlesPacket(lastCleanseX, lastCleanseZ, didUseIgnis, true), worldObj.provider.dimensionId, lastCleanseX, 128.0f, lastCleanseZ, 150);
    }
 // TODO: getEntitiesWithinAABBForEntity
	 // TODO: getEntitiesOfTypeWithinAAAB
    protected boolean hasAnythingToCleanseHere(int secondaryBlocks) {
        //Can cleanse biome?
//        for (int z = -1; z <= 2; z++) {
//            for (int x = -1; x <= 2; x++) {
//                if (z < 0 && (secondaryBlocks & 0x4) == 0)
//                    continue;
//                if (z > 1 && (secondaryBlocks & 0x8) == 0)
//                    continue;
//                if (x < 0 && (secondaryBlocks & 0x1) == 0)
//                    continue;
//                if (x > 1 && (secondaryBlocks & 0x2) == 0)
//                    continue;
//
//                BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(lastCleanseX+x, lastCleanseZ+z);
//                if (biome.biomeID == Config.biomeTaintID ||
//                        biome.biomeID == Config.biomeEerieID ||
//                        biome.biomeID == Config.biomeMagicalForestID)
//                    return true;
//            }
//        }
//
//        if (haveEnoughFor(DawnMachineResource.SANO)) {
//            if (getWorldObj().getEntitiesWithinAABB(ITaintedMob.class, AxisAlignedBB.getBoundingBox(lastCleanseX, 0, lastCleanseZ, lastCleanseX+2, 255, lastCleanseZ+2)).size() > 0)
//                return true;
//        }
//
//        for (int z = 0; z <= 1; z++) {
//            for (int x = 0; x <= 1; x++) {
//                int herbaTopBlock = -1;
//                boolean canHerba = haveEnoughFor(DawnMachineResource.HERBA);
//                if (canHerba) {
//                    herbaTopBlock = getWorldObj().getTopSolidOrLiquidBlock(lastCleanseX+x, lastCleanseZ+z);
//                }
//
//                boolean canIgnis = haveEnoughFor(DawnMachineResource.IGNIS);
//                boolean canVacuos = haveEnoughFor(DawnMachineResource.VACUOS);
//                boolean canAura = haveEnoughFor(DawnMachineResource.AURAM);
//                if (canIgnis || canVacuos || canHerba || canAura) {
//                    //Are there any taint blocks to cleanse?
//                    for (int i = 0; i < 256; i++) {
//                        Block block = getWorldObj().getBlock(lastCleanseX+x, i, lastCleanseZ+z);
//                        int meta = getWorldObj().getBlockMetadata(lastCleanseX+x, i, lastCleanseZ+z);
//
//                        if (canIgnis && block == ConfigBlocks.blockTaintFibres)
//                            return true;
//                        if (canIgnis && block == ConfigBlocks.blockTaint && meta != 2)
//                            return true;
//                        if (canVacuos && block == ConfigBlocks.blockFluxGoo)
//                            return true;
//                        if (canHerba && i == herbaTopBlock && block == Blocks.dirt)
//                            return true;
//                        if (canAura && block == ConfigBlocks.blockAiry) {
//                            if (meta == 0) {
//                                TileNode node = (TileNode)getWorldObj().getTileEntity(lastCleanseX+x, i, lastCleanseZ+z);
//                                if (node != null) {
//                                    if (node.getNodeType() == NodeType.TAINTED) {
//                                        return true;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        return true;
    }

    protected void cleanseBiome(Chunk chunk) {
		BiomeGenBase[] genBiomes = null;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				
                genBiomes = getWorldObj().getWorldChunkManager().loadBlockGeneratorData(genBiomes, chunk.xPosition * 16+x, chunk.zPosition+z, 1, 1);
                BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(x, z);
                if (biome.biomeID == Config.biomeTaintID ||
                        biome.biomeID == Config.biomeEerieID ||
                        biome.biomeID == Config.biomeMagicalForestID) {

                    if (genBiomes != null && genBiomes.length > 0 && genBiomes[0] != null) {
                    		BlightbusterNetwork.setBiomeAt(getWorldObj(), chunk.xPosition * 16+x, chunk.zPosition+z, genBiomes[0]);
                    }
                }
			}
		}
    }

    protected boolean cleanseBlocks(Chunk chunk) {

        boolean haveUsedIgnis = false;
    	
    	for (int x = 0; x < 16; x++) {
    		for (int z = 0; z < 16; z++) {
    			// entire chunk
    			
                // - Ignis is used to cleanse tainted blocks
                // - Vacuos will remove crusted taint blocks when ignis acts on them, instead of converting to flux goo
                // - Herba will convert the top dirt block to grass/top tainted soil block to grass instead of dirt
                // - Auram will convert tained node blocks to normal node blocks
                // - Arbor will detect when we've found a stack of crusted taint 3 high and put a sapling there
                boolean canArbor = haveEnoughFor(DawnMachineResource.ARBOR);
                boolean canHerba = haveEnoughFor(DawnMachineResource.HERBA);

                //Get the y-value of the block herba might want to act on
                boolean foundTopBlock = false;
                int topBlock = -1;
                //We do the cleanse from top to bottom and every time we find crusted taint, we count how many consecutive
                //ones we cleanse.  When we find something that isn't crusted taint, after cleansing it, we plant a sapling on
                //it if there was enough crusted taint above it
                int columnCrustedTaint = 0;
                for (int y = 255; y >= 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    int meta = chunk.getBlockMetadata(x, y, z);

                    boolean thisIsCrustedTaint = (block == ConfigBlocks.blockTaint && meta == 0);

                    if (thisIsCrustedTaint && haveEnoughFor(DawnMachineResource.IGNIS) && haveEnoughFor(DawnMachineResource.VACUOS)) {
                        columnCrustedTaint++;
                    }

                    if (!foundTopBlock && (canHerba || canArbor) && block.isOpaqueCube()) {
                        foundTopBlock = true;
                        topBlock = y;
                    }

                    boolean didUseIgnis = cleanseSingleBlock(x, y, z, block, meta, canHerba && foundTopBlock && y == topBlock);
                    haveUsedIgnis = didUseIgnis || haveUsedIgnis;

                    if (didUseIgnis && getWorldObj().getBlock(x, y, z) != Blocks.dirt) {
                        foundTopBlock = false;
                    }
                }

                if (columnCrustedTaint >= 3 && foundTopBlock) {
                	
                	// TODO: Use chunk instead of getWorldObj()
                    BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(x, z);
                    String biomeName = biome.biomeName.toLowerCase(Locale.ENGLISH);

                    //Default to oak
                    int treeType = 0;
                    if (biomeName.contains("taiga") || biomeName.contains("tundra")) {
						treeType = 1; //Spruce trees
					} else if (biomeName.contains("birch")) {
						treeType = 2; //Birch trees
					} else if (biomeName.contains("jungle")) {
						treeType = 3; //Jungle tree
					} else if (biomeName.contains("savanna")) {
						treeType = 4; //Acacia trees
					} else if (biomeName.contains("roof")) {
						treeType = 5;
					}

                    // TODO: use chunk instead of getWorldObj if possible
                    getWorldObj().setBlock(x, topBlock + 1, z, Blocks.sapling, treeType, 3);
                    spend(DawnMachineResource.ARBOR);
                }
                
                if (foundTopBlock) {
                    BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(x, z);
                    String biomeName = biome.biomeName.toLowerCase(Locale.ENGLISH);
                    
                    if (biomeName.contains("desert")) {
                    	int chance = rand.nextInt(200) + 1;
                    	boolean block = rand.nextBoolean();
                    	if (chance == 200) {
                    		if (block) {
                                getWorldObj().setBlock(x, topBlock + 1, z, Blocks.cactus);
                    		}
                    		else {
                                getWorldObj().setBlock(x, topBlock + 1, z, ConfigBlocks.blockCustomPlant, 3, 3);
                    		}
                            spend(DawnMachineResource.ARBOR);
                    	}
                    }
                }
            }
        }

        return haveUsedIgnis;
    }

    private boolean cleanseSingleBlock(int x, int y, int z, Block block, int meta, boolean doHerbaCheck) {
        boolean canIgnis = haveEnoughFor(DawnMachineResource.IGNIS);
        boolean canVacuos = haveEnoughFor(DawnMachineResource.VACUOS);

        //Cleanse crusted taint
        if (canIgnis && block == ConfigBlocks.blockTaint && meta == 0) {
            spend(DawnMachineResource.IGNIS);

            Block replaceBlock = ConfigBlocks.blockFluxGoo;
            int replaceMeta = ((BlockFluxGoo)ConfigBlocks.blockFluxGoo).getQuanta();

            if (haveEnoughFor(DawnMachineResource.VACUOS)) {
                spend(DawnMachineResource.VACUOS);
                replaceBlock = Blocks.air;
                replaceMeta = 0;
            }

            getWorldObj().setBlock(x, y, z, replaceBlock, replaceMeta, 3);
            return true;
        }

        //Cleanse tainted soil
        if (canIgnis && block == ConfigBlocks.blockTaint && meta == 1) {
            spend(DawnMachineResource.IGNIS);

            Block replaceBlock = Blocks.dirt;
            if (doHerbaCheck) {
                spend(DawnMachineResource.HERBA);
                if (getWorldObj().getBiomeGenForCoords(x, z).biomeID == 14 || getWorldObj().getBiomeGenForCoords(x, z).biomeID == 15) {
                	replaceBlock = Blocks.mycelium;
                }
                else {
                	replaceBlock = Blocks.grass;
                }
            }

            getWorldObj().setBlock(x, y, z, replaceBlock);
            return true;
        }

        //Cleanse taint fibres
        if (canIgnis && block == ConfigBlocks.blockTaintFibres) {
            spend(DawnMachineResource.IGNIS);
            getWorldObj().setBlock(x, y, z, Blocks.air);
            return true;
        }

        if (canVacuos && block == ConfigBlocks.blockFluxGoo) {
            spend(DawnMachineResource.VACUOS);

            getWorldObj().setBlock(x, y, z, Blocks.air);
            return false;
        }

        if (doHerbaCheck && block == Blocks.dirt) {
            spend(DawnMachineResource.HERBA);
            getWorldObj().setBlock(x, y, z, Blocks.grass);
            return false;
        }

        if (haveEnoughFor(DawnMachineResource.AURAM) && GameRegistry.findUniqueIdentifierFor(block).modId.equals("Thaumcraft")) {
            TileEntity tile = getWorldObj().getTileEntity(x, y, z);

            if (tile != null && tile instanceof TileNode) {
                TileNode node = (TileNode)tile;
                if (node != null && node.getNodeType() == NodeType.TAINTED) {
                    spend(DawnMachineResource.AURAM);
                    node.setNodeType(NodeType.NORMAL);
                    node.markDirty();
                    getWorldObj().markBlockForUpdate(x, y, z);
                }
            }
            return false;
        }

        return false;
    }

    protected void cleanseMobs(Chunk chunk) {

		List<Entity>[] entityLists = chunk.entityLists.clone();
		for (List<Entity> list : entityLists) {
		    for (Object entityObj : list) {
			    if (DawnMachineTileEntity.taint_purified_constructors.containsKey(entityObj.getClass())) {
			        spend(DawnMachineResource.SANO);
			        try {
						cleanseSingleMob((Entity)entityObj, (EntityLivingBase) taint_purified_constructors.get(entityObj.getClass()).newInstance(getWorldObj()));
					} catch (Exception e) {}
			    }
			    if (!haveEnoughFor(DawnMachineResource.SANO)) {
			        return;
			    }
			}
		}
    }

    private void cleanseSingleMob(Entity tainted, EntityLivingBase cleansed) {
        //new entity copies original entity location
        cleansed.copyLocationAndAnglesFrom(tainted);
        //original entity spawns new entity into the world
        tainted.worldObj.spawnEntityInWorld(cleansed);
        //new entity removes the old entity
        cleansed.worldObj.removeEntity(tainted);
    }

    public boolean spendAndCheck(DawnMachineResource resource) {
        spend(resource);
        return haveEnoughFor(resource);
    }

    public boolean haveEnoughFor(DawnMachineResource resource) {

        int discountMultiplier = getDiscount(resource);

        int cost = resource.getAspectCost();
        cost /= discountMultiplier;

        return internalAspectList.getAmount(resource.getAspect()) >= cost;
    }

    public void spend(DawnMachineResource resource) {
        if (!haveEnoughFor(resource)) {
			return;
		}
        
        int discountMultiplier = getDiscount(resource);
        int cost = resource.getAspectCost();
        cost /= discountMultiplier;

        internalAspectList.remove(resource.getAspect(), cost);
        signalUpdate();
    }
    
    private int getDiscount(DawnMachineResource resource) {
        int energyCost = resource.getEnergyCost();
        int manaCost = resource.getManaCost();
        int bloodCost = resource.getBloodCost();
        int discountMultiplier = 1;
        
        if (currentRf >= energyCost) {
        	discountMultiplier *= 2;
        	currentRf -= energyCost;
        }
        if (currentMana >= manaCost) {
        	discountMultiplier *= 2;
        	currentMana -= manaCost;
        }
        if (fluid.amount >= bloodCost) {
        	discountMultiplier *= 2;
        	fluid.amount -= bloodCost;
        }
        return discountMultiplier;
    }
    
    
    // rewrite
    private void setUpAerRange() {
//        boolean aerChunkLoadingActive = BlightBuster.instance.chunkLoader.getAerStatus(getWorldObj(), xCoord, yCoord, zCoord);
//        boolean canAffordAer = haveEnoughFor(DawnMachineResource.AER);
//
//        if (canAffordAer != this.aerIsActive) {
//            if (!canAffordAer || aerCooldownRemaining <= 0) {
//                this.aerIsActive = canAffordAer;
//
//                if (!canAffordAer)
//                    this.aerCooldownRemaining = AER_COOLDOWN;
//            }
//        }
//
//        if (this.aerIsActive != aerChunkLoadingActive) {
//            BlightBuster.instance.chunkLoader.changeAerStatus(getWorldObj(), xCoord, yCoord, zCoord, this.aerIsActive);
//        }
    }

    private void getNewCleanseCoords() {
        if (haveEnoughFor(DawnMachineResource.ORDO)) {
            spend(DawnMachineResource.ORDO);
            generateScanlineCoords();
        } else {
			generateRandomCoords();
		}
    }

    private void generateScanlineCoords() {
        lastChunkX = chunkX;
        lastChunkZ = chunkZ;
        if (lastChunkX >= MAP_WIDTH_CHUNKS) {
            chunkX = 0;
        } else {
        	chunkX++;
        	return;
    	}
        if (lastChunkZ >= MAP_HEIGHT_CHUNKS) {
            chunkZ = 1;
        } else {
        	chunkZ++; 
    	}
    }

    private void generateRandomCoords() {
        chunkX = rand.nextInt(MAP_WIDTH_CHUNKS);
        chunkZ = rand.nextInt(MAP_HEIGHT_CHUNKS);
    }

    public boolean needsMore(Aspect aspect) {
        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null) {
			return false;
		}

        return (relevantResource.getMaximumValue() - internalAspectList.getAmount(aspect)) >= relevantResource.getValueMultiplier();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        readCustomNBT(tag);
    }

    private void readCustomNBT(NBTTagCompound tag) {
        internalAspectList.readFromNBT(tag.getCompoundTag("Essentia"));
        currentRf = tag.getInteger("CurrentRF");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        writeCustomNBT(tag);
    }

    private void writeCustomNBT(NBTTagCompound tag) {
        NBTTagCompound essentia = new NBTTagCompound();
        internalAspectList.writeToNBT(essentia);
        tag.setTag("Essentia", essentia);

        tag.setInteger("CurrentRF", currentRf);
    }

    public void pairDawnCharger(NBTTagCompound tag) {
    	this.writeToNBT(tag);
    }
    
    protected void signalUpdate() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        markDirty();
    }

    @Override
	public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeCustomNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -999, nbttagcompound);
    }

    @Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readCustomNBT(pkt.func_148857_g());
    }

    public Vec3 getGlowColor(double partialTicks) {
        if (currentRf >= FULLGREEN_RF) {
			return COLOR_GREEN;
		} else if (currentRf >= FULLYELLOW_RF) {
            double progress = (double)(currentRf - FULLYELLOW_RF) / (double)(FULLGREEN_RF - FULLYELLOW_RF);
            return interpColor(COLOR_GREEN, COLOR_YELLOW, progress);
        } else if (currentRf >= FULLRED_RF) {
            double progress = (double)(currentRf - FULLRED_RF) / (double)(FULLYELLOW_RF - FULLRED_RF);
            return interpColor(COLOR_YELLOW, COLOR_RED, progress);
        } else if (currentRf > DEAD_RF) {
			return COLOR_RED;
		} else {
			return null;
		}
    }

    protected Vec3 interpColor(Vec3 left, Vec3 right, double progress) {
        double red = (left.xCoord - right.xCoord) * progress;
        double green = (left.yCoord - right.yCoord) * progress;
        double blue = (left.zCoord - right.zCoord) * progress;

        return Vec3.createVectorHelper(right.xCoord + red, right.yCoord + green, right.zCoord + blue);
    }

    // THAUMCRAFT INTEGRATION (IAspectContainer)
    
    @Override
    public AspectList getAspects() {
        AspectList aspectList = new AspectList();

        for (DawnMachineResource resource : DawnMachineResource.values()) {
            int value = internalAspectList.getAmount(resource.getAspect());
            value /= resource.getValueMultiplier();
            aspectList.add(resource.getAspect(), value);
        }

        return aspectList;
    }

    @Override
    public void setAspects(AspectList aspectList) {
        //Ehhhhh
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return DawnMachineResource.getResourceFromAspect(aspect) != null;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null) {
			return i;
		}

        int currentValue = internalAspectList.getAmount(aspect);
        int remainingRoom = relevantResource.getMaximumValue() - currentValue;

        int essentiaRemaining = remainingRoom / relevantResource.getValueMultiplier();

        if (essentiaRemaining > 0) {
            int essentiaToMove = Math.min(i, essentiaRemaining);
            i -= essentiaToMove;
            internalAspectList.add(aspect, essentiaToMove * relevantResource.getValueMultiplier());
            signalUpdate();
        }

        return i;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        //This container is input-only, we're working here!
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        //This container is input-only, we're working here!
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        if (i == 0) {
			return true;
		}

        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null) {
			return false;
		}

        int currentValue = internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();

        return (currentValue >= i);
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        boolean successful = true;
        for (Aspect aspect : aspectList.getAspects()) {
			successful = doesContainerContainAmount(aspect, aspectList.getAmount(aspect)) && successful;
		}

        return successful;
    }

    @Override
    public int containerContains(Aspect aspect) {
        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null) {
			return 0;
		}

        return internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();
    }
    
    // RF INTEGRATION
    
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (from != ForgeDirection.DOWN) {
			return 0;
		}

        int room = MAX_RF - currentRf;

        int actualReceive = Math.min(maxReceive, room);

        if (!simulate) {
			currentRf += actualReceive;
		}

        signalUpdate();

        return actualReceive;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (from != ForgeDirection.DOWN) {
			return 0;
		}

        return currentRf;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (from != ForgeDirection.DOWN) {
			return 0;
		}

        return MAX_RF;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return (from == ForgeDirection.DOWN);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receiveEnergy(ForgeDirection.DOWN, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) { return 0; }

    @Override
    public int getEnergyStored() { return currentRf; }

    @Override
    public int getMaxEnergyStored() { return MAX_RF; }
    
    // FLUID INTEGRATION (BLOOD MAGIC)

    //IFluidTank
    
	@Override
	public FluidStack getFluid() { return fluid; }

	@Override
	public int getFluidAmount() { return fluid.amount; }

	@Override
	public int getCapacity() { return MAX_BLOOD; }

	@Override
	public FluidTankInfo getInfo() { return tankInfo; }

	@Override
	public int fill(FluidStack paramFluidStack, boolean paramBoolean) {
		if (paramFluidStack.getFluid().getID() == blood.getID()) {
			int change = Math.max(Math.min(MAX_BLOOD - fluid.amount, paramFluidStack.amount), 0);
			if (paramBoolean) {
				this.fluid.amount += change;
			}
			return change;
		}
		return 0;
	}

	@Override
	public FluidStack drain(int paramInt, boolean paramBoolean) { return null; }

	// IFluidHandler
	
	@Override
	public int fill(ForgeDirection paramForgeDirection, FluidStack paramFluidStack, boolean paramBoolean) {
		if (paramFluidStack.getFluid().getID() == blood.getID()) {
			int change = Math.max(Math.min(MAX_BLOOD - fluid.amount, paramFluidStack.amount), 0);
			if (paramBoolean) {
				this.fluid.amount += change;
			}
			return change;
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection paramForgeDirection, FluidStack paramFluidStack, boolean paramBoolean) { return null; }

	@Override
	public FluidStack drain(ForgeDirection paramForgeDirection, int paramInt, boolean paramBoolean) { return null; }

	@Override
	public boolean canFill(ForgeDirection paramForgeDirection, Fluid paramFluid) { return true; }

	@Override
	public boolean canDrain(ForgeDirection paramForgeDirection, Fluid paramFluid) { return false; }

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection paramForgeDirection) { return tankInfoArray; }
	
	// BOTANIA INTEGRATION
	
	//IManaReceiver
	
	@Override
	public boolean isFull() { return currentMana >= MAX_MANA; }

	@Override
	public void recieveMana(int mana) {
		this.currentMana = Math.max(0, Math.min(MAX_MANA, this.currentMana + mana));
		worldObj.func_147453_f(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord)); // i have no idea what this does
	}

	@Override
	public boolean canRecieveManaFromBursts() { return false; }

	// ISparkAttachable
	
	@Override
	public boolean canAttachSpark(ItemStack stack) { return true; }

	@Override
	public void attachSpark(ISparkEntity entity) {}

	@Override
	public ISparkEntity getAttachedSpark() {
		// Code used from the TA Plate in Botania
		List<ISparkEntity> sparks = worldObj.getEntitiesWithinAABB(ISparkEntity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if(sparks.size() == 1) {
			Entity e = (Entity) sparks.get(0);
			return (ISparkEntity) e;
		}

		return null;
	}

	@Override
	public boolean areIncomingTranfersDone() { return currentMana >= MAX_MANA; }
	
	public int getAvailableSpaceForMana() { return MAX_MANA - currentMana; }
	
	// IManaBlock
	
	@Override
	public int getCurrentMana() { return currentMana; }
}
