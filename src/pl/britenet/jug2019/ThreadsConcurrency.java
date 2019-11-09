package pl.britenet.jug2019;

/**
 * README FIRST
 *
 * To see compilations use flag:-XX:+PrintCompilation
 *
 * With:
 * Thread.sleep(1) - program should work and end
 * Thread.sleep(100) - program should run forever
 *
 * Test it on JDK 11
 */
class ThreadsConcurrency {
    public static void main(String[] args) {
        new ThreadsConcurrency().run();
    }

    private boolean stopThread = false;

    private void run() {
        new Thread(() -> {
            try {
                System.out.println("Start");
                Thread.sleep(1);
                System.out.println("Setting to true");
                stopThread = true;
                System.out.println("Done");
            } catch (InterruptedException ignored) {
            }
        }).start();

        new Thread(() -> {
            int i = 0;
            while (!stopThread) {
                square(i++);
            }
            System.out.println(i);
        }).start();
    }

    private int square(int i) {
        return i * i;
    }
}
