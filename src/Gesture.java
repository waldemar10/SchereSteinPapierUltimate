package src;

public class Gesture {

    private final String name;
    private final int minLines;
    private final int maxLines;
    private final double minCircularity;
    private final double maxCircularity;
    private final double minArcHull;
    private final double maxArcHull;

    public Gesture(String name, int minLines, int maxLines, double minCircularity,
                   double maxCircularity, double minArcHull, double maxArcHull) {
        this.name = name;
        this.minLines = minLines;
        this.maxLines = maxLines;
        this.minCircularity = minCircularity;
        this.maxCircularity = maxCircularity;
        this.minArcHull = minArcHull;
        this.maxArcHull = maxArcHull;
    }
    public boolean isDetectedStone(int indexLine, double circularity,double arcHull){
        return indexLine >= minLines && indexLine <= maxLines
                && circularity >= minCircularity && circularity <= maxCircularity
                && arcHull >= minArcHull && arcHull <= maxArcHull;
    }
    public boolean isDetectedPaper(int indexLine, double circularity,double arcHull){
        return indexLine > minLines && indexLine < maxLines ||
                indexLine < minLines && circularity <= minCircularity && arcHull >= minArcHull;
    }
    public boolean isDetectedScissor(int indexLine, double circularity){
        return indexLine >= minLines && indexLine <= maxLines && circularity > minCircularity && circularity < maxCircularity;
    }

    public String getName(){
        return name;
    }
}
