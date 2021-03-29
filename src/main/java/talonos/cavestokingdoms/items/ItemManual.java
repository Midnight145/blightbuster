package talonos.cavestokingdoms.items;

import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.Mantle;
import mantle.books.BookData;
import mantle.client.gui.GuiManual;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import talonos.cavestokingdoms.CavesToKindgoms;
import talonos.cavestokingdoms.lib.DEFS;

public class ItemManual extends ItemOreManual
{
	String name = "basicManual";
	
	private static final int NUMBER_OF_MANUALS = 12;

    public ItemManual()
    {
        super();
		setUnlocalizedName(DEFS.MODID + ":" + name );
		GameRegistry.registerItem(this, name);
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName(DEFS.MODID + ":" + name);
		setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        super.onItemRightClick(stack, world, player);
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        player.openGui(Mantle.instance, mantle.client.MProxyClient.manualGuiID, world, 0, 0, 0);
        FMLClientHandler.instance().displayGuiScreen(player, new GuiManual(stack, getData(stack)));
        return stack;
    }

    private BookData getData (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return CavesToKindgoms.manualInfo.mats0;
        case 1:
            return CavesToKindgoms.manualInfo.mats1;
        case 2:
            return CavesToKindgoms.manualInfo.mats2;
        case 3:
            return CavesToKindgoms.manualInfo.mats3;
        case 4:
            return CavesToKindgoms.manualInfo.mats4;
        case 5:
            return CavesToKindgoms.manualInfo.mats5;
        case 6:
            return CavesToKindgoms.manualInfo.ben3;
        case 7:
            return CavesToKindgoms.manualInfo.ben4;
        case 8:
            return CavesToKindgoms.manualInfo.ben1;
        case 9:
            return CavesToKindgoms.manualInfo.ben2;
        case 10:
            return CavesToKindgoms.manualInfo.taint1;
        case 11:
            return CavesToKindgoms.manualInfo.taint2;
        default:
        	return CavesToKindgoms.manualInfo.mats1;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            list.add("\u00a7o" + StatCollector.translateToLocal("blightfallmanual.geology"));
            break;
            case 6:
            case 7:
            case 8:
            case 9:
            list.add("\u00a7o" + StatCollector.translateToLocal("blightfallmanual.ben"));
            break;
        case 10:
        case 11:
            list.add("\u00a7o" + StatCollector.translateToLocal("blightfallmanual.taint"));
            break;
        }
    }
    
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int damage = 0; damage < NUMBER_OF_MANUALS; damage++)
        {
            list.add(new ItemStack(b, 1, damage));
        }
    }
    
    IIcon[] icons;
    
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
		return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[NUMBER_OF_MANUALS];
        for (int i = 0; i < NUMBER_OF_MANUALS; i++)
        {
            this.icons[i] = iconRegister.registerIcon("cavestokingdoms:manual"+i);
        }
    }

    public String getUnlocalizedName (ItemStack stack)
    {
        int meta = stack.getItemDamage();
        return getUnlocalizedName() + "." + meta;
    }
}