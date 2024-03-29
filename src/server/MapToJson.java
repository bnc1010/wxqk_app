package server;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class MapToJson {
    private static final String COMMA = ",";
    private static final String MARKS = "\"";
    private static final String COLON = ":";
    private static final String BRACKET_LEFT = "[";
    private static final String BRACKET_RIGHT = "]";
    private static final String BRACE_LEFT = "{";
    private static final String BRACE_RIGHT = "}";
    private static final String STR_VERSION_UID = "serialVersionUID";
    final StringBuilder json = new StringBuilder();
    public String write(Map<String, Object> jsonMap) {
        try {
            appendMap(jsonMap);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    private void appendMap(Map<String, Object> map) {
        json.append(BRACE_LEFT);
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            appendObj(key, map.get(key), keys.hasNext());
        }
        json.append(BRACE_RIGHT);
    }
    private void appendObj(String key, Object obj, boolean hasNext) {
        if (obj == null || STR_VERSION_UID.equals(key)) {
            return;
        }
        if (key != null) {
            json.append(MARKS).append(key).append(MARKS).append(COLON);
        }
        if (isNumber(obj)) {
            appendNum(obj);
        } else if (isString(obj)) {
            appendString(obj.toString());
        } else if (isArray(obj)) {
            appendArray(obj);
        } else if (isList(obj)) {
            appendList((List<?>) obj);
        } else if (isMap(obj)) {
            appendMap2((Map<?, ?>) obj);
        } else {
            appendBean(obj);
        }
        if (hasNext) {
            json.append(COMMA);
        }
    }
    private void appendBean(Object obj) {
        json.append(BRACE_LEFT);
        Map<String, Object> map = getBeanValue(obj);
        Iterator<String> keys = map.keySet().iterator();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            appendObj(key, map.get(key), keys.hasNext());
        }
        json.append(BRACE_RIGHT);
    }
    private static Map<String, Object> getBeanValue(Object obj) {
        Map<String, Object> map = new HashMap<>();
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            Object obj2;
            for (Field field : fields) {
                field.setAccessible(true);
                obj2 = field.get(obj);
                field.setAccessible(false);
                if (obj2 != null) {
                    map.put(field.getName(), obj2);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            //logger.error("getBeanValue faild", e);
        }
        return map;
    }
    private void appendMap2(Map<?, ?> map) {
        json.append(BRACKET_LEFT);
        Iterator<?> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            appendObj(key, map.get(key), keys.hasNext());
        }
        json.append(BRACKET_RIGHT);
    }
    private void appendList(List<?> list) {
        json.append(BRACKET_LEFT);
        int len = list.size();
        for (int i = 0; i < len; i++) {
            appendObj(null, list.get(i), i != (len - 1));
        }
        json.append(BRACKET_RIGHT);
    }
    private void appendArray(Object obj) {
        json.append(BRACKET_LEFT);
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            appendObj(null, Array.get(obj, i), i != (len - 1));
        }
        json.append(BRACKET_RIGHT);
    }
    private void appendNum(Object obj) {
        json.append(obj);
    }
    private void appendString(String str) {
        if (str.indexOf("\n") != -1) {
            str = str.replaceAll("\\n", "\\\\n");
        }
        if (str.indexOf("\t") != -1) {
            str = str.replaceAll("\\t", "\\\\t");
        }
        if (str.indexOf("\r") != -1) {
            str = str.replaceAll("\\r", "\\\\r");
        }
        json.append(MARKS).append(str).append(MARKS);
    }
    private static boolean isMap(Object obj) {
        return obj instanceof Map;
    }
    private static boolean isList(Object obj) {
        return obj instanceof List;
    }
    private static boolean isArray(Object obj) {
        return obj.getClass().isArray();
    }
    private static boolean isString(Object obj) {
        return obj instanceof CharSequence || obj instanceof Character;
    }
    private static boolean isNumber(Object obj) {
        return obj instanceof Integer || obj instanceof Boolean || obj instanceof Double || obj instanceof Long
                || obj instanceof Byte || obj instanceof Float || obj instanceof Short;
    }
}

