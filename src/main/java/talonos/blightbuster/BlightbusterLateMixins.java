package talonos.blightbuster;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import talonos.blightbuster.mixins.Mixins;

// Code here adapted from
// https://github.com/GTNewHorizons/Hodgepodge/blob/master/src/main/java/com/mitchej123/hodgepodge/HodgepodgeLateMixins.java
// and therefore under the LGPL-3.0 license.

@LateMixin
public class BlightbusterLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.blightbuster.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Mixins.getLateMixins(loadedMods);
    }
}
