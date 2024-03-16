package talonos.cavestokingdoms.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.weaponry.ArrowShaftMaterial;

public class ListShaftMaterials implements ICommand {

    private List aliases = new ArrayList(2);

    public ListShaftMaterials() {
        aliases.add("blightfallListShaftMaterials");
        aliases.add("blightfallLSM");
    }

    @Override
    public String getCommandName() {
        return "blightfallListShaftMaterials";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "blightfallListShaftMaterials";
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
            new ChatComponentText(StatCollector.translateToLocal("command.listshaftmaterials.success")));
        for (CustomMaterial mat : TConstructRegistry.customMaterials) {
            if (mat instanceof ArrowShaftMaterial) {
                ArrowShaftMaterial m = (ArrowShaftMaterial) mat;
                String toPrint = m.input + ", " + m.durabilityModifier + ", " + m.fragility + ", " + m.weight;
                System.out.println(toPrint);
                sender.addChatMessage(new ChatComponentText(toPrint));
            }
        }
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
