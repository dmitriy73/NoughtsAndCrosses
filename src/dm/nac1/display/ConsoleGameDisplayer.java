package dm.nac1.display;

import dm.nac1.Common;
import dm.nac1.GameField;

public class ConsoleGameDisplayer extends GameDisplayer {
    private static final char SPACE_SYMBOL  = ' ';
    private static final char NOUGHT_SYMBOL = 'O';
    private static final char CROSS_SYMBOL  = 'X';
    private static final char MENU_SEPARATOR_SYMBOL = '=';

    private GameField<Character> gameField;

    public ConsoleGameDisplayer() {
        gameField = new GameField<Character>(new Character[Common.FIELD_DIM][Common.FIELD_DIM], Common.FIELD_DIM, SPACE_SYMBOL);
    }

    protected void handleChangeFieldEvent(int lineNumber, int colNumber, int contents) {
        switch (contents) {
            case Common.SPACE:
                gameField.setCellContent(lineNumber, colNumber, SPACE_SYMBOL);
                break;
            case Common.NOUGHT:
                gameField.setCellContent(lineNumber, colNumber, NOUGHT_SYMBOL);
                break;
            case Common.CROSS:
                gameField.setCellContent(lineNumber, colNumber, CROSS_SYMBOL);
                break;
        }
    }

    public void showMainMenu() {
        if (mainMenu != null) {
            int maxLen = 0;

            System.out.println();
            System.out.println(mainMenu.getHeader());

            for (int i = 0; i < mainMenu.getSize(); i++) {
                MenuItem mi = mainMenu.getMenuItem(i);
                String s = "(" + mi.getCmdKey() + ") - " + mi.getTitle();
                System.out.println(s);

                if (s.length() > maxLen) {
                    maxLen = s.length();
                }
            }
            char[] separator = new char[maxLen];
            for (int i = 0; i < maxLen; i++) {
                separator[i] = MENU_SEPARATOR_SYMBOL;
            }
            String strSeparator = new String(separator);
            System.out.println(strSeparator);
        }
    }

    public void showField() {
        System.out.println();
        System.out.print(" ");
        for (int j = 0; j < Common.FIELD_DIM; j++) {
            System.out.print(" " + j + " ");
        }
        System.out.println();
        for (int i = 0; i < Common.FIELD_DIM; i++) {
            System.out.print(i);
            for (int j = 0; j < Common.FIELD_DIM; j++) {
                System.out.print("[" + gameField.getCellContent(i, j) + "]");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void showMessage(String msg) {
        if (msg != null) {
            System.out.println(msg);
        } else {
            System.out.println();
        }
    }

    public void resetField() {
        gameField.reset();
    }
}
