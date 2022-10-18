package talonos.blightbuster.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import talonos.blightbuster.blocks.BBBlock;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class DawnMachineSpoutTileEntity extends TileEntity implements IEssentiaTransport, IAspectContainer {

	public DawnMachineSpoutTileEntity() {

	}

	protected DawnMachineTileEntity getController() {
		TileEntity controller = BBBlock.dawnMachineInput.getMultiblockController(getWorldObj(), this.xCoord, this.yCoord, this.zCoord);
		if (controller == null || !(controller instanceof DawnMachineTileEntity))
			return null;

		return (DawnMachineTileEntity) controller;
	}

	@Override
	public void updateEntity() {
		// Implement suction myself because ???
		super.updateEntity();

		DawnMachineTileEntity controller = getController();

		if (controller == null)
			return;

		if ((!this.worldObj.isRemote) && (getWorldObj().getWorldTime() % 5 == 0)) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				Aspect aspect = getEssentiaType(dir);
				if (aspect == null)
					continue;

				if (!controller.needsMore(aspect))
					continue;

				TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, this.xCoord, this.yCoord, this.zCoord, dir);
				if (te != null) {
					IEssentiaTransport ic = (IEssentiaTransport) te;
					if (!ic.canOutputTo(dir.getOpposite()))
						return;

					if (ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(dir))
						addToContainer(aspect, ic.takeEssentia(aspect, 1, dir.getOpposite()));
				}
			}
		}
	}

	@Override
	public AspectList getAspects() {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? new AspectList() : getController().getAspects();
	}

	@Override
	public void setAspects(AspectList aspectList) {
		DawnMachineTileEntity controller = getController();

		if (controller != null)
			controller.setAspects(aspectList);
	}

	@Override
	public boolean doesContainerAccept(Aspect aspect) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? false : controller.doesContainerAccept(aspect);
	}

	@Override
	public int addToContainer(Aspect aspect, int i) {
		if (i == 0)
			return 0;

		DawnMachineTileEntity controller = getController();

		return (controller == null) ? i : controller.addToContainer(aspect, i);
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int i) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? false : controller.takeFromContainer(aspect, i);
	}

	@Override
	public boolean takeFromContainer(AspectList aspectList) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? false : controller.takeFromContainer(aspectList);
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int i) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? false : controller.doesContainerContainAmount(aspect, i);
	}

	@Override
	public boolean doesContainerContain(AspectList aspectList) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? false : controller.doesContainerContain(aspectList);
	}

	@Override
	public int containerContains(Aspect aspect) {
		DawnMachineTileEntity controller = getController();

		return (controller == null) ? 0 : controller.containerContains(aspect);
	}

	@Override
	public boolean isConnectable(ForgeDirection forgeDirection) {
		return getEssentiaType(forgeDirection) != null;
	}

	@Override
	public boolean canInputFrom(ForgeDirection forgeDirection) {
		return isConnectable(forgeDirection);
	}

	@Override
	public boolean canOutputTo(ForgeDirection forgeDirection) {
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int i) {}

	@Override
	public Aspect getSuctionType(ForgeDirection forgeDirection) {
		return getEssentiaType(forgeDirection);
	}

	@Override
	public int getSuctionAmount(ForgeDirection forgeDirection) {
		DawnMachineTileEntity controller = getController();

		if (controller == null)
			return 0;

		Aspect aspect = getEssentiaType(forgeDirection);

		return controller.needsMore(aspect) ? 256 : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return (canOutputTo(face)) && (takeFromContainer(aspect, amount)) ? amount : 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return canInputFrom(face) ? amount - addToContainer(aspect, amount) : 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection forgeDirection) {
		int meta = getWorldObj().getBlockMetadata(xCoord, yCoord, zCoord);
		return BBBlock.dawnMachineInput.getSpoutAspect(forgeDirection.ordinal(), meta);
	}

	@Override
	public int getEssentiaAmount(ForgeDirection forgeDirection) {
		DawnMachineTileEntity controller = getController();

		if (controller == null)
			return 0;

		return controller.containerContains(getEssentiaType(forgeDirection));
	}

	@Override
	public int getMinimumSuction() { return 256; }

	@Override
	public boolean renderExtendedTube() {
		return true;
	}
}
