package dm.nac1;

public class GameField<T> {
    private T[][] field;
    private final int size;
    private final T defaultValue;

    public GameField(T[][] field, int size, T defaultValue) {
        this.field = field;
        this.size = size;
        this.defaultValue = defaultValue;
        reset();
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = defaultValue;
            }
        }
    }

    public boolean checkIndexes(int line, int column) {
        return (line >= 0 && line < size && column >=0 && column < size);
    }

    public void setCellContent(int line, int column, T content) {
        if (checkIndexes(line, column)) {
            field[line][column] = content;
        }
    }

    public T getCellContent(int line, int column) {
        T content = defaultValue;
        if (checkIndexes(line, column)) {
            content = field[line][column];
        }
        return content;
    }

    public boolean isCellOccupied(int line, int column) {
        boolean result = false;
        if (checkIndexes(line, column)) {
            result = field[line][column] != defaultValue;
        }
        return result;
    }

    public boolean isCellFree(int line, int column) {
        return !isCellOccupied(line, column);
    }
}
