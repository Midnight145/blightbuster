package talonos.blightbuster.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.entities.EntitySilverPotion;

public class ItemSilverPotion extends Item {

    public ItemSilverPotion() {
        setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.silverPotionName);
        GameRegistry.registerItem(this, BBStrings.silverPotionName);
        setCreativeTab(CreativeTabs.tabMaterials);
        setTextureName(BlightBuster.MODID + ":" + BBStrings.silverPotionName);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if (!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote) {
            par2World.spawnEntityInWorld(new EntitySilverPotion(par2World, par3EntityPlayer));
        }

        return par1ItemStack;
    }
}
