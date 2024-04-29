package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.world.chunk.IChunkProvider;

public abstract interface UBStrataColumnProvider {

    public abstract UBStrataColumn strataColumn(int paramInt1, int paramInt2);

    public abstract IChunkProvider UBChunkProvider(IChunkProvider paramIChunkProvider);

    public abstract boolean inChunkGenerationAllowed();
}
