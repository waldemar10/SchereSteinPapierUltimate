package src.enums;

public enum WindowType {
    CALIBRATION("Kalibrierung"),
    MAIN("Hauptmenu"),
    RULES("Regeln"),
    SETTINGS("Einstellungen"),
    GAME("Spiel");

    private final String windowTitle;

    WindowType(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
