package talonos.blightbuster.items;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.rwtema.extrautils.item.ItemAngelRing;
import com.rwtema.extrautils.network.NetworkHandler;
import com.rwtema.extrautils.network.packets.PacketAngelRingNotifier;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.items.interfaces.ArmourUpgrade;
import WayofTime.alchemicalWizardry.common.items.armour.BoundArmour;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;

public class ItemBoundRing extends ItemAngelRing implements ArmourUpgrade {

    public static HashSet<String> flyingPlayers = new HashSet<String>();
    public int wingType = -1;
    boolean sentPacket = false;

    public ItemBoundRing() {
        this.setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.boundRingName);
        GameRegistry.registerItem(this, BBStrings.boundRingName);
        this.setCreativeTab(AlchemicalWizardry.tabBloodMagic);
        this.setTextureName(BlightBuster.MODID + ":" + BBStrings.boundRingName);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onArmourUpdate(World world, EntityPlayer player, ItemStack thisItemStack) {
        if (!world.isRemote) {
            if (!this.sentPacket) {
                this.sendPacket(player.getDisplayName(), this.wingType);
            }
        }
        if (world.isRemote) {
            if (this.wingType == -1) {
                this.wingType = thisItemStack.getItemDamage();
            }
            if (!(flyingPlayers.contains(player.getDisplayName())) && this.shouldFly(player)) {

                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
                flyingPlayers.add(player.getDisplayName());
            }
        }
    }

    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
        super.onUpdate(itemstack, world, entity, slot, par5);
        if (this.wingType == -1) {
            this.wingType = itemstack.getItemDamage();
        }
    }

    @Override
    public boolean isUpgrade() {
        return true;
    }

    @Override
    public int getEnergyForTenSeconds() {
        return 1000;
    }

    @SubscribeEvent
    public void canFly(LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote) {
            return;
        }
        if (!(event.entityLiving instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
        if (this.shouldFly(player)) {
            if (!(flyingPlayers.contains(player.getDisplayName()))) {
                flyingPlayers.add(player.getDisplayName());
            }
            if (!this.sentPacket) {
                this.sendPacket(player.getDisplayName(), this.wingType);
            }

            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        } else {
            if (flyingPlayers.contains(player.getDisplayName())) {
                flyingPlayers.remove(player.getDisplayName());
                if (!this.sentPacket) {
                    this.sendPacket(player.getDisplayName(), 0);
                }
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            }
        }
    }

    public boolean shouldFly(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.getCurrentArmor(i);
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item != null && item instanceof BoundArmour) {
                BoundArmour armor = (BoundArmour) item;
                for (ItemStack is : armor.getInternalInventory(stack)) {
                    if (is != null && is.getItem() instanceof ItemBoundRing) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendPacket(String playerName, int type) {
        NetworkHandler.sendToAllPlayers(new PacketAngelRingNotifier(playerName, type));
    }
}
