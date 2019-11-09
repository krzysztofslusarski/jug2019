package pl.britenet.jug2019;

/**
 * README FIRST
 *
 * Test it on JDK 11
 */
class ImplicitNullCheckBaaad {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            try {
                execute(null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[GREEN] Optimizing...");
        checkNulls();
        System.out.println("[GREEN] Optimized...");
        Thread.sleep(1000);
        for (int i = 0; i < 15; i++) {
            try {
                execute(null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkNulls() {
        Object[] objects = new Object[100_000];
        for (int i = 0; i < 100_000; i++) {
            objects[i] = Math.random() > 0.9 ? null : new Object();
        }
        for (Object object : objects) {
            try {
                execute(object);
            } catch (RuntimeException e) {
            }
        }
    }

    private static int execute(Object object) {
        return object.hashCode();
    }
}
