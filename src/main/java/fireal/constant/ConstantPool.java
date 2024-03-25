package fireal.constant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConstantPool {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final Map<String, Object> map = new HashMap<>();

    public Collection<String> getKeys() {
        return map.keySet();
    }

    public void addItem(String key, Object value) {
        map.putIfAbsent(key, value);
    }

    public void addFile(String path) {
        InputStream inputStream = ConstantPool.class.getResourceAsStream(path);
        addFile(inputStream);
    }

    public void addFile(InputStream inputStream) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapOfFile = mapper.readValue(inputStream, Map.class);
            map.putAll(mapOfFile);
        } catch (MismatchedInputException ignored) {

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Object get(String name) {
        if (name.contains(".")) {
            return getByNames(name.split("\\."));
        } else {
            return map.get(name);
        }
    }

    @SuppressWarnings("rawtypes")
    private Object getByNames(String[] names) {
        Map currentMap = map;
        Object result = null;

        for (int i = 0; i < names.length; i++) {
            Object nextMap = currentMap.get(names[i]);

            if (i == names.length - 1) {
                result = nextMap;
                break;
            }

            if (nextMap instanceof Map) {
                currentMap = (Map) nextMap;
            } else {
                return null;
            }
        }

        return result;
    }

}
