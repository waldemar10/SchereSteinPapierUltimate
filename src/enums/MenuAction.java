package src.enums;

public enum MenuAction {
    START("Start", "Start"),
    CALIBRATION("Calibration", "Kalibrierung"),
    RULES("Rules", "Spielregeln"),
    BACK("Back", "Zurück"),
    QUIT("Quit", "Beenden"),
    NEW_ROUND("New Round","Neue Runde"),
    RESET("RESET","Zurücksetzen"),
    SUBMIT("Submit","Bestätigen");

    private final String englishLabel;
    private final String germanLabel;

    MenuAction(String englishLabel, String germanLabel) {
        this.englishLabel = englishLabel;
        this.germanLabel = germanLabel;
    }

    public String getLabel() {
        return germanLabel;
    }

    public String getActionCommand() {
        return name();
    }
}
