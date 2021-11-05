package talonos.blightbuster.tileentity.dawnmachine;

import thaumcraft.api.aspects.Aspect;

public enum DawnMachineResource {
			// aspect			   mult     cost   max   rf     mana  blood   u     v
			// Aspect, mult * 4 so you can halve three times
    SANO     (Aspect.HEAL,      512  * 4,  2 * 4, 16384 * 4, 16,    0,      0,   0.6f, 0.5f),
    IGNIS    (Aspect.FIRE,      2048 * 4,  2 * 4, 65536 * 4, 4,     0,      0,   0,    0.5f),
    AER      (Aspect.AIR,       128  * 4,  2 * 4, 4096  * 4, 63,    0,      0,   0,    0),
    COGNITIO (Aspect.MIND,      128  * 4,  2 * 4, 4096  * 4, 63,    0,      0,   0.6f, 0),
    MACHINA  (Aspect.MECHANISM, 128  * 4,  2 * 4, 4096  * 4, 63,    0,      0,   0.2f, 0.5f),
    AURAM    (Aspect.AURA,      1    * 4,  4 * 4, 32    * 4, 16000, 0,      0,   0.4f, 0),
    VACUOS   (Aspect.VOID,      512  * 4,  2 * 4, 16384 * 4, 16,    0,      0,   0.8f, 0.5f),
    ORDO     (Aspect.ORDER,     128  * 4,  2 * 4, 4096  * 4, 63,    0,      0,   0.4f, 0.5f),
    ARBOR    (Aspect.TREE,      256  * 4,  2 * 4, 8192  * 4, 32,    0,      0,   0.2f, 0),
    HERBA    (Aspect.PLANT,     512  * 4,  2 * 4, 16384 * 4, 16,    0,      0,   0.8f, 0);

    private Aspect aspect;
    private int valueMultiplier;
    private int cost;
    private int maximumValue;
    private int rfDiscountCost;
    private int manaDiscountCost;
    private int bloodDiscountCost;
    private float u;
    private float v;

    DawnMachineResource(Aspect aspect, int valueMultiplier, int cost, int maximumValue, int rfDiscountCost, int manaDiscountCost, int bloodDiscountCost, float u, float v) {
        this.aspect = aspect;
        this.valueMultiplier = valueMultiplier;
        this.cost = cost;
        this.maximumValue = maximumValue;
        this.rfDiscountCost = rfDiscountCost;
        this.manaDiscountCost = manaDiscountCost; // TODO: update
        this.bloodDiscountCost = bloodDiscountCost; // TODO: update
        this.u = u;
        this.v = v;
    }

    public Aspect getAspect() { return aspect; }
    public int getValueMultiplier() { return valueMultiplier; }
    public int getAspectCost() { return cost; }
    public int getMaximumValue() { return maximumValue; }
    public int getEnergyCost() { return rfDiscountCost; }
    public int getManaCost() { return manaDiscountCost; }
    public int getBloodCost() { return bloodDiscountCost; }
    public float getU() { return u; }
    public float getV() { return v; }

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
