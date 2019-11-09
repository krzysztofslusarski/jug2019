package pl.britenet.jug2019.full;

public class ClassToOptimizeImpl implements ClassToOptimize{
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            new Object();
            doSomething(1 + (i % 10));
        }
    }

    private void doSomething(int o) {
        int acc = 0;
        for (int i = 0; i < o; i++) {
            acc = add(acc, i);
        }
    }

    private int add(int acc, int i) {
        return acc + sqrt(i);
    }

    private int sqrt(int i) {
        return (int) Math.sqrt(i);
    }
}
