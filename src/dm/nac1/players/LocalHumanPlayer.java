package dm.nac1.players;

import dm.nac1.Common.Sides;
import dm.nac1.Cell;
import dm.nac1.userinput.UserInputHelper;

public class LocalHumanPlayer extends Player {
    private UserInputHelper uiHelper;
    public LocalHumanPlayer(String n, Sides s, UserInputHelper uih) {
        super(n, s);
        uiHelper = uih;
    }
    public Cell doMove() {
        return uiHelper.readCell();
    }

    public boolean init() {
        return true;
    }

    public void free() {

    }
}
