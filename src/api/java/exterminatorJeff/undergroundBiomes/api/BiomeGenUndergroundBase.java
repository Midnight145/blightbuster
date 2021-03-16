package exterminatorJeff.undergroundBiomes.api;

public class BiomeGenUndergroundBase
{
    public String biomeName = "";
    public final int biomeID;
    public boolean hasStrata = false;
    public StrataLayer[] strata;
    public final PerlinNoiseGenerator strataNoise;
    public final UBStoneCodes fillerBlockCodes;

    public BiomeGenUndergroundBase(int ID, NamedBlock filler, int metadataValue, BiomeGenUndergroundBase[] biomeList)
    {
        this.biomeID = ID;
        this.fillerBlockCodes = new UBStoneCodes(filler, metadataValue);
        this.strataNoise = new PerlinNoiseGenerator(1L);
        biomeList[ID] = this;
    }

    public BiomeGenUndergroundBase(int ID, NamedBlock filler, int metadataValue, BiomeGenUndergroundBase[] biomeList, StrataLayer[] strataLayers)
    {
        this.biomeID = ID;
        this.fillerBlockCodes = new UBStoneCodes(filler, metadataValue);
        this.strataNoise = new PerlinNoiseGenerator(1L);
        biomeList[ID] = this;
        AddStrataLayers(strataLayers);
    }

    public BiomeGenUndergroundBase AddStrataLayers(StrataLayer[] strata) {
        this.hasStrata = true;
        this.strata = strata;
        return this;
    }

    public UBStoneCodes getStrataBlockAtLayer(int y) {
        for (int i = 0; i < this.strata.length; i++) {
            if (this.strata[i].valueIsInLayer(y) == true) {
                return this.strata[i].codes;
            }
        }
        return this.fillerBlockCodes;
    }

    public BiomeGenUndergroundBase setName(String name) {
        this.biomeName = name;
        return this;
    }
}