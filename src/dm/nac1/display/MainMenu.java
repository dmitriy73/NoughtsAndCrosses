package dm.nac1.display;

import java.util.ArrayList;

public class MainMenu {
    private ArrayList<MenuItem> items; //TODO: use Map instead
    private String header;

    public MainMenu(String h) {
        header = h;
        items = new ArrayList<MenuItem>();
    }

    public boolean addMenuItem(char cmdKey, String title) {
        for (MenuItem mi: items) {
            if (mi.getCmdKey() == cmdKey) {
                return false;
            }
        }

        MenuItem menuItem = new MenuItem(cmdKey, title);
        items.add(menuItem);
        return true;
    }

    public void deleteMenuItem(char cmdKey) {
        for (MenuItem mi: items) {
            if (mi.getCmdKey() == cmdKey) {
                items.remove(mi);
                break;
            }
        }
    }

    public int getSize() {
        return items.size();
    }

    public MenuItem getMenuItem(int itemNumber) {
        if (itemNumber < items.size()) {
            return items.get(itemNumber);
        } else {
            return null;
        }
    }

    public String getHeader() {
        return header;
    }
}
