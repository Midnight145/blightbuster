package talonos.blightbuster.tileentity.dawnmachine;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;

public class DawnMachineChunkLoader implements ForgeChunkManager.LoadingCallback {

	DawnMachineTileEntity dawnMachine;

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
		for (ForgeChunkManager.Ticket ticket : tickets) {
			NBTTagCompound data = ticket.getModData();
			String id = data.getString("id");
			if (id == "DawnMachine") {
//    			dawnMachine.forceChunkLoading(ticket); 
			}
		}
	}
}
