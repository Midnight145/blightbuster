package talonos.biomescanner.map;

import net.minecraft.util.StatCollector;

public enum Zone {

    SwampBay("zone.swampbay", 0),
    ArchesBay("zone.archesbay", 11),
    NorthOcean("zone.northocean", 22),
    SouthOcean("zone.southocean", 33),
    EastOcean("zone.eastocean", 44),
    WestOcean("zone.westocean", 55),

    Desert("zone.desert", 66),
    Pillars("zone.pillars", 77),
    Arches("zone.arches", 88),
    Mountains("zone.mountains", 99),
    MagicalForest("zone.magicalforest", 110),
    EeriedIsland("zone.eeriedisland", 121),

    Forests("zone.forests", 132),
    Plains("zone.plains", 143),
    Swamps("zone.swamps", 154),
    Jungle("zone.jungle", 165),
    Savannah("zone.savannah", 176),
    MushroomIsland("zone.mushroomisland", 187),

    FloatingIslands("zone.floatingislands", 198),
    Iceberg("zone.iceberg", 209),
    Archipelago("zone.archipelago", 220),
    VolcanoIsland("zone.volcanoisland", 231),
    ColdForests("zone.coldforests", 242),
    Tundra("zone.tundra", 253);

    private String unlocalizedName;
    private int imageColor;

    Zone(String unlocalizedName, int imageColor) {
        this.unlocalizedName = unlocalizedName;
        this.imageColor = imageColor;
    }

    public String getDisplay() {
        return StatCollector.translateToLocal(unlocalizedName);
    }

    public int getImageColor() {
        return imageColor;
    }
}
