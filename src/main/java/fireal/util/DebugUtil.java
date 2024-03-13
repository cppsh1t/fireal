package fireal.util;

public class DebugUtil {

    public static void log(Class<?> currentClass, int line, String str) {
        System.out.println("[" + currentClass.getName() + ", " + line + "]: " + str);
    }

    public static void log(Class<?> currentClass, int line, Object str) {
        System.out.println("[" + currentClass.getName() + ", " + line + "]: " + str.toString());
    }
}
