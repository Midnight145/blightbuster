package talonos.blightbuster.tileentity;

import java.util.List;
import java.util.Locale;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
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

public class DawnMachineTileEntity extends TileEntity implements IAspectSource, IAspectContainer, IEnergyReceiver, IEnergyStorage {

    private int currentRf = 0;
    public static final int MAX_RF = 128000;
    protected static final int FULLGREEN_RF = 80000;
    protected static final int FULLYELLOW_RF = 40000;
    protected static final int FULLRED_RF = 20000;
    public static final int DEAD_RF = 150;
    protected static final Vec3 COLOR_GREEN = Vec3.createVectorHelper(0, 0.9, 0);
    protected static final Vec3 COLOR_YELLOW = Vec3.createVectorHelper(0.9, 0.9, 0);
    protected static final Vec3 COLOR_RED = Vec3.createVectorHelper(0.9, 0, 0);

    private boolean aerIsActive = false;
    private int aerCooldownRemaining = 0;
    public static final int AER_COOLDOWN = 20 * 30;
    private boolean spendAer = false;

    private int ticksSinceLastCleanse = 0;

    private int lastCleanseX = Integer.MAX_VALUE;
    private int lastCleanseZ = Integer.MAX_VALUE;

    private AspectList internalAspectList = new AspectList();

    private boolean hasInitializedChunkloading = false;
    
    private boolean chargerPaired = false;

    public DawnMachineTileEntity() {

    }

    @Override
    public void updateEntity() {

        if (getWorldObj().getIndirectPowerLevelTo(xCoord, yCoord, zCoord, 1) > 0) {
            return;
        }

        if (getWorldObj().isRemote) {
            return;
        }

        if (!hasInitializedChunkloading) {
            BlightBuster.instance.chunkLoader.newDawnMachine(getWorldObj(), xCoord, yCoord, zCoord);
            hasInitializedChunkloading = true;
        }

        if (aerCooldownRemaining > 0) {
            aerCooldownRemaining--;
        }

        int cleanseLength = haveEnoughFor(DawnMachineResource.MACHINA) ? 4 : 12;

        ticksSinceLastCleanse %= cleanseLength;

        if (ticksSinceLastCleanse == 0) {
            setUpAerRange();

            for (int i = 0; i < 5; i++) {
                int secondaryBlocks = getNewCleanseCoords();

                boolean anythingToDo = hasAnythingToCleanseHere(secondaryBlocks);

                if (!anythingToDo) {
                    if (haveEnoughFor(DawnMachineResource.COGNITIO)) {
                        spend(DawnMachineResource.COGNITIO);
                        continue;
                    }
                }

                if (cleanseLength == 4) {
                    spend(DawnMachineResource.MACHINA);
                }

                executeCleanse(secondaryBlocks);
                ticksSinceLastCleanse++;
                break;
            }
        } 
        else {
            ticksSinceLastCleanse++;
        }
    }

    protected void executeCleanse(int secondaryBlocks) {
        if (spendAer) {
            spendAer = false;
            spend(DawnMachineResource.AER);
        }

        for (Object entityObj : getWorldObj().getEntitiesWithinAABB(EntityTaintSporeSwarmer.class, AxisAlignedBB.getBoundingBox(lastCleanseX, 0, lastCleanseZ, lastCleanseX+2, 255, lastCleanseZ+2))) {
            Entity entity = (Entity)entityObj;
            if (haveEnoughFor(DawnMachineResource.IGNIS)) {
                spend(DawnMachineResource.IGNIS);

                if (haveEnoughFor(DawnMachineResource.VACUOS))
                    spend(DawnMachineResource.VACUOS);
                else
                    getWorldObj().setBlock((int)entity.posX, (int)entity.posY, (int)entity.posZ, ConfigBlocks.blockFluxGoo, ((BlockFluxGoo)ConfigBlocks.blockFluxGoo).getQuanta(), 3);
                entity.setDead();
            }
        }

        for (Object entityObj : getWorldObj().getEntitiesWithinAABB(EntityFallingTaint.class, AxisAlignedBB.getBoundingBox(lastCleanseX, 0, lastCleanseZ, lastCleanseX+2, 255, lastCleanseZ+2))) {
            if (haveEnoughFor(DawnMachineResource.IGNIS)) {
                Entity entity = (Entity)entityObj;
                spend(DawnMachineResource.IGNIS);

                if (haveEnoughFor(DawnMachineResource.VACUOS))
                    spend(DawnMachineResource.VACUOS);
                else
                    getWorldObj().setBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ, ConfigBlocks.blockFluxGoo, ((BlockFluxGoo)ConfigBlocks.blockFluxGoo).getQuanta(), 3);

                entity.setDead();
            }
        }

        cleanseBiome(secondaryBlocks);

        boolean didUseIgnis = cleanseBlocks();

        if (haveEnoughFor(DawnMachineResource.SANO))
            cleanseMobs();

        BlightbusterNetwork.sendToNearbyPlayers(new SpawnCleanseParticlesPacket(lastCleanseX, lastCleanseZ, didUseIgnis, true), worldObj.provider.dimensionId, lastCleanseX, 128.0f, lastCleanseZ, 150);
    }

    protected boolean hasAnythingToCleanseHere(int secondaryBlocks) {
        //Can cleanse biome?
        for (int z = -1; z <= 2; z++) {
            for (int x = -1; x <= 2; x++) {
                if (z < 0 && (secondaryBlocks & 0x4) == 0)
                    continue;
                if (z > 1 && (secondaryBlocks & 0x8) == 0)
                    continue;
                if (x < 0 && (secondaryBlocks & 0x1) == 0)
                    continue;
                if (x > 1 && (secondaryBlocks & 0x2) == 0)
                    continue;

                BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(lastCleanseX+x, lastCleanseZ+z);
                if (biome.biomeID == Config.biomeTaintID ||
                        biome.biomeID == Config.biomeEerieID ||
                        biome.biomeID == Config.biomeMagicalForestID)
                    return true;
            }
        }

        if (haveEnoughFor(DawnMachineResource.SANO)) {
            if (getWorldObj().getEntitiesWithinAABB(ITaintedMob.class, AxisAlignedBB.getBoundingBox(lastCleanseX, 0, lastCleanseZ, lastCleanseX+2, 255, lastCleanseZ+2)).size() > 0)
                return true;
        }

        for (int z = 0; z <= 1; z++) {
            for (int x = 0; x <= 1; x++) {
                int herbaTopBlock = -1;
                boolean canHerba = haveEnoughFor(DawnMachineResource.HERBA);
                if (canHerba) {
                    herbaTopBlock = getWorldObj().getTopSolidOrLiquidBlock(lastCleanseX+x, lastCleanseZ+z);
                }

                boolean canIgnis = haveEnoughFor(DawnMachineResource.IGNIS);
                boolean canVacuos = haveEnoughFor(DawnMachineResource.VACUOS);
                boolean canAura = haveEnoughFor(DawnMachineResource.AURAM);
                if (canIgnis || canVacuos || canHerba || canAura) {
                    //Are there any taint blocks to cleanse?
                    for (int i = 0; i < 256; i++) {
                        Block block = getWorldObj().getBlock(lastCleanseX+x, i, lastCleanseZ+z);
                        int meta = getWorldObj().getBlockMetadata(lastCleanseX+x, i, lastCleanseZ+z);

                        if (canIgnis && block == ConfigBlocks.blockTaintFibres)
                            return true;
                        if (canIgnis && block == ConfigBlocks.blockTaint && meta != 2)
                            return true;
                        if (canVacuos && block == ConfigBlocks.blockFluxGoo)
                            return true;
                        if (canHerba && i == herbaTopBlock && block == Blocks.dirt)
                            return true;
                        if (canAura && block == ConfigBlocks.blockAiry) {
                            if (meta == 0) {
                                TileNode node = (TileNode)getWorldObj().getTileEntity(lastCleanseX+x, i, lastCleanseZ+z);
                                if (node != null) {
                                    if (node.getNodeType() == NodeType.TAINTED) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected void cleanseBiome(int secondaryBlocks) {
        boolean canVacuos = haveEnoughFor(DawnMachineResource.VACUOS);
        for (int z = -1; z <= 2; z++) {
            for (int x = -1; x <= 2; x++) {
                if (z < 0 && (secondaryBlocks & 0x4) == 0)
                    continue;
                if (z > 1 && (secondaryBlocks & 0x8) == 0)
                    continue;
                if (x < 0 && (secondaryBlocks & 0x1) == 0)
                    continue;
                if (x > 1 && (secondaryBlocks & 0x2) == 0)
                    continue;

                BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(lastCleanseX+x, lastCleanseZ+z);
                if (biome.biomeID == Config.biomeTaintID ||
                        biome.biomeID == Config.biomeEerieID ||
                        biome.biomeID == Config.biomeMagicalForestID) {

                    BiomeGenBase[] genBiomes = null;
                    genBiomes = getWorldObj().getWorldChunkManager().loadBlockGeneratorData(genBiomes, lastCleanseX+x, lastCleanseZ+z, 1, 1);
                    if (genBiomes != null && genBiomes.length > 0 && genBiomes[0] != null) {
                    	
                    	if (genBiomes[0].biomeID == Config.biomeEerieID && getWorldObj().getBiomeGenForCoords(x, z).biomeID == Config.biomeEerieID);
                    	else if (genBiomes[0].biomeID == Config.biomeMagicalForestID && getWorldObj().getBiomeGenForCoords(x, z).biomeID == Config.biomeMagicalForestID);
                    	else {
                    		BlightbusterNetwork.setBiomeAt(getWorldObj(), lastCleanseX+x, lastCleanseZ+z, genBiomes[0]);
                    	}
                    }
                }

                if (!canVacuos)
                    continue;

                if (x < 0 || z < 0 || x > 1 || z > 1) {
                    for (int y = 0; y < 255; y++) {
                        Block block = getWorldObj().getBlock(lastCleanseX+x, y, lastCleanseZ+z);
                        if (block == ConfigBlocks.blockFluxGoo) {
                            getWorldObj().setBlock(lastCleanseX+x, y, lastCleanseZ+z, Blocks.air);
                            spend(DawnMachineResource.VACUOS);
                            canVacuos = haveEnoughFor(DawnMachineResource.VACUOS);

                            if (!canVacuos)
                                break;
                        }
                    }
                }
            }
        }
    }

    protected boolean cleanseBlocks() {
        boolean haveUsedIgnis = false;

        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
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
                    Block block = getWorldObj().getBlock(lastCleanseX+x, y, lastCleanseZ+z);
                    int meta = getWorldObj().getBlockMetadata(lastCleanseX+x, y, lastCleanseZ+z);

                    boolean thisIsCrustedTaint = (block == ConfigBlocks.blockTaint && meta == 0);

                    if (thisIsCrustedTaint && haveEnoughFor(DawnMachineResource.IGNIS) && haveEnoughFor(DawnMachineResource.VACUOS))
                        columnCrustedTaint++;

                    if (!foundTopBlock && (canHerba || canArbor) && block.isOpaqueCube()) {
                        foundTopBlock = true;
                        topBlock = y;
                    }

                    boolean didUseIgnis = cleanseSingleBlock(lastCleanseX+x, y, lastCleanseZ+z, block, meta, canHerba && foundTopBlock && y == topBlock);
                    haveUsedIgnis = didUseIgnis || haveUsedIgnis;

                    if (didUseIgnis && getWorldObj().getBlock(lastCleanseX+x, y, lastCleanseZ+z) != Blocks.dirt)
                        foundTopBlock = false;
                }

                if (columnCrustedTaint >= 3 && foundTopBlock) {
                    BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(lastCleanseX+x, lastCleanseZ+z);
                    String biomeName = biome.biomeName.toLowerCase(Locale.ENGLISH);

                    //Default to oak
                    int treeType = 0;
                    if (biomeName.contains("jungle"))
                        treeType = 3; //Jungle tree
                    else if (biomeName.contains("taiga") || biomeName.contains("tundra"))
                        treeType = 2; //Spruce trees
                    else if (biomeName.contains("birch"))
                        treeType = 1; //Birch trees
                    else if (biomeName.contains("savanna"))
                        treeType = 4; //Acacia trees
                    else if (biomeName.contains("roof"))
                        treeType = 5;

                    getWorldObj().setBlock(lastCleanseX+x, topBlock + 1, lastCleanseZ+z, Blocks.sapling, treeType, 3);
                    spend(DawnMachineResource.ARBOR);
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
                replaceBlock = Blocks.grass;
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

    protected void cleanseMobs() {
        List entities = getWorldObj().getEntitiesWithinAABB(ITaintedMob.class, AxisAlignedBB.getBoundingBox(lastCleanseX, 0, lastCleanseZ, lastCleanseX + 2, 256, lastCleanseZ + 2));

        for (Object entityObj : entities) {
            if (entityObj instanceof EntityTaintSheep)
            {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntitySheep(getWorldObj()));
            } else if (entityObj instanceof EntityTaintChicken) {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntityChicken(getWorldObj()));
            } else if (entityObj instanceof EntityTaintCow) {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntityCow(getWorldObj()));
            } else if (entityObj instanceof EntityTaintPig) {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntityPig(getWorldObj()));
            } else if (entityObj instanceof EntityTaintVillager) {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntityVillager(getWorldObj()));
            } else if (entityObj instanceof EntityTaintCreeper) {
                spend(DawnMachineResource.SANO);
                cleanseSingleMob((Entity)entityObj, new EntityCreeper(getWorldObj()));
            }

            if (!haveEnoughFor(DawnMachineResource.SANO))
                return;
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
        int energyCost = resource.getEnergyCost();

        boolean halfEssentia = false;
        if (currentRf >= energyCost)
            halfEssentia = true;

        int cost = resource.getAspectCost();
        if (halfEssentia)
            cost /= 2;

        return internalAspectList.getAmount(resource.getAspect()) >= cost;
    }

    public void spend(DawnMachineResource resource) {
        if (!haveEnoughFor(resource))
            return;

        int energyCost = resource.getEnergyCost();
        boolean halfEssentia = false;
        if (currentRf >= energyCost) {
            halfEssentia = true;
            currentRf -= energyCost;
        }

        int cost = resource.getAspectCost();
        if (halfEssentia)
            cost /= 2;

        internalAspectList.remove(resource.getAspect(), cost);
        signalUpdate();
    }

    private void setUpAerRange() {
        boolean aerChunkLoadingActive = BlightBuster.instance.chunkLoader.getAerStatus(getWorldObj(), xCoord, yCoord, zCoord);
        boolean canAffordAer = haveEnoughFor(DawnMachineResource.AER);

        if (canAffordAer != this.aerIsActive) {
            if (!canAffordAer || aerCooldownRemaining <= 0) {
                this.aerIsActive = canAffordAer;

                if (!canAffordAer)
                    this.aerCooldownRemaining = AER_COOLDOWN;
            }
        }

        if (this.aerIsActive != aerChunkLoadingActive) {
            BlightBuster.instance.chunkLoader.changeAerStatus(getWorldObj(), xCoord, yCoord, zCoord, this.aerIsActive);
        }
    }

    private int getNewCleanseCoords() {
        int chunkX = xCoord / 16;
        int chunkZ = zCoord / 16;
        int xInChunk = xCoord % 16;
        int zInChunk = zCoord % 16;

        int minChunkX = chunkX - 1;
        int minChunkZ = chunkZ - 1;
        int maxChunkX = chunkX + 2;
        int maxChunkZ = chunkZ + 2;

        int freeMinChunkX = minChunkX;
        int freeMinChunkZ = minChunkZ;
        int freeMaxChunkX = maxChunkX;
        int freeMaxChunkZ = maxChunkZ;

        if (haveEnoughFor(DawnMachineResource.AER)) {
            minChunkX -= 2;
            minChunkZ -= 2;
            maxChunkX += 2;
            maxChunkZ += 2;
        }

        if (xInChunk < 8) {
            minChunkX--;
            maxChunkX--;
        }

        if (zInChunk < 8) {
            minChunkZ--;
            maxChunkZ--;
        }

        int minBlockX = minChunkX * 16;
        int minBlockZ = minChunkZ * 16;
        int maxBlockX = maxChunkX * 16 + 15;
        int maxBlockZ = maxChunkZ * 16 + 15;

        if (haveEnoughFor(DawnMachineResource.ORDO)) {
            spend(DawnMachineResource.ORDO);
            generateScanlineCoords(minBlockX, minBlockZ, maxBlockX, maxBlockZ);
        } else
            generateRandomCoords(minBlockX, minBlockZ, maxBlockX, maxBlockZ);

        int cleanseChunkX = lastCleanseX / 16;
        int cleanseChunkZ = lastCleanseZ / 16;

        if (cleanseChunkX < freeMinChunkX || cleanseChunkZ < freeMinChunkZ || cleanseChunkX > freeMaxChunkX || cleanseChunkZ > freeMaxChunkZ)
            spendAer = true;

        int secondaryBlocks = 0xF;
        if (lastCleanseX == minBlockX)
            secondaryBlocks &= 0xE;
        else if (lastCleanseX == maxBlockX)
            secondaryBlocks &= 0xD;

        if (lastCleanseZ == minBlockZ)
            secondaryBlocks &= 0xB;
        else if (lastCleanseZ == maxBlockZ)
            secondaryBlocks &= 0x7;

        return secondaryBlocks;
    }

    private void generateScanlineCoords(int minX, int minZ, int maxX, int maxZ) {
        if (lastCleanseX == Integer.MAX_VALUE || lastCleanseX < minX)
            lastCleanseX = minX-2;
        if (lastCleanseZ == Integer.MAX_VALUE || lastCleanseZ < minZ)
            lastCleanseZ = minZ;

        lastCleanseX -= (lastCleanseX % 2);
        lastCleanseZ -= (lastCleanseZ % 2);

        lastCleanseX += 2;

        if (lastCleanseX > maxX) {
            lastCleanseX = minX;
            lastCleanseZ += 2;
        }

        if (lastCleanseZ > maxZ) {
            lastCleanseZ = minZ;
        }
    }

    private void generateRandomCoords(int minX, int minZ, int maxX, int maxZ) {
        int diffX = maxX - minX + 1;
        int diffZ = maxZ - minZ + 1;

        lastCleanseX = getWorldObj().rand.nextInt(diffX) + minX;
        lastCleanseZ = getWorldObj().rand.nextInt(diffZ) + minZ;

        lastCleanseX -= (lastCleanseX % 2);
        lastCleanseZ -= (lastCleanseZ % 2);
    }

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

        if (relevantResource == null)
            return i;

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
        //This contianer is input-only, we're working here!
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        if (i == 0)
            return true;

        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null)
            return false;

        int currentValue = internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();

        return (currentValue >= i);
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        boolean successful = true;
        for (Aspect aspect : aspectList.getAspects())
            successful = doesContainerContainAmount(aspect, aspectList.getAmount(aspect)) && successful;

        return successful;
    }

    @Override
    public int containerContains(Aspect aspect) {
        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null)
            return 0;

        return internalAspectList.getAmount(aspect) / relevantResource.getValueMultiplier();
    }

    public boolean needsMore(Aspect aspect) {
        DawnMachineResource relevantResource = DawnMachineResource.getResourceFromAspect(aspect);

        if (relevantResource == null)
            return false;

        return (relevantResource.getMaximumValue() - internalAspectList.getAmount(aspect)) >= relevantResource.getValueMultiplier();
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (from != ForgeDirection.DOWN)
            return 0;

        int room = MAX_RF - currentRf;

        int actualReceive = Math.min(maxReceive, room);

        if (!simulate)
            currentRf += actualReceive;

        signalUpdate();

        return actualReceive;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (from != ForgeDirection.DOWN)
            return 0;

        return currentRf;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (from != ForgeDirection.DOWN)
            return 0;

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
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return currentRf;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_RF;
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

    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeCustomNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, -999, nbttagcompound);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readCustomNBT(pkt.func_148857_g());
    }

    public Vec3 getGlowColor(double partialTicks) {
        if (currentRf >= FULLGREEN_RF)
            return COLOR_GREEN;
        else if (currentRf >= FULLYELLOW_RF) {
            double progress = (double)(currentRf - FULLYELLOW_RF) / (double)(FULLGREEN_RF - FULLYELLOW_RF);
            return interpColor(COLOR_GREEN, COLOR_YELLOW, progress);
        } else if (currentRf >= FULLRED_RF) {
            double progress = (double)(currentRf - FULLRED_RF) / (double)(FULLYELLOW_RF - FULLRED_RF);
            return interpColor(COLOR_YELLOW, COLOR_RED, progress);
        } else if (currentRf > DEAD_RF)
            return COLOR_RED;
        else
            return null;
    }

    protected Vec3 interpColor(Vec3 left, Vec3 right, double progress) {
        double red = (left.xCoord - right.xCoord) * progress;
        double green = (left.yCoord - right.yCoord) * progress;
        double blue = (left.zCoord - right.zCoord) * progress;

        return Vec3.createVectorHelper(right.xCoord + red, right.yCoord + green, right.zCoord + blue);
    }

	public void setChargerPaired(boolean paired) {
		chargerPaired = paired;
	}
}
