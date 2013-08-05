package dm.nac1;

import dm.nac1.notifications.*;
import dm.nac1.Common.Sides;

import java.util.ArrayList;

public class Game extends AbstractPublisher {
    public static final int FULL_CHAIN_OF_NOUGHTS = -3;
    public static final int FULL_CHAIN_OF_CROSSES = 3;
    public static final int ALMOST_FULL_CHAIN_OF_NOUGHTS = -2;
    public static final int ALMOST_FULL_CHAIN_OF_CROSSES = 2;
    public static final int MIN_OCCUPIED_CELLS_TO_CHECK_WIN = 3;

    private enum GameResult {
        DRAW, NOUGHTS_WIN, CROSSES_WIN
    }

    private class MoveInfo {
        final Sides side;
        final int   line;
        final int   column;

        MoveInfo(Sides side, int line, int column) {
            this.side   = side;
            this.line   = line;
            this.column = column;
        }
    }

    private GameField<Integer> gameField;
    private int occupiedCellsCount;
    private GameResult gameResult = GameResult.DRAW;
    private ArrayList<MoveInfo> history;

    public enum MoveResult {
        READY,
        INVALID_CELL,
        OCCUPIED_CELL,
        GAME_OVER_DRAW,
        GAME_OVER_NOUGHTS_WIN,
        GAME_OVER_CROSSES_WIN
    }

    public Game() {
        gameField = new GameField<Integer>(new Integer[Common.FIELD_DIM][Common.FIELD_DIM], Common.FIELD_DIM, Common.SPACE);
        history   = new ArrayList<MoveInfo>();
    }

    public void start() {
        gameField.reset();
        history.clear();
        occupiedCellsCount = 0;
    }

    public int getCellContent(int lineNumber, int colNumber) {
        return gameField.getCellContent(lineNumber, colNumber);
    }

    public MoveResult processMove(Sides side, Cell cell) {
        MoveResult result = MoveResult.READY;

        if (!gameField.checkIndexes(cell.lineNumber, cell.colNumber)) {
            return MoveResult.INVALID_CELL;
        }
        if (gameField.isCellOccupied(cell.lineNumber, cell.colNumber)) {
            return MoveResult.OCCUPIED_CELL;
        }

        int contents = (side == Sides.NOUGHTS) ? Common.NOUGHT : Common.CROSS;
        gameField.setCellContent(cell.lineNumber, cell.colNumber, contents);
        occupiedCellsCount++;

        ChangeFieldNotification cfn = new ChangeFieldNotification(cell.lineNumber, cell.colNumber, contents);
        notifySubscribers(cfn);

        if (checkGameEnd()) {
            switch (gameResult) {
                case NOUGHTS_WIN:
                    result = MoveResult.GAME_OVER_NOUGHTS_WIN;
                    break;
                case CROSSES_WIN:
                    result = MoveResult.GAME_OVER_CROSSES_WIN;
                    break;
                default:
                    result = MoveResult.GAME_OVER_DRAW;
            }
        }
        history.add(new MoveInfo(side, cell.lineNumber, cell.colNumber));
        return result;
    }

    private boolean checkChainSum(int chainSum) {
        boolean result = true;

        if (chainSum == FULL_CHAIN_OF_NOUGHTS) {
            gameResult = GameResult.NOUGHTS_WIN;
        } else if (chainSum == FULL_CHAIN_OF_CROSSES) {
            gameResult = GameResult.CROSSES_WIN;
        } else {
            result = false;
        }
        return result;
    }

    private boolean checkGameEnd() {
        int chainSum;
        // Check columns
        for (int line = 0; line < Common.FIELD_DIM; line++) {
            chainSum = 0;
            for (int col = 0; col < Common.FIELD_DIM; col++) {
                chainSum += gameField.getCellContent(line, col);
            }
            if (checkChainSum(chainSum)) {
                return true;
            }
        }
        // Check lines
        for (int col = 0; col < Common.FIELD_DIM; col++) {
            chainSum = 0;
            for (int line = 0; line < Common.FIELD_DIM; line++) {
                chainSum += gameField.getCellContent(line, col);
            }
            if (checkChainSum(chainSum)) {
                return true;
            }
        }
        // Check diagonals
        chainSum = 0;
        for (int i = 0; i < Common.FIELD_DIM; i++) {
            chainSum += gameField.getCellContent(i, i);
        }
        if (checkChainSum(chainSum)) {
            return true;
        }
        chainSum = 0;
        for (int line = 0, col = Common.FIELD_DIM-1; line < Common.FIELD_DIM; line++, col--) {
            chainSum += gameField.getCellContent(line, col);
        }
        if (checkChainSum(chainSum)) {
            return true;
        }
        //Check draw
        if (occupiedCellsCount == Common.FIELD_CELLS_NUM) {
            gameResult = GameResult.DRAW;
            return true;
        }
        return false;
    }

    public Cell searchCellToWin(Sides side) {
        int chainSumToFind = side == Sides.CROSSES ? ALMOST_FULL_CHAIN_OF_CROSSES : ALMOST_FULL_CHAIN_OF_NOUGHTS;
        int chainSum;

        if (occupiedCellsCount < MIN_OCCUPIED_CELLS_TO_CHECK_WIN) {
            return null;
        }

        // Check lines
        for (int line = 0; line < Common.FIELD_DIM; line++) {
            chainSum = 0;
            int spareColumn = 0;
            for (int col = 0; col < Common.FIELD_DIM; col++) {
                chainSum += gameField.getCellContent(line, col);
                if (gameField.isCellFree(line, col)) {
                    spareColumn = col;
                }
            }
            if (chainSum == chainSumToFind) {
                return new Cell(line, spareColumn);
            }
        }
        // Check columns
        for (int col = 0; col < Common.FIELD_DIM; col++) {
            chainSum = 0;
            int spareLine = 0;
            for (int line = 0; line < Common.FIELD_DIM; line++) {
                chainSum += gameField.getCellContent(line, col);
                if (gameField.isCellFree(line, col)) {
                    spareLine = line;
                }
            }
            if (chainSum == chainSumToFind) {
                return new Cell(spareLine, col);
            }
        }
        // Check diagonals
        chainSum = 0;
        int spareIndex = 0;
        for (int i = 0; i < Common.FIELD_DIM; i++) {
            chainSum += gameField.getCellContent(i, i);
            if (gameField.isCellFree(i, i)) {
                spareIndex = i;
            }
        }
        if (chainSum == chainSumToFind) {
            return new Cell(spareIndex, spareIndex);
        }

        chainSum = 0;
        int spareLine = 0;
        int spareCol  = 0;
        for (int i = 0, j = Common.FIELD_DIM-1; i < Common.FIELD_DIM; i++, j--) {
            chainSum += gameField.getCellContent(i, j);
            if (gameField.isCellFree(i, j)) {
                spareLine = i;
                spareCol  = j;
            }
        }
        if (chainSum == chainSumToFind) {
            return new Cell(spareLine, spareCol);
        }
        return null;
    }

    public Cell findFirstFreeCell() {
        for (int line = 0; line < Common.FIELD_DIM; line++) {
            for (int col = 0; col < Common.FIELD_DIM; col++) {
                if (gameField.isCellFree(line, col)) {
                    return new Cell(line, col);
                }
            }
        }
        return null;
    }

    public void undoLastMove() {
        if (occupiedCellsCount > 0) {
            int lastIndex = history.size()-1;
            MoveInfo moveInfo = history.get(lastIndex);
            gameField.setCellContent(moveInfo.line, moveInfo.column, Common.SPACE);
            occupiedCellsCount--;

            ChangeFieldNotification cfn = new ChangeFieldNotification(moveInfo.line, moveInfo.column, Common.SPACE);
            notifySubscribers(cfn);

            history.remove(lastIndex);
        }
    }

    public void replay(Callback cbk) {
        if (cbk != null) {
            int moveCount = 1;
            for (MoveInfo moveInfo : history) {
                int contents = (moveInfo.side == Sides.NOUGHTS) ? Common.NOUGHT : Common.CROSS;
                ChangeFieldNotification cfn = new ChangeFieldNotification(moveInfo.line, moveInfo.column, contents);
                notifySubscribers(cfn);
                cbk.invoke(moveCount++, moveInfo.side);
            }
        }
    }

    public boolean haveHistory() {
        return !history.isEmpty();
    }
}
