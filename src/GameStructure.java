package src;

import src.UI.windows.MainWindow;
import src.enums.GameStatus;

import java.util.HashMap;
import java.util.Map;


public class GameStructure {

    private static final Map<String, String> winnerRules = new HashMap<>();
    private static final String[] gestures = {"Scissor", "Stone", "Paper"};
    public static String selectedGesture;
    static AnimationGroups a = new AnimationGroups();
    private static int playerPoint;
    private static int enemyPoint;
    private static GameStatus gameStatus = GameStatus.IDLE;

    static {
        winnerRules.put("Scissor", "Paper");
        winnerRules.put("Stone", "Scissor");
        winnerRules.put("Paper", "Stone");
    }

    MainWindow mainWindow;

    public GameStructure(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        selectedGesture = "";
    }

    static String generateComputerGesture() {
        return gestures[(int) (Math.random() * gestures.length)];
    }

    public static GameStatus getGameStatus() {
        return gameStatus;
    }

    public static void setGameStatus(GameStatus status) {
        gameStatus = status;
    }

    public static int getPlayerPoint() {
        return playerPoint;
    }

    public static void setPlayerPoint(int playerPoints) {
        playerPoint = playerPoints;
    }


    public static int getEnemyPoint() {
        return enemyPoint;
    }

    public static void setEnemyPoint(int enemyPoints) {
        enemyPoint = enemyPoints;
    }


    static void checkGestures(String player1, String player2) {
        if (!winnerRules.containsKey(player1)) {
            System.out.println("Fehler: Ungültige Geste '" + player1 + "'");
            return;
        }
        if (player1.equals(player2)) {
            MainWindow.updateAnnouncementText("Unentschieden" + " Spielstand: " +
                    GameStructure.getPlayerPoint() + " : " + GameStructure.getEnemyPoint());
            setGameStatus(GameStatus.TIED);
        } else if (winnerRules.get(player1).equals(player2)) {
            setGameStatus(GameStatus.WON);
            setPlayerPoint(GameStructure.getPlayerPoint() + 1);
            MainWindow.updateAnnouncementText("Spieler gewinnt" + " Spielstand: " +
                    GameStructure.getPlayerPoint() + " : " + GameStructure.getEnemyPoint());
        } else {
            setGameStatus(GameStatus.LOST);
            setEnemyPoint(GameStructure.getEnemyPoint() + 1);
            MainWindow.updateAnnouncementText("Computer gewinnt" + " Spielstand: " +
                    GameStructure.getPlayerPoint() + " : " + GameStructure.getEnemyPoint());
        }
        a.giveHands(player1.toLowerCase(), player2.toLowerCase());

    }

    public static void resetAnimation() {
        a.reset();
    }

}


