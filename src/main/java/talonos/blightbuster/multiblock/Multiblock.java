package talonos.blightbuster.multiblock;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import talonos.blightbuster.multiblock.entries.IMultiblockEntryState;
import talonos.blightbuster.multiblock.entries.MultiblockEntry;

public abstract class Multiblock {

    public abstract void init();

    public int findMultiblockFit(World world, int x, int y, int z) {
        return findMultiblockFit(world, x, y, z, false, null);
    }

    public int findMultiblockFit(World world, int x, int y, int z, boolean isConverted, MultiblockEntry centerEntry) {
        for (int orientation = 0; orientation < 4; orientation++) {
            if (checkSingleFit(world, x, y, z, orientation, isConverted, centerEntry)) return orientation;
        }

        return -1;
    }

    protected boolean checkSingleFit(World world, int x, int y, int z, int orientation, boolean isConverted,
        MultiblockEntry centerEntry) {
        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        if (centerEntry != null) {
            ForgeDirection zAxis = ForgeDirection.SOUTH;
            ForgeDirection xAxis = ForgeDirection.EAST;

            for (int i = 0; i < orientation; i++) {
                zAxis = zAxis.getRotation(ForgeDirection.UP);
                xAxis = xAxis.getRotation(ForgeDirection.UP);
            }

            offsetX = xAxis.offsetX * centerEntry.getXOffset() + zAxis.offsetX * centerEntry.getZOffset();
            offsetZ = xAxis.offsetZ * centerEntry.getXOffset() + zAxis.offsetZ * centerEntry.getZOffset();
            offsetY = centerEntry.getYOffset();
        }

        return hasMultiblockFitWithOrientation(
            world,
            x - offsetX,
            y - offsetY,
            z - offsetZ,
            orientation,
            isConverted,
            centerEntry);
    }

    protected abstract Iterable<MultiblockEntry> getMultiblockSchema();

    public abstract MultiblockEntry getControllerEntry();

    public Pair<MultiblockEntry, Integer> getEntry(World world, int x, int y, int z, int orientation, Block block,
        int meta) {
        boolean convertedMultiblock = false;
        if (orientation >= 0) convertedMultiblock = true;

        for (MultiblockEntry entry : getMultiblockSchema()) {
            IMultiblockEntryState test = (convertedMultiblock) ? entry.getConvertedState() : entry.getBuildState();

            if (test.isBlockState(block, meta)) {
                if (!convertedMultiblock) {
                    int outOrientation = findMultiblockFit(world, x, y, z, false, entry);
                    if (outOrientation >= 0) return new ImmutablePair<MultiblockEntry, Integer>(entry, outOrientation);
                } else {
                    if (checkSingleFit(world, x, y, z, orientation, true, entry))
                        return new ImmutablePair<MultiblockEntry, Integer>(entry, orientation);
                }
            }
        }

        return null;
    }

    protected boolean hasMultiblockFitWithOrientation(World world, int x, int y, int z, int orientation,
        boolean isConverted, MultiblockEntry excludeEntry) {
        ForgeDirection zAxis = ForgeDirection.SOUTH;
        ForgeDirection xAxis = ForgeDirection.EAST;

        for (int i = 0; i < orientation; i++) {
            zAxis = zAxis.getRotation(ForgeDirection.UP);
            xAxis = xAxis.getRotation(ForgeDirection.UP);
        }

        for (MultiblockEntry entry : getMultiblockSchema()) {
            if (entry == excludeEntry) continue;

            int offsetX = xAxis.offsetX * entry.getXOffset() + zAxis.offsetX * entry.getZOffset();
            int offsetZ = xAxis.offsetZ * entry.getXOffset() + zAxis.offsetZ * entry.getZOffset();
            int offsetY = entry.getYOffset();

            IMultiblockEntryState testState = isConverted ? entry.getConvertedState() : entry.getBuildState();

            if (!testState.isBlockState(world, x + offsetX, y + offsetY, z + offsetZ)) return false;
        }

        return true;
    }

    public void convertMultiblockWithOrientationFromSideBlock(World world, int x, int y, int z, int orientation,
        boolean doUnconvert, MultiblockEntry sideBlock) {
        ForgeDirection zAxis = ForgeDirection.SOUTH;
        ForgeDirection xAxis = ForgeDirection.EAST;

        for (int i = 0; i < orientation; i++) {
            zAxis = zAxis.getRotation(ForgeDirection.UP);
            xAxis = xAxis.getRotation(ForgeDirection.UP);
        }

        int offsetX = xAxis.offsetX * sideBlock.getXOffset() + zAxis.offsetX * sideBlock.getZOffset();
        int offsetZ = xAxis.offsetZ * sideBlock.getXOffset() + zAxis.offsetZ * sideBlock.getZOffset();
        int offsetY = sideBlock.getYOffset();

        convertMultiblockWithOrientation(world, x - offsetX, y - offsetY, z - offsetZ, orientation, doUnconvert);
    }

    public void convertMultiblockWithOrientation(World world, int x, int y, int z, int orientation) {
        convertMultiblockWithOrientation(world, x, y, z, orientation, false);
    }

    public void unconvertMultiblockWithOrientation(World world, int x, int y, int z, int orientation) {
        convertMultiblockWithOrientation(world, x, y, z, orientation, true);
    }

    protected void convertMultiblockWithOrientation(World world, int x, int y, int z, int orientation,
        boolean doUnconvert) {
        ForgeDirection zAxis = ForgeDirection.SOUTH;
        ForgeDirection xAxis = ForgeDirection.EAST;

        for (int i = 0; i < orientation; i++) {
            zAxis = zAxis.getRotation(ForgeDirection.UP);
            xAxis = xAxis.getRotation(ForgeDirection.UP);
        }

        for (MultiblockEntry entry : getMultiblockSchema()) {
            int offsetX = xAxis.offsetX * entry.getXOffset() + zAxis.offsetX * entry.getZOffset();
            int offsetZ = xAxis.offsetZ * entry.getXOffset() + zAxis.offsetZ * entry.getZOffset();
            int offsetY = entry.getYOffset();

            IMultiblockEntryState testState = null;
            IMultiblockEntryState convertState = null;

            if (doUnconvert) {
                testState = entry.getConvertedState();
                convertState = entry.getBuildState();
            } else {
                testState = entry.getBuildState();
                convertState = entry.getConvertedState();
            }

            if (testState.isBlockState(world, x + offsetX, y + offsetY, z + offsetZ))
                convertState.replaceWithState(world, x + offsetX, y + offsetY, z + offsetZ, orientation);
        }
    }
}
