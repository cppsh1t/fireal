package fireal.util;

public class StringUtil {

    public static String lowerFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return str;
        }
        return Character.toLowerCase(firstChar) + str.substring(1);
    }

    public static String toUpperFirstChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return str;
        }
        return Character.toUpperCase(firstChar) + str.substring(1);
    }
}
