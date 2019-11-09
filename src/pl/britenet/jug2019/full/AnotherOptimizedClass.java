package pl.britenet.jug2019.full;

public class AnotherOptimizedClass {
    public int run(MathOperation mathOperation) {
        if (mathOperation != null) {
            int acc = 0;
            for (int i = 0; i < 1000; i++) {
                acc += doSomething(1 + (i % 10), mathOperation);
            }
            return acc;
        } else {
            return 0;
        }
    }

    private int doSomething(int o, MathOperation mathOperation) {
        int acc = 0;
        for (int i = 0; i < o; i++) {
            acc = mathOperation.doMath(o, i);
        }
        return acc;
    }
}
