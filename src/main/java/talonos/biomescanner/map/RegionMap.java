package talonos.biomescanner.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.nbt.NBTTagCompound;

import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.event.UpdateCompletionEvent;

public class RegionMap {

    Map<Zone, Integer> totalBlocksCount = new HashMap<Zone, Integer>();
    Map<Zone, Integer> baselineCleanBlocksCount = new HashMap<Zone, Integer>();
    Map<Zone, Integer> cleanBlocksCount = new HashMap<Zone, Integer>();
    boolean buildingBaseline = true;

    Zone[][] zoneMap = null;

    Map<Integer, Zone> colorToZone = new HashMap<Integer, Zone>();

    public RegionMap() {

        for (Zone zone : Zone.values()) {
            colorToZone.put(zone.getImageColor(), zone);
        }

        try {
            BufferedImage image = ImageIO
                .read(RegionMap.class.getResourceAsStream("/assets/biomescanner/textures/regionmap.png"));
            zoneMap = new Zone[image.getHeight()][image.getWidth()];

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    zoneMap[y][x] = colorToZone.get(image.getRGB(x, y) & 0xFF);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void resetBaseline() {
        totalBlocksCount.clear();
        cleanBlocksCount.clear();
        baselineCleanBlocksCount.clear();
        buildingBaseline = true;
    }

    public boolean read(NBTTagCompound tag) {
        NBTTagCompound totals = tag.getCompoundTag("TotalBlocks");
        NBTTagCompound baseline = tag.getCompoundTag("BaselineBlocks");
        NBTTagCompound clean = tag.getCompoundTag("CleanBlocks");
        buildingBaseline = tag.getBoolean("BuildingBaseline");

        boolean forceRescan = false;
        for (Zone zone : Zone.values()) {
            String ordinal = Integer.toString(zone.ordinal());
            if (totals.hasKey(ordinal)) totalBlocksCount.put(zone, totals.getInteger(ordinal));

            if (BiomeScanner.zoneBaselines[zone.ordinal()] != null) {
                int configBaseline = BiomeScanner.zoneBaselines[zone.ordinal()];
                int mapBaseline = baseline.getInteger(ordinal);

                if (configBaseline != mapBaseline) forceRescan = true;

                baselineCleanBlocksCount.put(zone, BiomeScanner.zoneBaselines[zone.ordinal()]);
            } else if (baseline.hasKey(ordinal)) baselineCleanBlocksCount.put(zone, baseline.getInteger(ordinal));

            if (clean.hasKey(ordinal)) cleanBlocksCount.put(zone, clean.getInteger(ordinal));
        }

        if (forceRescan) {
            for (Zone zone : cleanBlocksCount.keySet()) {
                cleanBlocksCount.put(zone, 0);
            }
        }

        return forceRescan;
    }

    public void write(NBTTagCompound tag) {
        NBTTagCompound totals = new NBTTagCompound();
        tag.setTag("TotalBlocks", totals);
        NBTTagCompound baseline = new NBTTagCompound();
        tag.setTag("BaselineBlocks", baseline);
        NBTTagCompound clean = new NBTTagCompound();
        tag.setTag("CleanBlocks", clean);
        tag.setBoolean("BuildingBaseline", buildingBaseline);

        for (Zone zone : Zone.values()) {
            String ordinal = Integer.toString(zone.ordinal());
            if (totalBlocksCount.containsKey(zone)) totals.setInteger(ordinal, totalBlocksCount.get(zone));
            if (baselineCleanBlocksCount.containsKey(zone))
                baseline.setInteger(ordinal, baselineCleanBlocksCount.get(zone));
            if (cleanBlocksCount.containsKey(zone)) clean.setInteger(ordinal, cleanBlocksCount.get(zone));
        }
    }

    public Zone incrementBlock(int x, int y, boolean isClean) {
        Zone zone = zoneMap[y / 2][x / 2];
        if (buildingBaseline) {
            increment(totalBlocksCount, zone);
            if (isClean) increment(baselineCleanBlocksCount, zone);
        }

        if (isClean) increment(cleanBlocksCount, zone);

        return zone;
    }

    public UpdateCompletionEvent getUpdateEvent(List<Zone> updatedZones) {
        Map<Zone, Float> completion = new HashMap<Zone, Float>();

        for (Zone zone : updatedZones) {
            completion.put(zone, getZoneCompletion(zone));
        }

        return new UpdateCompletionEvent(completion, getCompletion());
    }

    public void wipeData() {
        cleanBlocksCount.clear();
    }

    public void updateData() {
        buildingBaseline = false;
    }

    public float getZoneCompletion(Zone zone) {
        int baseline = get(baselineCleanBlocksCount, zone);
        int total = get(totalBlocksCount, zone);
        int clean = get(cleanBlocksCount, zone);

        return calculateCompletion(baseline, clean, total);
    }

    public float getCompletion() {
        int baseline = 0;
        int total = 0;
        int clean = 0;

        for (Zone zone : Zone.values()) {
            baseline += get(baselineCleanBlocksCount, zone);
            total += get(totalBlocksCount, zone);
            clean += get(cleanBlocksCount, zone);
        }

        return calculateCompletion(baseline, clean, total);
    }

    private float calculateCompletion(int baseline, int clean, int total) {
        if (total == 0) return 0;

        total -= baseline;
        clean -= baseline;

        if (clean < 0) clean = 0;
        if (clean > total) clean = total;

        return (float) clean / (float) total;
    }

    private void increment(Map<Zone, Integer> map, Zone zone) {
        if (!map.containsKey(zone)) map.put(zone, 1);
        else map.put(zone, map.get(zone) + 1);
    }

    private int get(Map<Zone, Integer> map, Zone zone) {
        if (!map.containsKey(zone)) return 0;
        else return map.get(zone);
    }
}
