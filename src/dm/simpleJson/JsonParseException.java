package dm.simpleJson;


public class JsonParseException extends Exception {
    private static final String defaultMsg = "JSON parsing error";
    private int errorOffset;

    public JsonParseException(String msg, int errorOffset) {
        super(msg);
        this.errorOffset = errorOffset;
    }

    public JsonParseException(int errorOffset) {
        super(defaultMsg);
        this.errorOffset = errorOffset;
    }

    public int getErrorOffset() {
        return errorOffset;
    }

    public String toString() {
        return getMessage() + " occurred in position " + errorOffset;
    }
}
