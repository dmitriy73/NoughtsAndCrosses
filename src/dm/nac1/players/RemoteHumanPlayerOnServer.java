package dm.nac1.players;

import dm.nac1.Cell;
import dm.nac1.Common;
import dm.simpleJson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteHumanPlayerOnServer extends RemotePlayer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public RemoteHumanPlayerOnServer(Player localPlayer) {
        super(localPlayer);
    }

    public Cell doMove() {
        if (out != null && in != null) {
            try {
                jsonParser.readAndParse(in);
                if (!jsonParser.contains(KEY_LINE) || !jsonParser.contains(KEY_COL)) {
                    System.err.println("Bad move info received from the client");
                    return null;
                }
                return new Cell(jsonParser.getAsInt(KEY_LINE), jsonParser.getAsInt(KEY_COL));
            }
            catch (JsonParseException e) {
                System.err.println(e.toString());
            }
            catch (IOException e) {
                System.err.println("Failed to get information from the client");
            }
        }
        return null;
    }

    public boolean init() {
        try {
            serverSocket = new ServerSocket(Common.NET_PORT);
        }
        catch (IOException e) {
            System.err.println("Failed to create server socket");
            return false;
        }
        try {
            clientSocket = serverSocket.accept();
        }
        catch (IOException e) {
            System.err.println("Failed to accept a client");
            return false;
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("Failed to get input/output streams for the client");
            return false;
        }

        jsonBuilder.reset();
        jsonBuilder.add(KEY_NAME, localPlayer.getName());
        jsonBuilder.add(KEY_SIDE, Common.Sides.toString(localPlayer.getSide()));
        jsonBuilder.write(out);

        try {
            jsonParser.readAndParse(in);
            if (jsonParser.contains(KEY_NAME)) {
                name = jsonParser.getAsString(KEY_NAME);
                side = Common.Sides.getOppositeSide(localPlayer.getSide());
                return true;
            } else {
                System.err.println("Bad player info received from the client");
                return false;
            }
        }
        catch (IOException e) {
            System.err.println("Failed to get information from the client");
            return false;
        }
        catch (JsonParseException e) {
            System.err.println("Bad player info received from the client");
            return false;
        }
    }

    public void free() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
