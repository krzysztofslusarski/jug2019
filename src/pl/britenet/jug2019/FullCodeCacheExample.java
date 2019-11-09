package pl.britenet.jug2019;

import static java.lang.Thread.sleep;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import pl.britenet.jug2019.full.AddMathOperation;
import pl.britenet.jug2019.full.AnotherOptimizedClass;
import pl.britenet.jug2019.full.ClassToOptimize;
import pl.britenet.jug2019.full.ClassToOptimizeImpl;
import pl.britenet.jug2019.full.ClassToOptimizeInvocationHandler;

/**
 * README FIRST
 *
 * Test it on JDK 11u2 - on later updates it may not work.
 *
 * You can try use it with flags: -XX:+UseParallelGC -XX:NonProfiledCodeHeapSize=1M -XX:ProfiledCodeHeapSize=1M
 */
class FullCodeCacheExample {
    public static void main(String[] args) throws Exception {
        AnotherOptimizedClass optimizedClass = new AnotherOptimizedClass();

        AddMathOperation mathOperation = new AddMathOperation();
        System.out.println("[RED] BEGIN");
        optimizeClass("[RED] First 1:", optimizedClass, mathOperation, 1);
        optimizeClass("Next 1:", optimizedClass, mathOperation, 1);
        optimizeClass("Next 25000", optimizedClass, mathOperation, 25000);
        sleep(1000);
        optimizeClass("[RED] After optimization", optimizedClass, mathOperation, 25000);
        System.out.println("[GREEN] Filling code cache...");
        fillCodeCache(optimizedClass, mathOperation);
        System.out.println("[GREEN] Code cache filled?");
        optimizeClass("[RED] After code cache full", optimizedClass, mathOperation, 25000);
        System.out.println("[GREEN] Deoptimizing...");
        optimizedClass.run(null);
        System.out.println("[GREEN] Deoptimized...");
        optimizeClass("[RED] After deoptimization", optimizedClass, mathOperation, 25000);
        optimizeClass("[RED] After deoptimization x2", optimizedClass, mathOperation, 25000);
        System.out.println("END");
    }

    private static void fillCodeCache(AnotherOptimizedClass anotherOptimizedClass, AddMathOperation mathOperation) throws MalformedURLException {
        List<ClassToOptimize> usedClasses = new ArrayList<>();
        createMultipleClasses(usedClasses);
        for (int i = 0; i < 19000; i++) {
            for (ClassToOptimize classToOptimize : usedClasses) {
                anotherOptimizedClass.run(mathOperation);
                classToOptimize.run();
            }
        }
    }

    private static void createMultipleClasses(List<ClassToOptimize> usedClasses) throws MalformedURLException {
        for (int j = 0; j < 1000; j++) {
            String fictiousClassloaderJAR = "file:" + j + ".jar";
            URL[] fictiousClassloaderURL = new URL[]{new URL(fictiousClassloaderJAR)};
            URLClassLoader newClassLoader = new URLClassLoader(fictiousClassloaderURL);

            ClassToOptimize classToOptimize = (ClassToOptimize) Proxy.newProxyInstance(newClassLoader,
                    new Class<?>[]{ClassToOptimize.class},
                    new ClassToOptimizeInvocationHandler(new ClassToOptimizeImpl()));
            usedClasses.add(classToOptimize);
        }
    }

    private static void optimizeClass(String message, AnotherOptimizedClass anotherOptimizedClass, AddMathOperation mathOperation, int invCount) {
        long startTime = System.nanoTime();

        for (int i = 0; i < invCount; i++) {
            anotherOptimizedClass.run(mathOperation);
        }
        System.out.println(message + ", average: " + (System.nanoTime() - startTime) / invCount + "ns");
    }
}
