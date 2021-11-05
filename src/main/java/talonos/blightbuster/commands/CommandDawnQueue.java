package talonos.blightbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;

public class CommandDawnQueue extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "queue";
	}

	@Override
	public String getCommandUsage(ICommandSender paramICommandSender) {
		// TODO Auto-generated method stub
		return "/queue";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] paramArrayOfString) {
		if (DawnMachineTileEntity.INSTANCE == null) {
			sender.addChatMessage(new ChatComponentText("Error: Dawn Machine not found."));
			return;
		}
		// TODO Auto-generated method stub
		sender.addChatMessage(new ChatComponentText("Currently cleaning chunk " + DawnMachineTileEntity.INSTANCE.chunkX + " " + DawnMachineTileEntity.INSTANCE.chunkZ));		
	}
}
