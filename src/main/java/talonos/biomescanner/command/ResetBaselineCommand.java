package talonos.biomescanner.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import talonos.biomescanner.map.MapScanner;

public class ResetBaselineCommand implements ICommand {

    private List aliases = new ArrayList(2);

    public ResetBaselineCommand() {
        aliases.add("blightfallResetBaseline");
        aliases.add("blightfallRB");
    }

    @Override
    public String getCommandName() {
        return "blightfallResetBaseline";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "blightfallResetBaseline";
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        MapScanner.instance.getRegionMap()
            .resetBaseline();
        MapScanner.instance.activate();
        sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("command.resetBaseline.success")));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) return true;
        else return sender.canCommandSenderUseCommand(2, "");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
