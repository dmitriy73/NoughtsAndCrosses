package dm.nac1.players;

import dm.nac1.Cell;
import java.io.BufferedReader;
import java.io.PrintWriter;
import dm.simpleJson.*;

public abstract class RemotePlayer extends Player {
    protected final String KEY_NAME = "name";
    protected final String KEY_SIDE = "side";
    protected final String KEY_LINE = "line";
    protected final String KEY_COL  = "column";
    protected Player localPlayer;
    protected PrintWriter out;
    protected BufferedReader in;
    protected JsonBuilder jsonBuilder;
    protected JsonParser jsonParser;

    public RemotePlayer(Player localPlayer) {
        super();
        this.localPlayer = localPlayer;
        this.jsonBuilder = new JsonBuilder();
        this.jsonParser  = new JsonParser();
    }

    public void sendLocalMove(Cell cell) {
        if (out != null) {
            jsonBuilder.reset();
            jsonBuilder.add(KEY_LINE, cell.lineNumber);
            jsonBuilder.add(KEY_COL, cell.colNumber);
            jsonBuilder.write(out);
        }
    }
}
