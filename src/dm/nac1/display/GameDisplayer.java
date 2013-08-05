package dm.nac1.display;

import dm.nac1.Subscriber;
import dm.nac1.notifications.*;
import dm.nac1.players.Player;

public abstract class GameDisplayer implements Subscriber {
    protected MainMenu mainMenu;

    protected abstract void handleChangeFieldEvent(int lineNumber, int colNumber, int contents);

    public void handleEvent(Notification notification) {
        if (notification instanceof ChangeFieldNotification) {
            ChangeFieldNotification cfn = (ChangeFieldNotification)notification;
            handleChangeFieldEvent(cfn.lineNumber, cfn.colNumber, cfn.contents);
        }
    }

    public void setMainMenu(MainMenu menu) {
        mainMenu = menu;
    }

    public abstract void showMainMenu();
    public abstract void showField();
    public abstract void showMessage(String msg);
    public abstract void resetField();
}
