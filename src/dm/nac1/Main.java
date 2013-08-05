package dm.nac1;

import dm.nac1.display.ConsoleGameDisplayer;
import dm.nac1.userinput.ConsoleUserInputHelper;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Cell.setLimits(Common.FIRST_DIM_NUM, Common.LAST_DIM_NUM, Common.FIRST_DIM_NUM, Common.LAST_DIM_NUM);
        Game game = new Game();
        ConsoleUserInputHelper conUiHelper = new ConsoleUserInputHelper();
        ConsoleGameDisplayer gameDisplayer = new ConsoleGameDisplayer();
        game.addSubscriber(gameDisplayer);
        GameController gameController = new GameController(game, gameDisplayer, conUiHelper);

        gameController.run();
    }
}
