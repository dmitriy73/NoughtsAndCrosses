package dm.nac1.players;

import dm.nac1.Cell;
import dm.nac1.Common;
import dm.nac1.Common.Sides;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import dm.simpleJson.JsonParseException;

public class RemoteHumanPlayerOnClient extends RemotePlayer {
    private String server;
    private Socket socket;

    public RemoteHumanPlayerOnClient(Player localPlayer, String server) {
        super(localPlayer);
        this.server = server;
    }

    public Cell doMove() {
        if (out != null && in != null) {
            try {
                jsonParser.readAndParse(in);
                if (!jsonParser.contains(KEY_LINE) || !jsonParser.contains(KEY_COL)) {
                    System.err.println("Bad move info received from the server");
                    return null;
                }
                return new Cell(jsonParser.getAsInt(KEY_LINE), jsonParser.getAsInt(KEY_COL));
            }
            catch (JsonParseException e) {
                System.err.println(e.toString());
            }
            catch (IOException e) {
                System.err.println("Failed to get information from the server");
            }
        }
        return null;
    }

    public boolean init() {
        try {
            if (server.matches(Common.IP_ADDR_REG_EXP_MATCH)) {
                String[] sGroups = server.split("\\.", 4);
                byte[] groups = new byte[sGroups.length];
                for (int i = 0; i < sGroups.length; i++) {
                    try {
                        groups[i] = Byte.parseByte(sGroups[i]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Failed parsing of server ip address");
                        groups[i] = 0;
                    }
                }
                socket = new Socket(InetAddress.getByAddress(groups), Common.NET_PORT);
            } else {
                socket = new Socket(server, Common.NET_PORT);
            }
        }
        catch (IOException e) {
            System.err.println("Failed to connect to server");
            return false;
        }
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("Failed to get input/output streams for server connection");
            return false;
        }

        try {
            jsonParser.readAndParse(in);
            if (!jsonParser.contains(KEY_NAME) || !jsonParser.contains(KEY_SIDE)) {
                System.err.println("Received incorrect information from server");
                return false;
            }
            name = jsonParser.getAsString(KEY_NAME);
            side = (jsonParser.getAsString(KEY_SIDE).equalsIgnoreCase(Sides.toString(Sides.NOUGHTS))) ? Sides.NOUGHTS :
                                                                                                        Sides.CROSSES;
        }
        catch (IOException e) {
            System.err.println("Failed to get information from the server");
            return false;
        }
        catch (JsonParseException e) {
            System.err.println(e.toString());
            return false;
        }
        localPlayer.setSide(Sides.getOppositeSide(side));

        jsonBuilder.reset();
        jsonBuilder.add(KEY_NAME, localPlayer.getName());
        jsonBuilder.add(KEY_SIDE, Sides.toString(localPlayer.getSide()));
        jsonBuilder.write(out);
        return true;
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
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
