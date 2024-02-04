package from.to;

public class BuggyClassLoader extends ClassLoader {
    static String PRE = "my.";

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith(PRE)) {
            name = name.substring(PRE.length());
        }
        return super.loadClass(name, resolve);
    }
}
