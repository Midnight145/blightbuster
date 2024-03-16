package talonos.cavestokingdoms.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryRegistry;

public class ItemOreManual extends Item {

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());

        OreDiscoveryRegistry.getInstance()
            .scanPlayerForDiscoveries(player);

        OreDiscoveryRegistry.getInstance()
            .copyDiscoveries(player.getEntityData(), stack.getTagCompound());
        return super.onItemRightClick(stack, world, player);
    }
}
