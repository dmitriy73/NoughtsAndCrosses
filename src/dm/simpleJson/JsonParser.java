package dm.simpleJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class JsonParser {
    private static final int NUM_PARTS_IN_GROUP = 2;
    private static final int KEY_INDEX = 0;
    private static final int VAL_INDEX = 1;
    private HashMap<String, Object> groups = new HashMap<String, Object>();

    public JsonParser() {

    }

    public JsonParser(String s) throws JsonParseException {
        parse(s);
    }

    public JsonParser(BufferedReader reader) throws JsonParseException, IOException {
        readAndParse(reader);
    }

    public void parse(String s) throws JsonParseException {
        groups.clear();

        int offset;
        StringBuilder sb = new StringBuilder(s);
        if (sb.charAt(0) != JsonDefs.OPENING_SYMBOL) {
            throw new JsonParseException(0);
        }
        sb.deleteCharAt(0);
        if (sb.charAt(sb.length()-1) != JsonDefs.CLOSING_SYMBOL) {
            throw new JsonParseException(sb.length()-1);
        }
        sb.deleteCharAt(sb.length()-1);
        offset = 1;

        String[] sGroups = sb.toString().split(Character.toString(JsonDefs.GROUP_DELIMITER_SYMBOL));
        for (String aGroup : sGroups) {
            String[] kv = aGroup.split(Character.toString(JsonDefs.KV_DELIMITER_SYMBOL));

            if (kv.length != NUM_PARTS_IN_GROUP) {
                throw new JsonParseException(offset);
            }
            String key = kv[KEY_INDEX];
            if (key.charAt(0) != JsonDefs.ENCLOSING_SYMBOL) {
                throw new JsonParseException(offset);
            }
            if (key.charAt(key.length()-1) != JsonDefs.ENCLOSING_SYMBOL) {
                throw new JsonParseException(offset + key.length() - 1);
            }
            offset += key.length();
            key = key.substring(1, key.length()-1);

            String value = kv[VAL_INDEX];
            if (value.charAt(0) == JsonDefs.ENCLOSING_SYMBOL) {
                if (value.charAt(value.length()-1) != JsonDefs.ENCLOSING_SYMBOL) {
                    throw new JsonParseException(offset + value.length() - 1);
                }
                offset += value.length();
                value = value.substring(1, value.length()-1);
                groups.put(key, value);
            } else if (value.charAt(0) == JsonDefs.OPENING_SYMBOL) {
                throw new JsonParseException("Complex values aren't supported yet", offset);
            } else {
                throw new JsonParseException(offset);
            }

        }
    }

    public void readAndParse(BufferedReader reader) throws JsonParseException, IOException {
        parse(reader.readLine());
    }

    public boolean contains(String key) {
        return groups.containsKey(key);
    }

    public String getAsString(String key) {
        Object o = groups.get(key);
        if (o != null && o instanceof String) {
            return (String)o;
        }
        return null;
    }

    public int getAsInt(String key) {
        int res = 0;
        String s = getAsString(key);
        if (s != null) {
            try {
                res = Integer.parseInt(s);
            }
            catch (NumberFormatException e) {
                res = 0;
            }
        }
        return res;
    }
}
