package src;

import com.jogamp.opengl.util.PMVMatrix;

/**
 * Enthält Animationensabfolgen für die einzelnen Objekte
 */
public class AnimationGroups {
    Animations a = new Animations();

    private static String playerhand = "";      //Hier wird die Form abgespeichert die der Spieler genommen hat
    private static String computerhand = "";    //Hier wird die Form abgespeichert die der Computer genommen hat
    private static String thisHand = "";        //Hier wird die Form der Hand die gerade verändert wird abgespeichert
    private static String enemyHand = "";       //Hier wird die Form der Hand die gerade nicht verändert wird abgespeichert

    protected static float[] playerHandLoc = {0f, -0.5f, 2f};       //Der Mittelpunkt der Spielerhand
    protected static float[] computerHandLoc = {0f, -0.5f, -2f};    //Der Mittelpunkt der Computerhand
    private static float[] thisHandLoc = new float[3];              //Der Mittelpunkt der Hand die gerade verändert wird
    private static float factor = 0f;                               //Faktor für Transformationen. 1 bei der Spielerhand und -1 bei der Computerhand

    //Die counts laufen nacheinander durch und während jedem count passieren andere Transformationen
    protected static float count1 = 0f;
    protected static float count2 = 0f;
    protected static float count3 = 0f;
    protected static float count4 = 0f;

    private static boolean start = false; //Wird true gesetzt wenn die Hände ausgewählt sind. Erst dann beginnen die counter an zu zählen

    /**
     *
     * @param player übergibt ausgewählte Form des Spielers
     * @param computer übergibt ausgewählte Form des Computers
     */
    //Hier kann die Handerkennung die ausgewählten Formen der Hände übergeben
    protected void giveHands(String player, String computer){
        playerhand = player;
        computerhand = computer;
        start = true;
    }

    /**
     * Die Logik wann welcher counter hochgezählt wird und wie viel
     */
    protected void counting(){
        if(count1 < 90 && start)
            count1 ++;
        else if(count2 < 90 && start)
            count2 ++;
        else if(count3 < 20 && start)
            count3 ++;
        else if(count4 < 90 && start)
            count4 ++;
    }

    /**
     * Setzt alle Werte auf ihren Anfangstartwert zurück
     */
    protected void reset() {
        count1 = 0;
        count2 = 0;
        count3 = 0;
        count4 = 0;
        playerhand = "";
        computerhand = "";
        thisHand = "";
        enemyHand = "";
        playerHandLoc [0] = 0f;
        playerHandLoc [1] = -0.5f;
        playerHandLoc [2] = 2f;
        computerHandLoc [0] = 0f;
        computerHandLoc [1] = -0.5f;
        computerHandLoc [2] = -2f;
        thisHandLoc = new float[3];
        factor = 0;
        start = false;
    }

    /**
     *
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     */
    //Beim aufrufen wird ein Wert übergeben. 1 für den Spieler und 2 für den Computer. Abhängig davon werden die Werte geändert
    private void determineHand(int player){
        if(player == 1){
            thisHand = playerhand;
            enemyHand = computerhand;
            thisHandLoc = playerHandLoc;
            factor = 1;
        }
        else if(player == 2){
            thisHand = computerhand;
            enemyHand = playerhand;
            thisHandLoc = computerHandLoc;
            factor = -1;
        }
    }

    /**
     * Alle Animationen der Handfläche
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsHand(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
                if(count2 > 0) {
                    if (enemyHand.equals("scissor"))
                        a.move(factor,-0.05f,0,-0.8f, count2, thisHandLoc, pmvMatrix);
                    else if (enemyHand.equals("paper"))
                        a.move(factor,-0.2f,0f,-1.45f, count2, thisHandLoc, pmvMatrix);
                    else if (enemyHand.equals("stone"))
                        a.move(factor,-0.2f,0f,-0.9f, count2, thisHandLoc, pmvMatrix);

                    a.rotateX(-1*factor,0, 90, count2, pmvMatrix);
                }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            if(count2 > 0) {
                if (enemyHand.equals("stone"))
                    a.move(factor,0f,0,-1.375f, count2, thisHandLoc, pmvMatrix);
                else if (enemyHand.equals("paper"))
                    a.move(factor,0f,0.425f,-1.375f, count2, thisHandLoc, pmvMatrix);
                else if (enemyHand.equals("scissor")) {
                    a.move(factor, 0f, 0f, -0.9f, count2, thisHandLoc, pmvMatrix);
                    if (count4 > 0)
                        a.move(factor, 0f, 0f, -0.5f, count4, thisHandLoc, pmvMatrix);
                }

                a.rotateX(-1 * factor, 0, 90, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if (count2 > 0){
                if ((enemyHand.equals("paper") || enemyHand.equals("stone")))
                    a.move(factor,0f,0f,-1.945f, count2, thisHandLoc, pmvMatrix);
                else if (enemyHand.equals("scissor")) {
                    a.move(factor,0f,-0.6f,-1.45f, count2, thisHandLoc, pmvMatrix);
                    a.rotateY(-1f, 0, 90, count2, pmvMatrix);
                }
            }
        }
    }

    /**
     * Alle Animationen des ersten Gelenkes vom Kleinen Finger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsKleinerFinger1(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")){
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,-0.325f, 0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,-0.325f, 0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if (count2 > 0) {
                if (enemyHand.equals("scissor")) {
                    a.rotateAllY(factor, -0.325f, 0.575f, 90, count2, thisHandLoc, pmvMatrix);
                    if(count4 > 0) {
                        a.move(factor,0.5f,-0.9f,0f, count4, pmvMatrix);
                        a.rotateZ(-1 * factor, 0, 90, count4, pmvMatrix);
                    }
                }
                else if (enemyHand.equals("paper") || enemyHand.equals("stone")) {
                    a.rotateAllY(factor, -0.325f, 0.575f, 0, count2, thisHandLoc, pmvMatrix);

                    if (count4 > 0 && enemyHand.equals("stone"))
                        a.rotateX(-1 * factor, 0, 90, count4, pmvMatrix);
                }
            }
        }
    }
    /**
     * Alle Animationen des zweiten Gelenkes vom Kleinen Finger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsKleinerFinger2(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);

        }
    }

    /**
     * Alle Animationen des dritten Gelenkes vom Kleinen Finger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsKleinerFinger3(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }
    /**
     * Alle Animationen des ersten Gelenkes vom Ringfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */

    protected void animationsRingFinger1(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,-0.109f,0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,-0.109f,0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if (count2 > 0) {
                if (enemyHand.equals("scissor")) {
                    a.rotateAllY(factor, -0.109f, 0.575f, 90, count2, thisHandLoc, pmvMatrix);
                    if(count4 > 0) {
                        a.move(factor,0.5f,-0.9f,0f, count4, pmvMatrix);
                        a.rotateZ(-1 * factor, 0, 90, count4, pmvMatrix);
                    }
                }
                else if (enemyHand.equals("paper") || enemyHand.equals("stone")) {
                    a.rotateAllY(factor, -0.109f, 0.575f, 0, count2, thisHandLoc, pmvMatrix);

                    if (count4 > 0 && enemyHand.equals("stone"))
                        a.rotateX(-1 * factor, 0, 90, count4, pmvMatrix);
                }
            }
        }
    }
    /**
     * Alle Animationen des zweiten Gelenkes vom Ringfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */

    protected void animationsRingFinger2(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }
    /**
     * Alle Animationen des dritten Gelenkes vom Ringfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */

    protected void animationsRingFinger3(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX( -1*factor,0,90, count1, pmvMatrix);
        }
    }
    /**
     * Alle Animationen des ersten Gelenkes vom Mittelfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsMittelFinger1(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateZ(factor,0,15, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,0.109f, 0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,0,90, count2, pmvMatrix);
                a.rotateZ(factor,0,15, count1, pmvMatrix);
                if(count3 > 0 && enemyHand.equals("paper"))
                    a.rotateZ(-1*factor,0,15, count3, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,0.109f,0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if (count2 > 0) {
                if (enemyHand.equals("scissor")) {
                    a.rotateAllY(factor, 0.109f, 0.575f, 90, count2, thisHandLoc, pmvMatrix);
                    if(count4 > 0) {
                        a.move(factor,0.5f,-0.9f,0f, count4, pmvMatrix);
                        a.rotateZ(-1 * factor, 0, 90, count4, pmvMatrix);
                    }
                }
                else if (enemyHand.equals("paper") || enemyHand.equals("stone")) {
                    a.rotateAllY(factor, 0.109f, 0.575f, 0, count2, thisHandLoc, pmvMatrix);

                    if (count4 > 0 && enemyHand.equals("stone"))
                        a.rotateX(-1 * factor, 0, 90, count4, pmvMatrix);
                }
            }
        }
    }

    /**
     * Alle Animationen des zweiten Gelenkes vom Mittelfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsMittelFinger2(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            if (count4 > 0 && enemyHand.equals("stone"))
                a.rotateZ(factor, 0, 90, count4, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }

    /**
     * Alle Animationen des dritten Gelenkes vom Mittelfinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsMittelFinger3(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {

        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }

    /**
     * Alle Animationen des ersten Gelenkes vom Zeigefinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsZeigeFinger1(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            a.rotateZ(-1*factor,0,15, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,0.325f,0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,0,90, count2, pmvMatrix);
                a.rotateZ(-1*factor,0,15, count1, pmvMatrix);
                if(count3 > 0 && enemyHand.equals("paper"))
                    a.rotateZ(factor,0,15, count3, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
            if(count2 > 0){
                a.rotateAllX(factor,0.325f,0.575f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,90,180, count2, pmvMatrix);
            }
        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if (count2 > 0) {
                if (enemyHand.equals("scissor")) {
                    a.rotateAllY(factor, 0.325f, 0.575f, 90, count2, thisHandLoc, pmvMatrix);
                    if(count4 > 0) {
                        a.move(factor,0.5f,-0.9f,0f, count4, pmvMatrix);
                        a.rotateZ(-1 * factor, 0, 90, count4, pmvMatrix);
                    }
                }
                else if (enemyHand.equals("paper") || enemyHand.equals("stone")) {
                    a.rotateAllY(factor, 0.325f, 0.575f, 0, count2, thisHandLoc, pmvMatrix);

                    if (count4 > 0 && enemyHand.equals("stone"))
                        a.rotateX(-1 * factor, 0, 90, count4, pmvMatrix);
                }
            }
        }
    }

    /**
     * Alle Animationen des zweiten Gelenkes vom Zeigefinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsZeigeFinger2(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            if (count4 > 0 && enemyHand.equals("stone"))
                a.rotateZ(-1*factor, 0, 90, count4, pmvMatrix);
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }


    /**
     * Alle Animationen des dritten Gelenkes vom Zeigefinger der Hand
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsZeigeFinger3(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {

        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateX(-1*factor,0,90, count1, pmvMatrix);
        }
    }


    /**
     * Alle Animationen des ersten Gelenkes vom Daumen
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsDaumen1(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {
            if(count2 > 0) {
                a.rotateAllX(factor,0.475f, 0.100f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,0,90, count2, pmvMatrix);
                pmvMatrix.glRotatef(-30f*factor,0f,0f,1f);
            }
        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {
            a.rotateZ(factor,0,30, count1, pmvMatrix);
            a.rotateX(-1*factor,0,30, count1, pmvMatrix);
            if(count2 > 0) {
                a.rotateAllX(factor,0.475f, 0.100f,90, count2, thisHandLoc, pmvMatrix);
                a.rotateX(-1*factor,0,90, count2, pmvMatrix);
                pmvMatrix.glRotatef(-30f*factor,1f,0f,0f);
            }

        }

        //Animationen wenn die Hand Papier gewählt hat
        if(thisHand.equals("paper")) {
            if(count2 > 0) {
                if (enemyHand.equals("scissor")) {
                    a.rotateAllY(factor, 0.475f, 0.100f, 90, count2, thisHandLoc, pmvMatrix);
                    a.rotateY(-1, 0, 90, count2, pmvMatrix);
                    pmvMatrix.glRotatef(-30f * factor, 0f, 0f, 1f);
                }
                if (enemyHand.equals("paper") || enemyHand.equals("stone")) {
                    a.rotateAllY(factor, 0.475f, 0.100f, 0, count2, thisHandLoc, pmvMatrix);
                    pmvMatrix.glRotatef(-30f * factor, 0f, 0f, 1f);
                }

                if (count4 > 0 && enemyHand.equals("stone")) {
                    a.rotateX(-1 * factor, 0, 65, count4, pmvMatrix);
                    a.rotateZ(factor, 0, 15, count4, pmvMatrix);
                }
            }
        }
    }

    /**
     * Alle Animationen des zweiten Gelenkes vom Daumen
     * @param player entscheidet, ob Spieler oder Computerhand transformiert wird
     * @param pmvMatrix Matrix in der transformiert wird
     */
    protected void animationsDaumen2(int player, PMVMatrix pmvMatrix){
        determineHand(player);

        //Animationen wenn die Hand Schere gewählt hat
        if(thisHand.equals("scissor")) {

        }

        //Animationen wenn die Hand Stein gewählt hat
        if(thisHand.equals("stone")) {

        }
    }

}
