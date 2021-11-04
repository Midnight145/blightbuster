package talonos.blightbuster.items;

import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemTunerFocus extends ItemFocusBasic {

	static HashMap<String, String> mode_dict;
	static HashMap<String, String> mode_chat;
	private final String NBT_TAG_MODE = "tunerMode";
	private final String NBT_TAG_CHUNKS = "tunerChunks";
	
	static {
		mode_dict.put("NW", "SE");
		mode_dict.put("SE", "NW");
		
		mode_chat.put("NW", "Set mode to northwest.");
		mode_chat.put("NW", "Set mode to southeast.");
	}
	
	public ItemTunerFocus() {
        setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.tunerFocusName);
        GameRegistry.registerItem(this, BBStrings.tunerFocusName);
        setCreativeTab(Thaumcraft.tabTC);
        setTextureName(BlightBuster.MODID + ":" + BBStrings.purityFocusName);
    }

    private static final AspectList cost = new AspectList().add(Aspect.ORDER, 5);

    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop) {
    	NBTTagCompound orig = itemstack.getTagCompound();
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        
        if (!orig.hasKey(NBT_TAG_MODE)) { setMode("NW", itemstack); }
    	
        if (p.isSneaking()) {
    		changeMode(itemstack);
    		return itemstack;
    	}
        
        // return if not enough vis
        if (!wand.consumeAllVis(itemstack, p, cost, false, false)) { return itemstack; }
        
        // if rightclicked on block
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        	Chunk chunk = p.worldObj.getChunkFromBlockCoords(mop.blockX, mop.blockY);
        	int[] coords = { chunk.xPosition, chunk.zPosition };
        	
        	TileEntity tile = p.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
        	
        	if (tile instanceof DawnMachineTileEntity && orig.hasKey("NW") && orig.hasKey("SE")) {
        		NBTTagCompound chunks = orig.getCompoundTag(NBT_TAG_CHUNKS);
        		handleDawnMachine((DawnMachineTileEntity) tile, chunks);
        	}
        	// used for nicely getting chunk coords

        	
        	NBTTagCompound tag = setCoords(new NBTTagCompound(), coords);
        	itemstack.writeToNBT(tag);
        	p.addChatMessage(new ChatComponentText(mode_chat.get(orig.getString(NBT_TAG_MODE))));
        }
        
        return itemstack;
    }
    
    private void handleDawnMachine(DawnMachineTileEntity tile, NBTTagCompound chunks) {
    	if (!chunks.hasKey("NW")) {
    		System.out.println("Don't have NW selected.");
    		return;
    	}
    	if (!chunks.hasKey("SE")) {
    		System.out.println("Don't have SE selected.");
    		return;
    	}
    	
		int[] nw_coords = chunks.getIntArray("NW");
		int[] se_coords = chunks.getIntArray("SE");
		((DawnMachineTileEntity) tile).NW_chunk = nw_coords;
		((DawnMachineTileEntity) tile).SE_chunk = se_coords;
    }
    
    private NBTTagCompound setCoords(NBTTagCompound tag, int[] coords) {
    	NBTTagCompound array = new NBTTagCompound();
    	tag.setTag(NBT_TAG_CHUNKS, array);
    	
    	array.setIntArray(tag.getString(NBT_TAG_MODE), coords);
    	return tag;
    }
    
    private void setMode(String string, ItemStack itemstack) {
		// TODO Auto-generated method stub
    	NBTTagCompound orig = itemstack.getTagCompound();
    	orig.setString(NBT_TAG_MODE, string);
    	itemstack.writeToNBT(orig);
		
	}
    private void changeMode(ItemStack itemstack) {
    	NBTTagCompound orig = itemstack.getTagCompound();
    	orig.setString(NBT_TAG_MODE, mode_dict.get(orig.getString(NBT_TAG_MODE)));
    	itemstack.writeToNBT(orig);
    }

	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
//        this.itemIcon = register.registerIcon(this.getIconString());
//        this.icon = register.registerIcon(this.getIconString());
    }

    @Override
    public int getFocusColor(ItemStack arg0) {
        return 0x32f8c5;
    }

    @Override
    public AspectList getVisCost(ItemStack arg0) {
        return cost;
    }
    
    @Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		// TODO Auto-generated method stub
    	
    	tooltip.add("Mode: " + (stack.getTagCompound().getString(NBT_TAG_MODE) != null ? stack.getTagCompound().getString(NBT_TAG_MODE) : ""));
		super.addInformation(stack, player, tooltip, par4);
	}

}
