package from.to;

import java.lang.reflect.Method;

/**
 * # 现象
 * 1. 自定义类加载器, 类加载器中修改类名后使用 AppClassLoader 进行加载
 * 2. 使用 ClassLoader.loadClass() 可以加载, 使用 Class.forName() 不能加载 (ClassNotFoundException)
 *
 * # 分析
 * Class.forName 会调用 ClassLoader.loadClass 来加载类, 但有更多的检查
 * 其中:
 * 检查已加载的类和需要被加载的类的类名是否相同, 不相同则返回 nullptr
 * 见 jvm 的源码:
 * SystemDictionary::load_instance_class_impl

 // For user defined Java class loaders, check that the name returned is
 // the same as that requested.  This check is done for the bootstrap
 // loader when parsing the class file.
 if (class_name == k->name()) {
 return k;
 }
 *  k: 已加载的类文件
 *  class_name: 需加载的 class 名
 *
 * # 结论
 * 使用自定义加载类, 通过 Class.forName() 方式来加载, 不能修改类名
 */

public class App {
    public static void main(String[] args) throws Exception {
        String className = "my.from.to.LoadedTarget";
        BuggyClassLoader buggyClassLoader = new BuggyClassLoader();
        call_ClassLoader_LoadClass(className, buggyClassLoader);
        call_Class_ForName(className, buggyClassLoader);
    }

    static void call_Class_ForName(String className, ClassLoader classLoader) {
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            invokeMethod(clazz);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    static void call_ClassLoader_LoadClass(String className, ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            invokeMethod(clazz);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    static void invokeMethod(Class<?> clazz) throws Exception {
        Object object = clazz.newInstance();
        Method method = object.getClass().getMethod("sayHi");
        Object ret = method.invoke(object);
        // print call method
        System.out.println(Thread.currentThread().getStackTrace()[2] + ": " + ret);
    }
}
