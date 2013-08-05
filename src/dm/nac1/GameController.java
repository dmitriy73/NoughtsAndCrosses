package dm.nac1;

import dm.nac1.display.GameDisplayer;
import dm.nac1.display.MainMenu;
import dm.nac1.players.*;
import dm.nac1.userinput.UserInputHelper;
import dm.nac1.Common.Sides;
import dm.nac1.Game.MoveResult;
import java.io.IOException;

public class GameController{
    private static final char CMD_KEY_LOCAL         = '1';
    private static final char CMD_KEY_AI            = '2';
    private static final char CMD_KEY_REMOTE_SERVER = '3';
    private static final char CMD_KEY_REMOTE_CLIENT = '4';
    private static final char CMD_KEY_REPLAY        = '5';
    private static final char CMD_KEY_QUIT          = '7';

    private static final int NUM_OF_PLAYERS      = 2;
    private static final int MAIN_PLAYER_IND     = 0;
    private static final int OPPONENT_PLAYER_IND = 1;

    private enum GameType {
        UNASSIGNED, LOCAL_TO_LOCAL, LOCAL_TO_AI, NET_AS_SERVER, NET_AS_CLIENT
    }

    private Player[] players = new Player[NUM_OF_PLAYERS];
    private int nextPlayer;
    private Game game;
    private GameDisplayer gameDisplayer;
    private MainMenu mainMenu;
    private UserInputHelper uiHelper;
    private GameType oldGameType = GameType.UNASSIGNED;
    private GameType gameType    = GameType.UNASSIGNED;

    private class HistoryOneMoveProcessor implements Callback {
        private final int paramsNum         = 2;
        private final int moveNumParamInd   = 0;
        private final int sideParamInd      = 1;
        private final int lineColUnassigned = Integer.MAX_VALUE;
        //private final int pause = 2000;

        public void invoke(Object... params) {
            Sides nextMoveSide = Sides.UNASSIGNED;
            int nextMoveNum    = lineColUnassigned;

            if (params.length >= paramsNum) {
                if (params[moveNumParamInd] instanceof Integer) {
                    nextMoveNum = (Integer)params[moveNumParamInd];
                }
                if (params[sideParamInd] instanceof Sides) {
                    nextMoveSide = (Sides)params[sideParamInd];
                }
            }
            if (nextMoveNum  != lineColUnassigned &&
                nextMoveSide != Sides.UNASSIGNED) {
                gameDisplayer.showMessage(null);
                gameDisplayer.showMessage("Move #" + nextMoveNum + ": " + Sides.toString(nextMoveSide));
                gameDisplayer.showField();
                uiHelper.wait4EnterKey();
                /*try {
                    Thread.sleep(pause);
                }
                catch (InterruptedException e) {}*/
            }
        }
    }

    public GameController(Game g, GameDisplayer d, UserInputHelper uih) {
        game            = g;
        gameDisplayer   = d;
        uiHelper        = uih;

        mainMenu = new MainMenu("<---- Select game type ---->");
        mainMenu.addMenuItem(CMD_KEY_LOCAL, "Play against another local guy");
        mainMenu.addMenuItem(CMD_KEY_AI, "Play against computer");
        mainMenu.addMenuItem(CMD_KEY_REMOTE_SERVER, "Play against another remote guy as server");
        mainMenu.addMenuItem(CMD_KEY_REMOTE_CLIENT, "Play against another remote guy as client");
        mainMenu.addMenuItem(CMD_KEY_REPLAY, "Show replay for the last game");
        mainMenu.addMenuItem(CMD_KEY_QUIT, "Finish game");
    }

    public void run() throws IOException {
        gameDisplayer.setMainMenu(mainMenu);

main_loop:
        while (true) {
            gameDisplayer.showMainMenu();
            char cmdKey = uiHelper.readMenuItemKey();
            boolean isReady = false;

            switch (cmdKey) {
                case CMD_KEY_LOCAL:
                    gameType = GameType.LOCAL_TO_LOCAL;
                    cleanUpOpponent();
                    setupGameLocalAgainstLocal();
                    isReady = true;
                    break;
                case CMD_KEY_AI:
                    gameType = GameType.LOCAL_TO_AI;
                    cleanUpOpponent();
                    setupGameHumanAgainstAi();
                    isReady = true;
                    break;
                case CMD_KEY_REMOTE_SERVER:
                    gameType = GameType.NET_AS_SERVER;
                    cleanUpOpponent();
                    if (setupGameNetAsServer()) {
                        isReady = true;
                    }
                    break;
                case CMD_KEY_REMOTE_CLIENT:
                    gameType = GameType.NET_AS_CLIENT;
                    cleanUpOpponent();
                    if (setupGameNetAsClient()) {
                        isReady = true;
                    }
                    break;
                case CMD_KEY_REPLAY:
                    if (game.haveHistory()) {
                        gameDisplayer.showMessage("The last game history:");
                        gameDisplayer.resetField();
                        HistoryOneMoveProcessor oneMoveProcessor = new HistoryOneMoveProcessor();
                        game.replay(oneMoveProcessor);
                    } else {
                        gameDisplayer.showMessage("There is no game history yet");
                        gameDisplayer.showMessage(null);
                    }
                    break;
                case CMD_KEY_QUIT:
                    break main_loop;
                case '\0':
                    gameDisplayer.showMessage("Program doesn't have an input device, aborting");
                    break main_loop;
            }

            if (isReady) {
                runGame();
                oldGameType = gameType;
            }
            cleanUpPlayers();
        }
        gameDisplayer.showMessage("Goodbye!");
    }

    private void setupGameLocalAgainstLocal() {
        Sides side = Sides.UNASSIGNED;
        String name;

        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            if (players[i] == null) {
                do {
                    name = uiHelper.readString("Player "+ (i+1) + ", please enter your name");
                } while (name == null || name.isEmpty());
                if (side == Sides.UNASSIGNED) {
                    side = Sides.getRandomSide();
                } else {
                    side = Sides.getOppositeSide(side);
                }
                players[i] = new LocalHumanPlayer(name, side, uiHelper);
            } else {
                name = uiHelper.readString("Player "+ (i+1) + "[" + players[i].getName() + "], please enter your name");
                if (name != null && !name.isEmpty()) {
                    players[i].setName(name);
                }

                if (side == Sides.UNASSIGNED) {
                    side = Sides.getRandomSide();
                } else {
                    side = Sides.getOppositeSide(side);
                }
                players[i].setSide(side);
            }
            gameDisplayer.showMessage("Player " + players[i].getName() + " will play for " + Sides.toString(side));
            if (side == Sides.CROSSES) {
                nextPlayer = i;
            }
        }
    }

    private boolean setupGameNetAsServer() {
        Sides side;
        String name;
        // Set up local player
        if (players[MAIN_PLAYER_IND] == null) {
            do {
                name = uiHelper.readString("Local player, please enter your name");
            } while (name == null || name.isEmpty());
            side = Sides.getRandomSide();
            players[MAIN_PLAYER_IND] = new LocalHumanPlayer(name, side, uiHelper);
        } else {
            name = uiHelper.readString("Local player [" + players[MAIN_PLAYER_IND].getName() + "], please enter your name");
            if (name != null && !name.isEmpty()) {
                players[MAIN_PLAYER_IND].setName(name);
            }
            side = Sides.getRandomSide();
            players[MAIN_PLAYER_IND].setSide(side);
        }
        gameDisplayer.showMessage("Player " + players[MAIN_PLAYER_IND].getName() + " will play for " + Sides.toString(side));

        // Set up remote player
        gameDisplayer.showMessage("waiting for the client...");
        if (players[OPPONENT_PLAYER_IND] == null) {
            players[OPPONENT_PLAYER_IND] = new RemoteHumanPlayerOnServer(players[MAIN_PLAYER_IND]);
        }
        if (!players[OPPONENT_PLAYER_IND].init()) {
            return false;
        }
        gameDisplayer.showMessage("Player " + players[OPPONENT_PLAYER_IND].getName() +
                " will play for " + Sides.toString(players[OPPONENT_PLAYER_IND].getSide()));
        nextPlayer = (side == Sides.CROSSES) ? MAIN_PLAYER_IND : OPPONENT_PLAYER_IND;
        return true;
    }

    private boolean setupGameNetAsClient() {
        Sides side;
        String name;
        String server;

        do {
            server = uiHelper.readString("Enter server name or IP address");
        } while (server == null || server.isEmpty());

        // Set up local player
        if (players[MAIN_PLAYER_IND] == null) {
            do {
                name = uiHelper.readString("Local player, please enter your name");
            } while (name == null || name.isEmpty());
            side = Sides.UNASSIGNED;
            players[MAIN_PLAYER_IND] = new LocalHumanPlayer(name, side, uiHelper);
        } else {
            name = uiHelper.readString("Local player [" + players[MAIN_PLAYER_IND].getName() + "], please enter your name");
            if (name != null && !name.isEmpty()) {
                players[MAIN_PLAYER_IND].setName(name);
            }

        }

        // Set up remote player
        gameDisplayer.showMessage("waiting for the server (it should be started first)...");
        if (players[OPPONENT_PLAYER_IND] == null) {
            players[OPPONENT_PLAYER_IND] = new RemoteHumanPlayerOnClient(players[MAIN_PLAYER_IND], server);
        }
        if (!players[OPPONENT_PLAYER_IND].init()) {
            return false;
        }
        side = Sides.getOppositeSide(players[OPPONENT_PLAYER_IND].getSide());
        players[MAIN_PLAYER_IND].setSide(side);
        gameDisplayer.showMessage("Player " + players[MAIN_PLAYER_IND].getName() + " will play for " + Sides.toString(side));
        gameDisplayer.showMessage("Player " + players[OPPONENT_PLAYER_IND].getName() +
                " will play for " + Sides.toString(players[OPPONENT_PLAYER_IND].getSide()));
        nextPlayer = (side == Sides.CROSSES) ? MAIN_PLAYER_IND : OPPONENT_PLAYER_IND;
        return true;
    }

    private void setupGameHumanAgainstAi() {
        Sides side;
        String name;
        // Set up local player
        if (players[MAIN_PLAYER_IND] == null) {
            do {
                name = uiHelper.readString("Please enter your name");
            } while (name == null || name.isEmpty());
            side = Sides.getRandomSide();
            players[MAIN_PLAYER_IND] = new LocalHumanPlayer(name, side, uiHelper);
        } else {
            name = uiHelper.readString("[" + players[MAIN_PLAYER_IND].getName() + "], please enter new name or use existent");
            if (name != null && !name.isEmpty()) {
                players[MAIN_PLAYER_IND].setName(name);
            }
            side = Sides.getRandomSide();
            players[MAIN_PLAYER_IND].setSide(side);
        }
        gameDisplayer.showMessage("Player " + players[MAIN_PLAYER_IND].getName() + " will play for " + Sides.toString(side));

        if (players[OPPONENT_PLAYER_IND] == null) {
            players[OPPONENT_PLAYER_IND] = new AiPlayer(Sides.getOppositeSide(side), game);
        } else {
            players[OPPONENT_PLAYER_IND].setSide(Sides.getOppositeSide(side));
        }
        gameDisplayer.showMessage("Player " + players[OPPONENT_PLAYER_IND].getName() +
                " will play for " + Sides.toString(players[OPPONENT_PLAYER_IND].getSide()));
        gameType = GameType.LOCAL_TO_AI;
        nextPlayer = (side == Sides.CROSSES) ? MAIN_PLAYER_IND : OPPONENT_PLAYER_IND;
    }

    private Player getPlayerForSide(Sides side) {
        for (Player player : players) {
            if (player.getSide() == side) {
                return player;
            }
        }
        return null;
    }

    private void cleanUpPlayers() {
        for (Player player : players) {
            if (player != null) {
                player.free();
            }
        }
    }

    private void cleanUpOpponent() {
        if (oldGameType != GameType.UNASSIGNED && oldGameType != gameType) {
            if (players[OPPONENT_PLAYER_IND] != null) {
                players[OPPONENT_PLAYER_IND] = null;
            }
        }
    }

    private void runGame() {
        gameDisplayer.showMessage("Let's start the game!");
        gameDisplayer.resetField();
        gameDisplayer.showField();
        game.start();

        while(true) {
            Cell cell;
            MoveResult moveResult = MoveResult.INVALID_CELL;

            while(true) {
                gameDisplayer.showMessage("Player " + players[nextPlayer].getName() + " is playing now for " +
                                          Sides.toString(players[nextPlayer].getSide()));
                do {
                    cell = players[nextPlayer].doMove();
                    if (cell != null) {
                        moveResult = game.processMove(players[nextPlayer].getSide(), cell);
                        if (moveResult == MoveResult.INVALID_CELL) {
                            gameDisplayer.showMessage("Invalid cell is selected, try again");
                        } else if (moveResult == MoveResult.OCCUPIED_CELL) {
                            gameDisplayer.showMessage("Already occupied cell is selected, try again");
                        }
                    }
                } while (cell == null || moveResult == MoveResult.INVALID_CELL || moveResult == MoveResult.OCCUPIED_CELL);

                if (gameType == GameType.NET_AS_SERVER || gameType == GameType.NET_AS_CLIENT) {
                    if (players[nextPlayer] instanceof LocalHumanPlayer) {
                        RemotePlayer remotePlayer = (RemotePlayer)players[OPPONENT_PLAYER_IND];
                        remotePlayer.sendLocalMove(cell);
                    } else {
                        gameDisplayer.showMessage("Remote player has made his move");
                    }
                }

                gameDisplayer.showField();

                if (gameType == GameType.LOCAL_TO_AI && nextPlayer == MAIN_PLAYER_IND) {
                    if (uiHelper.requestForConfirm(players[nextPlayer].getName() + ", continue [y] or you want to select another cell [n]?")) {
                        break;
                    } else {
                        gameDisplayer.showMessage("Last move will be rolled back");
                        game.undoLastMove();
                        gameDisplayer.showField();
                    }
                } else {
                    break;
                }
            }

            if (moveResult == MoveResult.READY) {
                if (++nextPlayer >= NUM_OF_PLAYERS) {
                    nextPlayer = 0;
                }
            } else if (moveResult == MoveResult.GAME_OVER_DRAW) {
                gameDisplayer.showMessage("The game's finished. There is no winner.");
                break;
            } else if (moveResult == MoveResult.GAME_OVER_CROSSES_WIN) {
                Player winner = getPlayerForSide(Sides.CROSSES);
                gameDisplayer.showMessage("The game's finished. " + winner.getName() + " wins!");
                break;
            } else if (moveResult == MoveResult.GAME_OVER_NOUGHTS_WIN) {
                Player winner = getPlayerForSide(Sides.NOUGHTS);
                gameDisplayer.showMessage("The game's finished. " + winner.getName() + " wins!");
                break;
            }
        }
    }
}
