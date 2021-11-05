package talonos.blightbuster.tileentity.dawnmachine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;

public class DawnMachineChunkLoader implements ForgeChunkManager.LoadingCallback {

    private ForgeChunkManager.Ticket dawnMachineTicket;
    private ForgeChunkManager.Ticket loadedChunkTicket;
    DawnMachineTileEntity dawnMachine;
    private boolean isActive;
    private List<ForgeChunkManager.Ticket> tickets = new ArrayList<ForgeChunkManager.Ticket>(2);
    
    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
    	if (isActive) {
    		loadDawnMachine();
    		loadNextChunk();
    	}
    }

    public void newDawnMachine(DawnMachineTileEntity dawnMachine) {
    	isActive = true;
    	eliminateDawnMachine();
    	System.out.println("Creating new Dawn Machine");
    	this.dawnMachine = dawnMachine;

        dawnMachineTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, dawnMachine.getWorldObj(), ForgeChunkManager.Type.NORMAL);
        loadedChunkTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, dawnMachine.getWorldObj(), ForgeChunkManager.Type.NORMAL);
        System.out.println("dawnMachineTicket: " + dawnMachineTicket);
    }
    public void loadDawnMachine() {
        Chunk chunk = dawnMachine.getWorldObj().getChunkFromBlockCoords(dawnMachine.xCoord, dawnMachine.yCoord);
        System.out.println("Dawn Machine Chunk: " + chunk.xPosition + " " + chunk.zPosition);
        ForgeChunkManager.forceChunk(dawnMachineTicket, new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition));
    }

    public void eliminateDawnMachine() {
    	if (dawnMachineTicket != null) {
    		ForgeChunkManager.releaseTicket(dawnMachineTicket);
    	    
    	}
    }

//    public  boolean getAerStatus(World world, int x, int y, int z) {
//    	ForgeChunkManager.Ticket ticket = dawnMachineTicket;
//        NBTTagCompound data = ticket.getModData();
//
//        return data.getBoolean("AerLoaded");
//    }

//    public void changeAerStatus(World world, int x, int y, int z, boolean active) {
//        
//    	ForgeChunkManager.Ticket ticket = dawnMachineTicket;
//        NBTTagCompound data = ticket.getModData();
//
//        data.setBoolean("AerLoaded", active);
//
//
//        if (active) {
//            loadChunks(ticket, world, x, z, true, true);
//        }
//        else {
//            ImmutableSet<ChunkCoordIntPair> list = ticket.getChunkList();
//            for (ChunkCoordIntPair coords : list) {
//                ForgeChunkManager.unforceChunk(ticket, coords);
//            }
//        }
//    }

    public void loadNextChunk() {
    	ForgeChunkManager.unforceChunk(loadedChunkTicket, new ChunkCoordIntPair(dawnMachine.lastChunkX, dawnMachine.lastChunkZ));
        ForgeChunkManager.forceChunk(loadedChunkTicket, new ChunkCoordIntPair(dawnMachine.chunkX, dawnMachine.chunkZ));
    }
    
}
