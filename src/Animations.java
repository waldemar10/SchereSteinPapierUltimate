package src;

import com.jogamp.opengl.util.PMVMatrix;

/**
 * Enthält die Animationen
 */
public class Animations {
    /**
     *
     * @param factor Spiegelt Verschiebung bei der Computerhand
     * @param x     Verschiebung auf x-Achse
     * @param y     Verschiebung auf y-Achse
     * @param z     Verschiebung auf z-Achse
     * @param count     Anzahl der Verschiebungen
     * @param pmvMatrix Matrix in der verschoben wird
     */
    //Bewegt das ausgewählte Objekt in die übergebenen Richtungen x, y und z
    //Ein Schritt hat die Länge von der Gesamtlänge geteilt durch die Maximale Anzahl Schritte in einem Animationsdurchlauf
    //Mit jedem count wird das Objekt um einen Schritt weiter bewegt
    //Wenn die Computerhand statt der Spielerhand bewegt wird, dann werden die Schritte auf der x und Z Achse gespiegelt
    //Wird genutzt um die Finger zu verschieben
    protected void move(float factor, float x, float y, float z, float count, PMVMatrix pmvMatrix){
        //Verschiebt auf der x Achse um den übergebenen Wert
        if(x != 0 && count < 90)
            pmvMatrix.glTranslatef(factor*count*x/90,  0f, 0f);
        else if(x != 0)
            pmvMatrix.glTranslatef(factor*x,  0f, 0f);

        //Verschiebt auf der y Achse um den übergebenen Wert
        if(y != 0 && count < 90)
            pmvMatrix.glTranslatef(0f,  count*y/90, 0f);
        else if(y != 0)
            pmvMatrix.glTranslatef(0f,  y, 0f);

        //Verschiebt auf der z Achse um den übergebenen Wert
        if(z != 0 && count < 90)
            pmvMatrix.glTranslatef(0f,  0f, factor*count*z/90);
        else if(z != 0)
            pmvMatrix.glTranslatef(0f,  0f, factor*z);
    }

    /**
     *
     * @param factor Spiegelt Verschiebung bei der Computerhand
     * @param x     Verschiebung auf x-Achse
     * @param y     Verschiebung auf y-Achse
     * @param z     Verschiebung auf z-Achse
     * @param count Anzahl der Verschiebungen
     * @param loc   Koordinaten der Handfläche
     * @param pmvMatrix Matrix in der verschoben wird
     */
    //So wie die obige move Methode nur wird hier noch ein Array mit den Koordinaten der zu verschiebenden Hand übergeben
    //Am Ende wird der Array mit den Koordinaten neu abgespeichert
    //Wird genutzt um die Handfläche zu verschieben
    protected void move(float factor, float x, float y, float z, float count, float[] loc, PMVMatrix pmvMatrix){
        //Matrix wird neu aufgesetzt damit das Weltkkordinatensystem wieder gerade ist. Ansonsten verschiebt es auf falscher Achse
        pmvMatrix.glPopMatrix();
        pmvMatrix.glPushMatrix();

        //Ändert um die zu verschiebenden Koordinaten im Array auf der x Achse
        if(x != 0 && count != 90)
            loc[0] = loc[0] + (factor * x/90);

        //Ändert um die zu verschiebenden Koordinaten im Array auf der y Achse
        if(y != 0 && count != 90)
            loc[1] = loc[1] + (y/90);

        //Ändert um die zu verschiebenden Koordinaten im Array auf der z Achse
        if(z != 0 && count != 90)
            loc[2] = loc[2] + (factor * z/90);

        //Verschiebt Array auf neue Koordinaten
        pmvMatrix.glTranslatef(loc[0],  loc[1], loc[2]);

        //Speichert neue Koordinaten ab. Wenn die Spielerhand verschoben wird dann werden die Koordniaten geändert ansonsten die von der Computerhand
        if(factor == 1)
            AnimationGroups.playerHandLoc = loc;
        else
            AnimationGroups.computerHandLoc = loc;
    }

    /**
     * Rotiert um x-Achse
     * @param factor Spiegelt Rotation bei der Computerhand
     * @param startdegree   Winkel beim Start der Rotation
     * @param enddegree     Winkel am Ende der Rotation
     * @param count         Anzahl der Rotationen
     * @param pmvMatrix Matrix in der rotiert wird
     */
    //Rotiert um die X-Achse von dem Startwinkel bis zum Endwinkel bei jedem count um einen Grad bis der erwünschte Grad erreicht ist und dann hält er diesen Grad
    protected void rotateX(float factor, int startdegree, int enddegree, float count, PMVMatrix pmvMatrix){
        if (count <= (enddegree-startdegree))
            pmvMatrix.glRotatef((count+startdegree)*factor, count+startdegree, 0f, 1f);
        else
            pmvMatrix.glRotatef(enddegree*factor, enddegree, 0f, 1f);
    }

    /**
     * Rotiert um y-Achse
     * @param factor Spiegelt Rotation bei der Computerhand
     * @param startdegree   Winkel beim Start der Rotation
     * @param enddegree     Winkel am Ende der Rotation
     * @param count         Anzahl der Rotationen
     * @param pmvMatrix Matrix in der rotiert wird
     */
    //Rotiert um die Y-Achse von dem Startwinkel bis zum Endwinkel bei jedem count um einen Grad bis der erwünschte Grad erreicht ist und dann hält er diesen Grad
    protected void rotateY(float factor, int startdegree, int enddegree, float count, PMVMatrix pmvMatrix){
         if (count <= (enddegree-startdegree))
            pmvMatrix.glRotatef((count+startdegree)*factor, 0f, count+startdegree, 1f);
        else
            pmvMatrix.glRotatef(enddegree*factor, 0f, enddegree, 1f);
    }

    /**
     * Rotiert um z-Achse
     * @param factor Spiegelt Rotation bei der Computerhand
     * @param startdegree   Winkel beim Start der Rotation
     * @param enddegree     Winkel am Ende der Rotation
     * @param count         Anzahl der Rotationen
     * @param pmvMatrix Matrix in der rotiert wird
     */
    //Rotiert um die Z-Achse von dem Startwinkel bis zum Endwinkel bei jedem count um einen Grad bis der erwünschte Grad erreicht ist und dann hält er diesen Grad
    protected void rotateZ(float factor, int startdegree, int enddegree, float count, PMVMatrix pmvMatrix){
        if (count <= (enddegree-startdegree))
            pmvMatrix.glRotatef((count+startdegree)*factor, 0f, 0f, count+startdegree);
        else
            pmvMatrix.glRotatef(enddegree*factor, 0f, 0f, enddegree);
    }

    /**
     * Rotiert um die Handfläche auf der x-Achse
     * @param factor    Spiegelt Rotation bei der Computerhand
     * @param distancex Abstand vom Mittelpunkt der Hand auf x-Achse
     * @param distancey Abstand vom Mittelpunkt der Hand auf y-Achse
     * @param degree    Winkel der Rotation
     * @param count     Anzahl der Rotationen
     * @param loc       Koordinaten der Handfläche
     * @param pmvMatrix Matrix in der rotiert wird
     */
    //Bewegt das Objekt im Einheitskreis um den Mittelpunkt loc um die übergebene Gradzahl
    //Rotiert um X-Achse mit jedem count um einen Grad bis der gewünschte Grad erreicht ist
    protected void rotateAllX(float factor, float distancex, float distancey, int degree, float count, float[] loc, PMVMatrix pmvMatrix){
        //Matrix wird neu aufgesetzt damit das Weltkkordinatensystem wieder gerade ist. Ansonsten verschiebt es auf falscher Achse
        pmvMatrix.glPopMatrix();
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(loc[0],loc[1],loc[2]);
        float y, z;

        //Berechnet um wie viel auf der Y- und Z-Achse verschoben werden muss
        if(count <= degree) {
            z = (float)(Math.cos((-270-count) * Math.PI/180) * distancey);
            y = (float)(Math.sin((-270-count) * Math.PI/180) * distancey);
        }
        else {
            z = (float)(Math.cos((-270-degree) * Math.PI/180) * distancey);
            y = (float)(Math.sin((-270-degree) * Math.PI/180) * distancey);
        }

        //Verschiebt um berrechnete Anzahl
        pmvMatrix.glTranslatef(distancex*factor, y, -z*factor);
    }

    /**
     * Rotiert um die Handfläche auf der y-Achse
     * @param factor    Spiegelt Rotation bei der Computerhand
     * @param distancex Abstand vom Mittelpunkt der Hand auf x-Achse
     * @param distancey Abstand vom Mittelpunkt der Hand auf y-Achse
     * @param degree    Winkel der Rotation
     * @param count     Anzahl der Rotationen
     * @param loc       Koordinaten der Handfläche
     * @param pmvMatrix Matrix in der rotiert wird
     */
    //Bewegt das Objekt im Einheitskreis um den Mittelpunkt loc um die übergebene Gradzahl
    //Rotiert um X-Achse mit jedem count um einen Grad bis der gewünschte Grad erreicht ist
    protected void rotateAllY(float factor, float distancex, float distancey, int degree, float count, float[] loc, PMVMatrix pmvMatrix){
        //Matrix wird neu aufgesetzt damit das Weltkkordinatensystem wieder gerade ist. Ansonsten verschiebt es auf falscher Achse
        pmvMatrix.glPopMatrix();
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(loc[0],loc[1],loc[2]);
        float x, z;

        //Berechnet um wie viel auf der X- und Z-Achse verschoben werden muss
        if(count <= degree) {
            z = (float)(Math.cos((-270-count) * Math.PI/180) * distancex);
            x = (float)(Math.sin((-270-count) * Math.PI/180) * distancex);
        }
        else {
            z = (float)(Math.cos((-270-degree) * Math.PI/180) * distancex);
            x = (float)(Math.sin((-270-degree) * Math.PI/180) * distancex);
        }

        pmvMatrix.glTranslatef(x*factor, distancey, z*factor);
    }
}
