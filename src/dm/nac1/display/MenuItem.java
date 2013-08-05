package dm.nac1.display;

public class MenuItem {
    private char cmdKey;
    private String title;

    public MenuItem(char key, String t) {
        cmdKey = key;
        title  = t;
    }

    public char getCmdKey() {
        return cmdKey;
    }

    public void setCmdKey(char key) {
        cmdKey = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }
}
