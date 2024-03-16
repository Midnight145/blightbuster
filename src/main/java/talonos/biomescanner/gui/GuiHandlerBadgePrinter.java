package talonos.biomescanner.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import talonos.biomescanner.tileentity.TileEntityIslandScanner;

public class GuiHandlerBadgePrinter implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityIslandScanner)
            return new ContainerBadgePrinter(player.inventory, (TileEntityIslandScanner) tileEntity);

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityIslandScanner)
            return new GuiBadgePrinter(player.inventory, (TileEntityIslandScanner) tileEntity);

        return null;
    }
}
