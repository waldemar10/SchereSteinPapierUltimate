package src.enums;

import java.awt.*;

public enum ColorPalette {
    RED(new Color(255, 0, 0)),
    BLUE(new Color(0, 0, 255)),
    GREEN(new Color(0, 255, 0)),
    CYAN(new Color(0, 255, 255)),
    MAGENTA(new Color(255, 0, 255)),
    YELLOW(new Color(255, 255, 0)),
    ORANGE(new Color(255, 165, 0)),
    WHITE(new Color(255, 255, 255)),
    BLACK(new Color(0, 0, 0)),
    SOFT_RED(new Color(230, 90, 90)),
    DEEP_BLUE(new Color(30, 60, 150)),
    FOREST_GREEN(new Color(34, 139, 34)),
    TURQUOISE(new Color(64, 224, 208)),
    PURPLE(new Color(128, 0, 128)),
    GOLDEN_YELLOW(new Color(255, 215, 0)),
    WARM_ORANGE(new Color(255, 140, 0)),
    LIGHT_GRAY(new Color(200, 200, 200)),
    DARK_GRAY(new Color(50, 50, 50)),
    SOFT_PINK(new Color(255, 182, 193)),
    SKY_BLUE(new Color(135, 206, 250)),
    OLIVE_GREEN(new Color(107, 142, 35)),
    DARK_CYAN(new Color(0, 139, 139)),
    VIOLET(new Color(148, 0, 211)),
    SANDY_BROWN(new Color(244, 164, 96))
    ;
    private final Color color;
    ColorPalette(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
}
