package pl.britenet.jug2019;

/**
 * README FIRST
 *
 * Test it on JDK 11u2, it will work on later updates with: -XX:-UseCountedLoopSafepoints
 */
class MathWithoutLock {
    static double calculateSomething(int iterations, double start) {
        for (int i = 0; i < iterations; i++) {
            start += StrictMath.pow(Math.exp(start), Math.sin(start));
        }
        return start;
    }

    public static void main(String[] args) throws Exception {
        Thread pingThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                System.out.println("ping");
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        for (int i = 0; i < 20_000; i++) {
            calculateSomething(i % 100, 1);
        }

        pingThread.start();

        System.out.println("Sleeping for 5s");
        Thread.sleep(5_000);

        System.out.println("Starting calculate");
        calculateSomething(400_000_000, 100);
        System.out.println("Calculate ended");
        System.out.println("Sleeping for 5s");
        Thread.sleep(5_000);
        System.out.println("Time to interrupt");
        pingThread.interrupt();
    }
}
