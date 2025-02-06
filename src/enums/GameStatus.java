package src.enums;

public enum GameStatus {
    RUNNING,
    WON,
    LOST,
    TIED,
    IDLE;

    public String toString() {
        return this.name();
    }
}
