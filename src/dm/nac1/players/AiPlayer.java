package dm.nac1.players;

import dm.nac1.Cell;
import dm.nac1.Common;
import dm.nac1.Game;
import dm.nac1.Common.Sides;

public class AiPlayer extends Player {
    private static final String AI_NAME = "Computer";
    private static final int THINKING_IMITATION_SLEEP = 2000;
    private Game game;
    private boolean isFirstMove = true;

    public AiPlayer(Sides side, Game game) {
        super(AI_NAME, side);
        this.game = game;
    }

    public Cell doMove() {
        Cell cell;
        if (isFirstMove) {
            if (side == Sides.CROSSES) {
                cell =  new Cell(Common.CENTER_OF_FIELD, Common.CENTER_OF_FIELD);
            } else {
                if (game.getCellContent(Common.CENTER_OF_FIELD, Common.CENTER_OF_FIELD) == Common.SPACE) {
                    cell =  new Cell(Common.CENTER_OF_FIELD, Common.CENTER_OF_FIELD);
                } else {
                    cell = new Cell(Common.FIRST_DIM_NUM, Common.FIRST_DIM_NUM);
                }
            }
            isFirstMove = false;
        }  else {
            cell = game.searchCellToWin(side);
            if (cell == null) {
                cell = game.searchCellToWin(Sides.getOppositeSide(side));
                if (cell == null) {
                    // for now just search for any free cell
                    cell = game.findFirstFreeCell();
                }
            }
        }
        try {
            Thread.sleep(THINKING_IMITATION_SLEEP);
        }
        catch (InterruptedException e) {}
        return cell;
    }

    public boolean init() {
        return true;
    }

    public void free() {
        isFirstMove = true;
    }
}
