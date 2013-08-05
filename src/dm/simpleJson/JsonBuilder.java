package dm.simpleJson;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonBuilder {
    private HashMap<String, Object> groups = new HashMap<String, Object>();

    private void addPair(String key, String value) {
        if (!groups.containsKey(key)) {
            groups.put(key, value);
        }
    }

    public void add(String key, String value) {
        addPair(key, value);
    }

    public void add(String key, Integer value) {
        addPair(key, value.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(JsonDefs.OPENING_SYMBOL);
        for (Map.Entry<String, Object> entry : groups.entrySet()) {
            sb.append(JsonDefs.ENCLOSING_SYMBOL);
            sb.append(entry.getKey());
            sb.append(JsonDefs.ENCLOSING_SYMBOL);
            sb.append(JsonDefs.KV_DELIMITER_SYMBOL);

            if (entry.getValue() instanceof String) {
                sb.append(JsonDefs.ENCLOSING_SYMBOL);
                sb.append(entry.getValue());
                sb.append(JsonDefs.ENCLOSING_SYMBOL);
            } else {
                sb.append(JsonDefs.ENCLOSING_SYMBOL);
                sb.append("Complex values aren't supported yet");
                sb.append(JsonDefs.ENCLOSING_SYMBOL);
            }
            sb.append(JsonDefs.GROUP_DELIMITER_SYMBOL);
        }
        //remove last unnecessary group delimiter
        sb.deleteCharAt(sb.length()-1);
        sb.append(JsonDefs.CLOSING_SYMBOL);
        return sb.toString();
    }

    public void write(PrintWriter writer) {
        if (writer != null) {
            writer.println(this.toString());
        }
    }

    public void reset() {
        groups.clear();
    }
}
