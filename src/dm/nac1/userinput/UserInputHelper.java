package dm.nac1.userinput;

import dm.nac1.Cell;
import java.io.IOException;

public interface UserInputHelper {
    public Cell readCell();
    public char readMenuItemKey() throws IOException;
    public String readString(String prompt);
    public boolean requestForConfirm(String prompt);
    public void wait4EnterKey();
}
