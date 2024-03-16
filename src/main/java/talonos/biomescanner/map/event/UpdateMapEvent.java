package talonos.biomescanner.map.event;

import cpw.mods.fml.common.eventhandler.Event;

public class UpdateMapEvent extends Event {

    private int x;
    private int y;
    private int width;
    private int height;

    public UpdateMapEvent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
