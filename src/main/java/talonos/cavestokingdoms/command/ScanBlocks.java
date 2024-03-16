package talonos.cavestokingdoms.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ScanBlocks extends CommandBase {

    @Override
    public String getCommandName() {
        return "scanBlocks";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/scanBlocks";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        p_71515_1_.addChatMessage(new ChatComponentText("Beginning Scan"));
        CommandBlockScanner scanner = new CommandBlockScanner(p_71515_1_);
    }

    public class CommandBlockScanner {

        private ICommandSender commandSender;
        private int x = 0;
        private int z = 0;
        private double total = 0;
        private HashMap<String, Integer> blockData = new HashMap<String, Integer>();

        public CommandBlockScanner(ICommandSender commandSender) {
            this.commandSender = commandSender;
            FMLCommonHandler.instance()
                .bus()
                .register(this);
        }

        @SubscribeEvent
        public void tickHandler(TickEvent.WorldTickEvent tickEvent) {
            if (tickEvent.phase != TickEvent.Phase.START) return;

            if (x == 110 * 16) {
                x = 0;
                z++;
            }

            if (z == 135 * 16) {
                writeValues();
                return;
            }

            scanRow();
        }

        private void scanRow() {
            World world = commandSender.getEntityWorld();
            for (int i = 0; i < 160; i++) {
                for (int y = 0; y < 255; y++) {
                    if (world.isAirBlock(x, y, z)) continue;

                    total += 1.0;
                    Block block = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);
                    String blockName = toBlockString(new ItemStack(block, 1, meta));
                    int blockCount = 0;
                    if (blockData.containsKey(blockName)) blockCount = blockData.get(blockName);
                    blockCount++;
                    blockData.put(blockName, blockCount);
                }
                x++;
            }
        }

        public String toBlockString(ItemStack stack) {
            StringBuilder result = new StringBuilder();
            result.append('<');
            result.append(Item.itemRegistry.getNameForObject(stack.getItem()));

            if (stack.getItemDamage() > 0) {
                result.append(':')
                    .append(stack.getItemDamage());
            }
            result.append('>');

            return result.toString();
        }

        private void writeValues() {
            try {
                FileOutputStream streamOut = FileUtils.openOutputStream(new File("scanOut.csv"));

                OutputStreamWriter writer = new OutputStreamWriter(streamOut);
                writer.write("block,count,percent" + System.getProperty("line.separator"));
                for (String key : blockData.keySet()) {
                    int count = blockData.get(key);

                    double percent = ((double) count / total) * 100.0;
                    writer.write(
                        key + ","
                            + Integer.toString(count)
                            + ","
                            + Double.toString(percent)
                            + System.getProperty("line.separator"));
                }

                writer.close();
                streamOut.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            FMLCommonHandler.instance()
                .bus()
                .unregister(this);
            commandSender.addChatMessage(new ChatComponentText("Scan complete"));
        }
    }
}
