package dm.nac1.userinput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import dm.nac1.Cell;

public class ConsoleUserInputHelper implements UserInputHelper {
    private BufferedReader reader;

    public ConsoleUserInputHelper() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public Cell readCell() {
        Cell cell = null;

        if (reader != null) {
            String s;
            int colN, lineN;
            s = readLine("Enter cell coordinates you wish to seize (line column)");
            String[] coords = s.split(" ");

            if (coords.length < 2) {
                System.out.println("Wrong format detected, please enter your coordinates in format 'line column' like 0 0");
                return null;
            }
            try {
                lineN = Integer.parseInt(coords[0]);
            }
            catch (NumberFormatException e) {
                System.out.println("Wrong format for line number detected");
                return null;
            }
            try {
                colN = Integer.parseInt(coords[1]);
            }
            catch (NumberFormatException e) {
                System.out.println("Wrong format for column number detected");
                return null;
            }
            if (colN  < Cell.getMinColumnNumber() || colN  > Cell.getMaxColumnNumber() ||
                lineN < Cell.getMinLineNumber()   || lineN > Cell.getMaxLineNumber()) {
                System.err.println("Use values from ranges [" + Cell.getMinLineNumber() + " - " +
                                                                Cell.getMaxLineNumber() + "] for lines and [" +
                                                                Cell.getMinColumnNumber() + " - " +
                                                                Cell.getMaxColumnNumber() + "] for columns");
            } else {
                cell = new Cell(lineN, colN);
            }
        }
        return cell;
    }

    public char readMenuItemKey() throws IOException {
        char choice = '\0';

        do {
            System.out.println();
            System.out.print("Please make your choice: ");
            try {
                choice = (char)System.in.read();
            }
            catch (Exception e) {
                System.err.println("Failed to read from the console");
                return choice;
            }
        } while(choice == '\n' | choice == '\r');
        System.in.skip(System.in.available());
        return choice;
    }

    public String readString(String prompt) {
        return readLine(prompt);
    }

    public boolean requestForConfirm(String prompt) {
        while(true) {
            String s = readLine(prompt);
            if (s.equalsIgnoreCase("Y")) {
                return true;
            } else if (s.equalsIgnoreCase("N")) {
                return false;
            }
        }
    }

    public void wait4EnterKey()
    {
        System.out.println("Press Enter to continue");
        try {
            System.in.read();
        }
        catch (Exception e) {}
    }

    private String readLine(String prompt) {
        String s;
        try {
            System.out.print(prompt + " : ");
            s = reader.readLine();
        }
        catch (Exception e) {
            System.err.println("Failed to read from the console");
            return null;
        }
        return s;
    }
}
