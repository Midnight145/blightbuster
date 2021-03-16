package exterminatorJeff.undergroundBiomes.api;

public abstract class UndergroundBiomeSet
{
    public final StrataLayer[][] strataLayers;
    public final BiomeGenUndergroundBase[] biomeList = new BiomeGenUndergroundBase[256];

    public UndergroundBiomeSet(StrataLayer[][] strataLayers) {
        this.strataLayers = strataLayers;
    }

    public abstract BiomeGenUndergroundBase[] allowedBiomes();
}