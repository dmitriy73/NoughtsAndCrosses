package dm.nac1.notifications;

public class ChangeFieldNotification extends Notification {
    public final int lineNumber;
    public final int colNumber;
    public final int contents;

    public ChangeFieldNotification(int lNumber, int cNumber, int cellContents) {
        lineNumber = lNumber;
        colNumber  = cNumber;
        contents   = cellContents;
    }
}
