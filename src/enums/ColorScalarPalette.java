package src.enums;

import org.opencv.core.Scalar;

/**
 * Color palette (bgr)
 *
 * @author Waldemar Justus
 */
public enum ColorScalarPalette {
    RED(new Scalar(0, 0, 255)),
    BLUE(new Scalar(255, 191, 50)),
    GREEN(new Scalar(0, 255, 0)),
    CYAN(new Scalar(255, 255, 0)),
    MAGENTA(new Scalar(255, 0, 255)),
    YELLOW(new Scalar(0, 255, 255)),
    ORANGE(new Scalar(0, 165, 255)),
    WHITE(new Scalar(255, 255, 255)),
    BLACK(new Scalar(0, 0, 0));

    private final Scalar scalar;

    ColorScalarPalette(Scalar scalar) {
        this.scalar = scalar;
    }

    public Scalar get() {
        return scalar;
    }
}
