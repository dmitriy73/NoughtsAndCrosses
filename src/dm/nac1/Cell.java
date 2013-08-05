package dm.nac1;

public class Cell {
    private static int minLineNumber;
    private static int maxLineNumber;
    private static int minColumnNumber;
    private static int maxColumnNumber;
    public final int lineNumber;
    public final int colNumber;

    public Cell(int lNumber, int cNumber) {
        lineNumber = lNumber;
        colNumber  = cNumber;
    }

    public static void setLimits(int lowLineLimit, int highLineLimit, int lowColLimit, int highColLimit) {
        minLineNumber   = lowLineLimit;
        maxLineNumber   = highLineLimit;
        minColumnNumber = lowColLimit;
        maxColumnNumber = highColLimit;
    }

    public static int getMinLineNumber() {
        return minLineNumber;
    }

    public static int getMaxLineNumber() {
        return maxLineNumber;
    }

    public static int getMinColumnNumber() {
        return minColumnNumber;
    }

    public static int getMaxColumnNumber() {
        return maxColumnNumber;
    }
}
