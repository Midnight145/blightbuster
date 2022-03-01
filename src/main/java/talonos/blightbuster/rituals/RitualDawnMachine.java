package talonos.blightbuster.rituals;

import java.util.ArrayList;
import java.util.List;

import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.renderer.AlchemyCircleRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.tiles.TileEtherealBloom;

public class RitualDawnMachine extends RitualEffect {
	DawnMachineTileEntity dawnMachine;
	int[][] bloomOffsets = new int[][] { { 7, 3, 0 }, { -7, 3, 0 }, { 5, 3, -5 }, { 5, 3, 5 }, { 0, 3, 7 },
			{ 0, 3, -7 }, { -5, 3, -5 }, { -5, 3, 5 } };
	boolean going = false;

	@Override
	public boolean startRitual(IMasterRitualStone ritualStone, EntityPlayer player) {

		if (!this.checkBlooms(ritualStone)) {
			player.addChatMessage(new ChatComponentTranslation("gui.ritual.missingBlooms"));
			return false;
		}
		this.going = true;
		return true;
	}

	@Override
	public void performEffect(IMasterRitualStone ritualStone) {
		this.going = this.checkBlooms(ritualStone);
		if (!this.going) {
			return;
		}

		String owner = ritualStone.getOwner();

		int currentEssence = SoulNetworkHandler.getCurrentEssence(owner);
		World world = ritualStone.getWorld();
		int x = ritualStone.getXCoord();
		int y = ritualStone.getYCoord();
		int z = ritualStone.getZCoord();
		if (world.getTileEntity(x, y + 2, z) instanceof DawnMachineTileEntity) {
			this.dawnMachine = (DawnMachineTileEntity) world.getTileEntity(x, y + 2, z);

		}
		else {
			return;
		}

		int bloodToAdd = Math.min(currentEssence, this.getCostPerRefresh());
		int actuallyAdd = this.dawnMachine.addBlood(bloodToAdd, true);
		if (currentEssence < actuallyAdd) {
			SoulNetworkHandler.causeNauseaToPlayer(owner);
			return;
		}

		this.dawnMachine.addBlood(actuallyAdd, false);
		SoulNetworkHandler.syphonFromNetwork(owner, actuallyAdd);
	}

	@Override
	public int getCostPerRefresh() { // TODO Auto-generated method stub
		return 500;
	}

	@Override
	public List<RitualComponent> getRitualComponentList() { // TODO Auto-generated method stub
		ArrayList<RitualComponent> dawnMachineRitual = new ArrayList<RitualComponent>();
		dawnMachineRitual.add(new RitualComponent(1, 0, 0, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(2, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(4, 0, 0, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(5, 1, 0, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(7, 1, 0, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(7, 2, 0, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(0, 0, -1, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(4, 0, -1, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(6, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(0, 0, -2, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(2, 0, -2, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(4, 0, -2, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(3, 1, -3, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(0, 0, -4, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(1, 0, -4, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(2, 0, -4, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(0, 1, -5, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(5, 1, -5, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(5, 2, -5, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(1, 0, -6, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(0, 1, -7, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(0, 2, -7, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(0, 0, 1, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(4, 0, 1, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(6, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(0, 0, 2, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(2, 0, 2, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(4, 0, 2, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(3, 1, 3, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(0, 0, 4, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(1, 0, 4, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(2, 0, 4, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(0, 1, 5, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(5, 1, 5, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(5, 2, 5, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(1, 0, 6, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(0, 1, 7, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(0, 2, 7, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(-1, 0, 0, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(-2, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(-4, 0, 0, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(-5, 1, 0, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(-7, 1, 0, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(-7, 2, 0, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(-4, 0, -1, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(-6, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(-2, 0, -2, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(-4, 0, -2, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-3, 1, -3, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(-1, 0, -4, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(-2, 0, -4, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-5, 1, -5, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-5, 2, -5, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(-1, 0, -6, RitualComponent.EARTH));
		dawnMachineRitual.add(new RitualComponent(-4, 0, 1, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(-6, 0, 0, RitualComponent.WATER));
		dawnMachineRitual.add(new RitualComponent(-2, 0, 2, RitualComponent.AIR));
		dawnMachineRitual.add(new RitualComponent(-4, 0, 2, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-3, 1, 3, RitualComponent.DAWN));
		dawnMachineRitual.add(new RitualComponent(-1, 0, 4, RitualComponent.BLANK));
		dawnMachineRitual.add(new RitualComponent(-2, 0, 4, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-5, 1, 5, RitualComponent.FIRE));
		dawnMachineRitual.add(new RitualComponent(-5, 2, 5, RitualComponent.DUSK));
		dawnMachineRitual.add(new RitualComponent(-1, 0, 6, RitualComponent.EARTH));

		return dawnMachineRitual;
	}

	private boolean checkBloomExists(IMasterRitualStone ritualStone, int[] coords) {

		World world = ritualStone.getWorld();
		int x = ritualStone.getXCoord();
		int y = ritualStone.getYCoord();
		int z = ritualStone.getZCoord();
		TileEntity tile = world.getTileEntity(x + coords[0], y + coords[1], z + coords[2]);
		return tile != null && tile instanceof TileEtherealBloom;
	}

	public static void init() {
		Rituals.registerRitual("BBDawnMachineRitual", 1, 10000, new RitualDawnMachine(), "Ritual of the New Dawn",
				new AlchemyCircleRenderer(
						new ResourceLocation("alchemicalwizardry:textures/models/SimpleTransCircle.png"), 0, 0, 0, 255,
						0, 0.501, 0.501, 0, 1.5, true));
	}

	public boolean checkBlooms(IMasterRitualStone ritualStone) {
		for (int[] offset : this.bloomOffsets) {
			if (!this.checkBloomExists(ritualStone, offset)) {

				return false;
			}
		}
		return true;
	}

}
