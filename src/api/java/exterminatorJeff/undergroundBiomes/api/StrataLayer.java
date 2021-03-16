package exterminatorJeff.undergroundBiomes.api;

public class StrataLayer
{
    public final NamedBlock layerBlock;
    public final int layerMetadataID;
    public final int layerMin;
    public final int layerMax;
    public final UBStoneCodes codes;

    public StrataLayer(NamedBlock layerBlock, int layerMetadataID, int layerMin, int layerMax)
    {
        this.layerBlock = layerBlock;
        this.layerMetadataID = layerMetadataID;
        this.layerMin = layerMin;
        this.layerMax = layerMax;
        this.codes = new UBStoneCodes(layerBlock, layerMetadataID);
    }

    public boolean valueIsInLayer(int y) {
        if ((y >= this.layerMin) && (y <= this.layerMax)) {
            return true;
        }
        return false;
    }
}