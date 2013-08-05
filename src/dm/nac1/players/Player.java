package dm.nac1.players;

import dm.nac1.Common.Sides;
import dm.nac1.Cell;

public abstract class Player {
    protected String name;
    protected Sides side;

    protected Player() {
        side = Sides.UNASSIGNED;
    }

    protected Player(String n, Sides s) {
        name = n;
        side = s;
    }

    public String getName() {
        return name;
    }

    public Sides getSide() {
        return side;
    }

    public void setName(String n) {
        name = n;
    }

    public void setSide(Sides s) {
        side = s;
    }

    public abstract Cell doMove();
    public abstract boolean init();
    public abstract void free();
}
