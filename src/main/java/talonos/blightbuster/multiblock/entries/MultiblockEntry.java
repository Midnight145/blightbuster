package talonos.blightbuster.multiblock.entries;

public class MultiblockEntry {

    private final int xOffset;
    private final int yOffset;
    private final int zOffset;

    private final IMultiblockEntryState buildState;
    private final IMultiblockEntryState convertedState;

    public MultiblockEntry(int xOffset, int yOffset, int zOffset, IMultiblockEntryState buildBlock,
        IMultiblockEntryState convertedBlock) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;

        this.buildState = buildBlock;
        this.convertedState = convertedBlock;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public int getZOffset() {
        return zOffset;
    }

    public IMultiblockEntryState getBuildState() {
        return buildState;
    }

    public IMultiblockEntryState getConvertedState() {
        return convertedState;
    }
}
