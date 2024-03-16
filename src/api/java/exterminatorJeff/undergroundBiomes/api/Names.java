package exterminatorJeff.undergroundBiomes.api;

public class Names {

    private final String internal;

    public Names(String internalName) {
        this.internal = internalName;
    }

    public final String internal() {
        return this.internal;
    }

    public final String external() {
        return UBIDs.publicName(this.internal);
    }

    public final String iconName() {
        return UBIDs.iconName(this.internal);
    }

    public final RuntimeException duplicateRegistry() {
        return new RuntimeException("duplication registry for Block " + external());
    }

    public String toString() {
        return external();
    }
}
