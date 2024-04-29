package talonos.blightbuster.tileentity.dawnmachine;

import thaumcraft.api.aspects.Aspect;

public enum DawnMachineResource {

    // machina, ordo, cognitio costs * 32 because they were significantly cheaper
    // They work per operation, not per block, so after moving to a chunkwise
    // operation they only get used a fraction of the time

    // Aspect, mult * 4 so you can halve three times
    // aspect mult cost max rf mana blood u v
    SANO(Aspect.HEAL, 512 * 4, 2 * 4, 16384 * 4, 16, 250, 12, 0.6f, 0.5f),
    IGNIS(Aspect.FIRE, 2048 * 4, 2 * 4, 65536 * 4, 4, 250, 12, 0, 0.5f),
    AER(Aspect.AIR, 128 * 4, 2 * 4, 4096 * 4, 63, 750, 37, 0, 0),
    COGNITIO(Aspect.MIND, 128 * 4, 2 * 32 * 4, 4096 * 4, 63, 500, 25, 0.6f, 0),
    MACHINA(Aspect.MECHANISM, 128 * 4, 2 * 32 * 4, 4096 * 4, 63, 500, 25, 0.2f, 0.5f),
    AURAM(Aspect.AURA, 1 * 4, 4 * 4, 32 * 4, 16000, 100, 50, 0.4f, 0),
    VACUOS(Aspect.VOID, 512 * 4, 2 * 4, 16384 * 4, 16, 250, 12, 0.8f, 0.5f),
    ORDO(Aspect.ORDER, 128 * 4, 2 * 32 * 4, 4096 * 4, 63, 750, 37, 0.4f, 0.5f),
    ARBOR(Aspect.TREE, 256 * 4, 2 * 4, 8192 * 4, 32, 500, 25, 0.2f, 0),
    HERBA(Aspect.PLANT, 512 * 4, 2 * 4, 16384 * 4, 16, 250, 12, 0.8f, 0);

    private final Aspect aspect;
    private final int valueMultiplier;
    private final int cost;
    private final int maximumValue;
    private final int rfDiscountCost;
    private final int manaDiscountCost;
    private final int bloodDiscountCost;
    private final float u;
    private final float v;

    DawnMachineResource(Aspect aspect, int valueMultiplier, int cost, int maximumValue, int rfDiscountCost,
        int manaDiscountCost, int bloodDiscountCost, float u, float v) {
        this.aspect = aspect;
        this.valueMultiplier = valueMultiplier;
        this.cost = cost;
        this.maximumValue = maximumValue;
        this.rfDiscountCost = rfDiscountCost;
        this.manaDiscountCost = manaDiscountCost;
        this.bloodDiscountCost = bloodDiscountCost;
        this.u = u;
        this.v = v;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public int getValueMultiplier() {
        return this.valueMultiplier;
    }

    public int getAspectCost() {
        return this.cost;
    }

    public int getMaximumValue() {
        return this.maximumValue;
    }

    public int getEnergyCost() {
        return this.rfDiscountCost;
    }

    public int getManaCost() {
        return this.manaDiscountCost;
    }

    public int getBloodCost() {
        return this.bloodDiscountCost;
    }

    public float getU() {
        return this.u;
    }

    public float getV() {
        return this.v;
    }

    public static DawnMachineResource getResourceFromAspect(Aspect aspect) {
        if (aspect == Aspect.HEAL) {
            return SANO;
        }
        if (aspect == Aspect.FIRE) {
            return IGNIS;
        }
        if (aspect == Aspect.AIR) {
            return AER;
        }
        if (aspect == Aspect.MIND) {
            return COGNITIO;
        }
        if (aspect == Aspect.MECHANISM) {
            return MACHINA;
        }
        if (aspect == Aspect.AURA) {
            return AURAM;
        }
        if (aspect == Aspect.VOID) {
            return VACUOS;
        }
        if (aspect == Aspect.ORDER) {
            return ORDO;
        }
        if (aspect == Aspect.TREE) {
            return ARBOR;
        }
        if (aspect == Aspect.PLANT) {
            return HERBA;
        }

        return null;
    }
}
