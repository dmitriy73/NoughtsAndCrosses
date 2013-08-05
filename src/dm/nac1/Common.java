package dm.nac1;

public class Common {
    public static final int FIELD_DIM       = 3;
    public static final int FIRST_DIM_NUM   = 0;
    public static final int LAST_DIM_NUM    = 2;
    public static final int FIELD_CELLS_NUM = FIELD_DIM * FIELD_DIM;
    public static final int CENTER_OF_FIELD = 1;

    public static final int SPACE  = 0;
    public static final int NOUGHT = -1;
    public static final int CROSS  = 1;

    public static final int NET_PORT = 37777;
    public static final String IP_ADDR_REG_EXP_MATCH =
        "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    public enum Sides {
        UNASSIGNED, NOUGHTS, CROSSES;

        public static Sides getRandomSide() {
            long t = Math.round(Math.random()+1);
            return (t == 1) ? NOUGHTS : CROSSES;
        }

        public static Sides getOppositeSide(Sides side) {
            if (side == UNASSIGNED) {
                return UNASSIGNED;
            } else {
                return (side == NOUGHTS) ? CROSSES : NOUGHTS;
            }
        }

        public static String toString(Sides side) {
            switch (side) {
                case NOUGHTS:
                    return "Noughts";
                case CROSSES:
                    return "Crosses";
                default:
                    return "Unassigned";
            }
        }
    }
}
