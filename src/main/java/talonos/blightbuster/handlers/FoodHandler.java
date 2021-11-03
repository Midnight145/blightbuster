package talonos.blightbuster.handlers;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import squeek.applecore.api.food.FoodEvent.GetFoodValues;
import squeek.applecore.api.food.FoodValues;

public class FoodHandler {
	
	public FoodHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onFoodEaten(GetFoodValues event) {
		if (event.food.getItem() == Items.rotten_flesh) {
			event.foodValues = new FoodValues(0, 0);
		}
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(PlayerTickEvent event) {
		if (event.player.isPotionActive(Potion.hunger)) {
			if (event.player.inventory.getCurrentItem().getItem() == Items.rotten_flesh) {
				event.player.removePotionEffect(Potion.hunger.id);
			}
		}
	}
}
