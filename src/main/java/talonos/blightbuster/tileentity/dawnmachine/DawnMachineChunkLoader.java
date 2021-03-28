package talonos.blightbuster.tileentity.dawnmachine;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import talonos.blightbuster.BlightBuster;

public class DawnMachineChunkLoader implements ForgeChunkManager.LoadingCallback {

    private List<ForgeChunkManager.Ticket> tickets = new ArrayList<ForgeChunkManager.Ticket>(12);

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            NBTTagCompound data = ticket.getModData();
            int x = data.getInteger("X");
            int z = data.getInteger("Z");
            boolean aerTicket = data.getBoolean("Aer");
            boolean isActive = !aerTicket || data.getBoolean("AerLoaded");
            loadChunks(ticket, world, x, z, aerTicket, isActive);
        }
    }

    public void newDawnMachine(World world, int x, int y, int z) {

        eliminateDawnMachine(world, x, y, z);

        ForgeChunkManager.Ticket normalTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, world, ForgeChunkManager.Type.NORMAL);

        if (normalTicket != null) {
            normalTicket.getModData().setInteger("X", x);
            normalTicket.getModData().setInteger("Y", y);
            normalTicket.getModData().setInteger("Z", z);
            normalTicket.getModData().setBoolean("Aer", false);
            loadChunks(normalTicket, world, x, z, false, true);
        }

        ForgeChunkManager.Ticket aerTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, world, ForgeChunkManager.Type.NORMAL);

        if (aerTicket != null) {
            aerTicket.getModData().setInteger("X", x);
            aerTicket.getModData().setInteger("Y", y);
            aerTicket.getModData().setInteger("Z", z);
            aerTicket.getModData().setBoolean("Aer", true);
            aerTicket.getModData().setBoolean("AerLoaded", false);
            loadChunks(aerTicket, world, x, z, true, false);
        }
    }

    public void eliminateDawnMachine(World world, int x, int y, int z) {
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).world != world)
                continue;

            NBTTagCompound data = tickets.get(i).getModData();

            if (data.getInteger("X") != x || data.getInteger("Y") != y || data.getInteger("Z") != z)
                continue;

            ForgeChunkManager.releaseTicket(tickets.get(i));
            tickets.remove(i);
        }
    }

    public  boolean getAerStatus(World world, int x, int y, int z) {
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).world != world)
                continue;

            NBTTagCompound data = tickets.get(i).getModData();

            if (!data.getBoolean("Aer"))
                continue;

            if (data.getInteger("X") != x || data.getInteger("Y") != y || data.getInteger("Z") != z)
                continue;

            return data.getBoolean("AerLoaded");
        }

        return false;
    }

    public void changeAerStatus(World world, int x, int y, int z, boolean active) {
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).world != world)
                continue;

            NBTTagCompound data = tickets.get(i).getModData();

            if (!data.getBoolean("Aer"))
                continue;

            if (data.getInteger("X") != x || data.getInteger("Y") != y || data.getInteger("Z") != z)
                continue;

            data.setBoolean("AerLoaded", active);

            ForgeChunkManager.Ticket ticket = tickets.get(i);

            if (active)
                loadChunks(ticket, world, x, z, true, true);
            else {
                ImmutableSet<ChunkCoordIntPair> list = ticket.getChunkList();
                for (ChunkCoordIntPair coords : list) {
                    ForgeChunkManager.unforceChunk(ticket, coords);
                }
            }
        }
    }

    protected void loadChunks(ForgeChunkManager.Ticket ticket, World world, int x, int z, boolean aerTicket, boolean isActive) {
        int chunkX = x / 16;
        int chunkZ = z / 16;
        int xInChunk = x % 16;
        int zInChunk = z % 16;

        int minChunkX = chunkX - 3;
        int minChunkZ = chunkZ - 3;

        if (xInChunk < 8)
            minChunkX--;
        if (zInChunk < 8)
            minChunkZ--;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boolean xInCenter = j >= 2 && j <= 5;
                boolean yInCenter = i >= 2 && i <= 5;

                boolean inCenter = xInCenter && yInCenter;

                if (aerTicket != inCenter) {
                    if (isActive)
                        ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(minChunkX+j, minChunkZ+i));
                    else
                        ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(minChunkX+j, minChunkZ+i));
                }
            }
        }

        if (!tickets.contains(ticket))
            tickets.add(ticket);
    }
}
