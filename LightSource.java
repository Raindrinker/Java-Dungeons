/**
 * Created by FerranRuiz on 07/02/2015.
 */
public class LightSource {
    int x, y;
    int intensity;

    /**
     * Light source data, consisting on its position and its intensity
     * @param x x position
     * @param y y position
     * @param intensity light intensity
     */
    public LightSource(int x, int y, int intensity) {
        this.x = x;
        this.y = y;
        this.intensity = intensity;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
