package talonos.blightbuster.handlers;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraftforge.common.MinecraftForge;
import squeek.applecore.api.food.FoodEvent.GetFoodValues;
import squeek.applecore.api.food.FoodValues;

public class FoodHandler {

	public FoodHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		// remove hunger effect from rotten flesh
		((ItemFood) Items.rotten_flesh).setPotionEffect(0, 0, 0, 0.0F);
	}

	@SubscribeEvent
	public void onFoodEaten(GetFoodValues event) {
		if (event.food.getItem() == Items.rotten_flesh) { event.foodValues = new FoodValues(0, 0); }
	}
}
