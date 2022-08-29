package talonos.blightbuster.tileentity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import talonos.blightbuster.BlightBuster;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;

public class NodeResetterTileEntity extends TileEntity {
	Ticket chunkTicket;
	boolean isInvalid = false;
	boolean hasInitializedChunkloading = false;
	int index = 0;
	private boolean waiting = false;
	ArrayList<NodeInfo> nodes = new ArrayList<NodeInfo>();

	public NodeResetterTileEntity() {
		File file = new File("jsonified-nodes.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String arr[] = line.split(",");
				NodeInfo info = new NodeInfo(arr);

				this.nodes.add(info);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateEntity() {
		boolean saidDone = false;
		if (this.index >= this.nodes.size()) {
			if (saidDone) { return; }
			System.out.println("Done!");
			saidDone = true;
			return;
		}
		if (this.isInvalid) { return; }
		if (!this.hasInitializedChunkloading) {
			this.initalizeChunkloading();
			return;
		}
		NodeInfo node = this.nodes.get(this.index);
		int[] coords = this.getChunkCoordsFromBlockCoords(node.x, node.z);
		if (!this.isChunkLoaded(coords[0], coords[1])) { this.loadChunk(); }
		if (!this.worldObj.getChunkProvider().chunkExists(coords[0], coords[1])) {
			this.waiting = true;
			return;
		}
		this.waiting = false;

		Chunk chunk = this.worldObj.getChunkFromBlockCoords(node.x, node.z);
		TileEntity tile = this.worldObj.getTileEntity(node.x, node.y, node.z);
		if (tile != null && tile instanceof TileNode) {
			TileNode nodeTile = (TileNode) tile;
			NodeType type = NodeType.values()[node.type];
			if (nodeTile.getNodeType() != type) { nodeTile.setNodeType(type); }
			System.out.println("Node at " + node.x + ", " + node.y + ", " + node.z + " set to type " + type.toString());
		}

		if (!this.waiting) {
			this.unloadChunk();
			this.index++;
		}
	}

	private void initalizeChunkloading() {
		this.chunkTicket = ForgeChunkManager.requestTicket(BlightBuster.instance, this.getWorldObj(), ForgeChunkManager.Type.NORMAL);
		this.chunkTicket.getModData().setString("id", "NodeResetter");
		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(0, 0));
		this.hasInitializedChunkloading = true;
	}

	private void loadChunk() {
		NodeInfo node = this.nodes.get(this.index);
		int[] coords = this.getChunkCoordsFromBlockCoords(node.x, node.z);
		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(coords[0], coords[1]));
	}

	private void unloadChunk() {
		NodeInfo node = this.nodes.get(this.index);
		int[] coords = this.getChunkCoordsFromBlockCoords(node.x, node.z);
		ForgeChunkManager.unforceChunk(this.chunkTicket, new ChunkCoordIntPair(coords[0], coords[1]));
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

	public int[] getChunkCoordsFromBlockCoords(int x, int z) {
		return new int[] { (int) Math.floor(x / 16.0), (int) Math.floor(z / 16.0) };
	}
}

class NodeInfo {
	int x, y, z, mod, type;
	HashMap<String, Integer> aspects = new HashMap<String, Integer>();

	public NodeInfo(String arr[]) {
		this.x = Integer.parseInt(arr[0]);
		this.y = Integer.parseInt(arr[1]);
		this.z = Integer.parseInt(arr[2]);
		this.mod = Integer.parseInt(arr[3]);
		this.type = Integer.parseInt(arr[4]);

		for (int i = 5; i < arr.length - 1; i += 2) {
			this.aspects.put(arr[i], Integer.parseInt(arr[i + 1]));
		}
	}
}