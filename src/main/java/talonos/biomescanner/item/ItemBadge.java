package talonos.biomescanner.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.map.Zone;

public class ItemBadge extends Item {

    private IIcon[] zoneIcons = new IIcon[Zone.values().length * 3];
    private IIcon completionIcon;
    private IIcon beginnerIcon;

    private IIcon zoneSilhouette;
    private IIcon beginnerSilhouette;
    private IIcon completionSilhouette;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        completionIcon = iconRegister.registerIcon("biomescanner:completionBadge");
        beginnerIcon = iconRegister.registerIcon("biomescanner:beginnerBadge");
        zoneSilhouette = iconRegister.registerIcon("biomescanner:zoneBadgeSilhouette");
        beginnerSilhouette = iconRegister.registerIcon("biomescanner:beginnerSilhouette");
        completionSilhouette = iconRegister.registerIcon("biomescanner:completionSilhouette");

        for (Zone zone : Zone.values()) {
            zoneIcons[zone.ordinal() * 3] = iconRegister
                .registerIcon("biomescanner:zoneBadge-" + zone.toString() + "-bronze");
            zoneIcons[zone.ordinal() * 3 + 1] = iconRegister
                .registerIcon("biomescanner:zoneBadge-" + zone.toString() + "-silver");
            zoneIcons[zone.ordinal() * 3 + 2] = iconRegister
                .registerIcon("biomescanner:zoneBadge-" + zone.toString() + "-gold");
        }
    }

    public IIcon getZoneSilhouette() {
        return zoneSilhouette;
    }

    public IIcon getCompletionSilhouette() {
        return completionSilhouette;
    }

    public IIcon getBeginnerSilhouette() {
        return beginnerSilhouette;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        // The total completion badge should have one of them fancy enchantment effects
        // on it to show how fancy it is
        return pass == 0 && zoneIcons.length == stack.getItemDamage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < zoneIcons.length) return zoneIcons[damage];
        else if (zoneIcons.length == damage) return completionIcon;
        else return beginnerIcon;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage == zoneIcons.length) return StatCollector.translateToLocal("item.completionBadge.name");
        if (damage > zoneIcons.length) return StatCollector.translateToLocal("item.beginnerBadge.name");

        int medalType = damage % 3;

        String zoneName = StatCollector.translateToLocal(Zone.values()[damage / 3].getDisplay());
        String medalDisplay;
        switch (medalType) {
            case 0:
                medalDisplay = StatCollector.translateToLocal("item.zoneBronze.name");
                break;
            case 1:
                medalDisplay = StatCollector.translateToLocal("item.zoneSilver.name");
                break;
            case 2:
            default:
                medalDisplay = StatCollector.translateToLocal("item.zoneGold.name");
        }

        return medalDisplay.replace("{0}", zoneName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean advanced) {
        int damage = itemStack.getItemDamage();
        if (damage < zoneIcons.length) {
            String zoneName = StatCollector.translateToLocal(Zone.values()[damage / 3].getDisplay());
            int medalType = damage % 3;
            switch (medalType) {
                case 0:
                    addBadgeData("gui.bronzeInfo", zoneName, info);
                    break;
                case 1:
                    addBadgeData("gui.silverInfo", zoneName, info);
                    break;
                case 2:
                    addBadgeData("gui.goldInfo", zoneName, info);
                    break;
            }
            return;
        }

        if (damage == zoneIcons.length) {
            addBadgeData("gui.completionInfo", "", info);
            return;
        }

        addBadgeData("gui.beginnerInfo", "", info);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < zoneIcons.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
        list.add(new ItemStack(this, 1, zoneIcons.length));
        list.add(new ItemStack(this, 1, zoneIcons.length + 1));
    }

    private void addBadgeData(String value, String zoneName, List info) {
        for (int i = 0; i < 99; i++) {
            if (!StatCollector.canTranslate(value + Integer.toString(i))) break;

            info.add(
                "\u00a79" + StatCollector.translateToLocal(value + Integer.toString(i))
                    .replace("{0}", zoneName));
        }
    }
}
