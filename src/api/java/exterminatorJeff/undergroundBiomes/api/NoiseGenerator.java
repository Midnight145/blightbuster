package exterminatorJeff.undergroundBiomes.api;

public abstract class NoiseGenerator
{
    protected final int[] perm = new int[512];
    protected double offsetX;
    protected double offsetY;
    protected double offsetZ;

    public static int floor(double x)
    {
        return x >= 0.0D ? (int)x : (int)x - 1;
    }

    protected static double fade(double x) {
        return x * x * x * (x * (x * 6.0D - 15.0D) + 10.0D);
    }

    protected static double lerp(double x, double y, double z) {
        return y + x * (z - y);
    }

    protected static double grad(int hash, double x, double y, double z) {
        hash &= 15;
        double u = hash < 8 ? x : y;
        double v = (hash == 12) || (hash == 14) ? x : hash < 4 ? y : z;
        return ((hash & 0x1) == 0 ? u : -u) + ((hash & 0x2) == 0 ? v : -v);
    }

    public double noise(double x)
    {
        return noise(x, 0.0D, 0.0D);
    }

    public double noise(double x, double y)
    {
        return noise(x, y, 0.0D);
    }

    public abstract double noise(double paramDouble1, double paramDouble2, double paramDouble3);

    public double noise(double x, int octaves, double frequency, double amplitude)
    {
        return noise(x, 0.0D, 0.0D, octaves, frequency, amplitude);
    }

    public double noise(double x, int octaves, double frequency, double amplitude, boolean normalized)
    {
        return noise(x, 0.0D, 0.0D, octaves, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, int octaves, double frequency, double amplitude)
    {
        return noise(x, y, 0.0D, octaves, frequency, amplitude);
    }

    public double noise(double x, double y, int octaves, double frequency, double amplitude, boolean normalized)
    {
        return noise(x, y, 0.0D, octaves, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, double z, int octaves, double frequency, double amplitude)
    {
        return noise(x, y, z, octaves, frequency, amplitude, false);
    }

    public double noise(double x, double y, double z, int octaves, double frequency, double amplitude, boolean normalized)
    {
        double result = 0.0D;
        double amp = 1.0D;
        double freq = 1.0D;
        double max = 0.0D;

        for (int i = 0; i < octaves; i++) {
            result += noise(x * freq, y * freq, z * freq) * amp;
            max += amp;
            freq *= frequency;
            amp *= amplitude;
        }

        if (normalized) {
            result /= max;
        }

        return result;
    }
}