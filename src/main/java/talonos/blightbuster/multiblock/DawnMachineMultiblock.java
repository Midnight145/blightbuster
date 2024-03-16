package talonos.blightbuster.multiblock;

import java.util.ArrayList;
import java.util.List;

import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.multiblock.entries.BasicMultiblockState;
import talonos.blightbuster.multiblock.entries.IMultiblockEntryState;
import talonos.blightbuster.multiblock.entries.MultiblockEntry;
import talonos.blightbuster.multiblock.entries.NonSolidBlockState;
import talonos.blightbuster.multiblock.entries.NullMultiblockState;
import talonos.blightbuster.multiblock.entries.OrientationMultiblockState;
import thaumcraft.common.config.ConfigBlocks;

public class DawnMachineMultiblock extends Multiblock {

    private List<MultiblockEntry> entries = new ArrayList<MultiblockEntry>(9);
    private MultiblockEntry controller = null;

    public DawnMachineMultiblock() {}

    public void init() {
        IMultiblockEntryState silverwoodLog = new BasicMultiblockState(ConfigBlocks.blockMagicalLog, 1);
        IMultiblockEntryState cyberTotem = new BasicMultiblockState(BBBlock.cyberTotem);
        IMultiblockEntryState blankSpace = new NonSolidBlockState();

        IMultiblockEntryState nullState = new NullMultiblockState();
        IMultiblockEntryState dawnMachine = new OrientationMultiblockState(BBBlock.dawnMachine);
        IMultiblockEntryState leftBuffer = new OrientationMultiblockState(BBBlock.dawnMachineBuffer, 0, 0xC);
        IMultiblockEntryState rightBuffer = new OrientationMultiblockState(BBBlock.dawnMachineBuffer, 4, 0xC);
        IMultiblockEntryState bottomLeftSpout = new OrientationMultiblockState(BBBlock.dawnMachineInput, 0, 0xC);
        IMultiblockEntryState bottomRightSpout = new OrientationMultiblockState(BBBlock.dawnMachineInput, 4, 0xC);
        IMultiblockEntryState topLeftSpout = new OrientationMultiblockState(BBBlock.dawnMachineInput, 8, 0xC);
        IMultiblockEntryState topRightSpout = new OrientationMultiblockState(BBBlock.dawnMachineInput, 12, 0xC);

        entries.add(new MultiblockEntry(-1, 1, 0, silverwoodLog, topLeftSpout));
        entries.add(new MultiblockEntry(0, 1, 0, blankSpace, nullState));
        entries.add(new MultiblockEntry(1, 1, 0, silverwoodLog, topRightSpout));
        entries.add(new MultiblockEntry(-1, 0, 0, silverwoodLog, bottomLeftSpout));
        controller = new MultiblockEntry(0, 0, 0, cyberTotem, dawnMachine);
        entries.add(controller);
        entries.add(new MultiblockEntry(1, 0, 0, silverwoodLog, bottomRightSpout));
        entries.add(new MultiblockEntry(-1, -1, 0, silverwoodLog, leftBuffer));
        entries.add(new MultiblockEntry(1, -1, 0, silverwoodLog, rightBuffer));
    }

    @Override
    protected Iterable<MultiblockEntry> getMultiblockSchema() {
        return entries;
    }

    @Override
    public MultiblockEntry getControllerEntry() {
        return controller;
    }
}
