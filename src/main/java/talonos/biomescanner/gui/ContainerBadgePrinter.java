package talonos.biomescanner.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import talonos.biomescanner.BSItems;
import talonos.biomescanner.tileentity.TileEntityIslandScanner;

public class ContainerBadgePrinter extends Container {

    private TileEntityIslandScanner tileEntity;

    public ContainerBadgePrinter(InventoryPlayer inventoryPlayer, TileEntityIslandScanner scanner) {
        this.tileEntity = scanner;

        for (int j = 0; j < 6; ++j) {
            for (int k = 0; k < 12; ++k) {
                this.addSlotToContainer(new BadgeSlot(scanner, k + j * 12, 8 + k * 18, 18 + j * 18));
            }
        }

        this.addSlotToContainer(new BadgeSlot(scanner, 72, 8, 176));
        this.addSlotToContainer(new BadgeSlot(scanner, 73, 8, 149));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + j * 9 + 9, 34 + k * 18, 103 + j * 18 + 36));
            }
        }

        for (int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inventoryPlayer, j, 34 + j * 18, 161 + 36));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        Slot slot = (Slot) inventorySlots.get(slotIndex);

        if (slot == null || !slot.getHasStack()) return null;

        if (slotIndex < tileEntity.getSizeInventory()) {
            ItemStack currentStack = slot.getStack();

            if (!this
                .mergeItemStack(currentStack, tileEntity.getSizeInventory(), 36 + tileEntity.getSizeInventory(), true))
                return null;

            slot.putStack(new ItemStack(BSItems.badge, 1, slotIndex));
            slot.onPickupFromSlot(player, currentStack);
        }

        return null;
    }
}
