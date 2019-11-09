package pl.britenet.jug2019;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * README FIRST
 *
 * - interpreter mode (flag -Xint) you should get 4 results
 * 1 - IntegersHolder{value1=1, value2=1, value3=1}
 * 966466 - IntegersHolder{value1=1000, value2=1000, value3=1000}
 * 2 - IntegersHolder{value1=1000, value2=1, value3=1}
 * 3 - IntegersHolder{value1=1000, value2=1000, value3=1}
 *
 * - with JIT (no additional flags) you should get 2 results
 * 15008 - IntegersHolder{value1=1000, value2=1, value3=1000}
 * 57518676 - IntegersHolder{value1=1000, value2=1000, value3=1000}
 *
 *  If you get less results you should increase last sleep:
 *  Thread.sleep(5000);
 *
 *  Test it on JDK 11
 */
class MemoryCoherence {
    private static class IntegerHolder {
        int value = 0;
    }

    private static class IntegersHolder {
        int value1 = 0;
        int value2 = 0;
        int value3 = 0;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegersHolder that = (IntegersHolder) o;
            return value1 == that.value1 &&
                    value2 == that.value2 &&
                    value3 == that.value3;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value1, value2, value3);
        }

        @Override
        public String toString() {
            return "IntegersHolder{" +
                    "value1=" + value1 +
                    ", value2=" + value2 +
                    ", value3=" + value3 +
                    '}';
        }
    }

    private static IntegerHolder integerHolder = new IntegerHolder();
    private static IntegerHolder secondHolder = integerHolder;

    public static void main(String[] args) throws InterruptedException {
        Map<IntegersHolder, Long> count = new ConcurrentHashMap<>();

        Thread writer = new Thread(() -> {
            while (!Thread.interrupted()) {
                integerHolder.value = 1;
            }
        });

        Thread reader = new Thread(() -> {
            while (!Thread.interrupted()) {
                IntegersHolder integersHolder = new IntegersHolder();
                integerHolder.value = 1000;
                integersHolder.value1 = integerHolder.value;
                integersHolder.value2 = secondHolder.value;
                integersHolder.value3 = integerHolder.value;

                Long value = count.computeIfAbsent(integersHolder, key -> 0L) + 1;
                count.put(integersHolder, value);
            }
        });

        reader.start();
        writer.start();

        Thread.sleep(1000);
        count.clear();
        Thread.sleep(5000);

        reader.interrupt();
        writer.interrupt();

        for (Map.Entry<IntegersHolder, Long> entry : count.entrySet()) {
            System.out.println(entry.getValue() + " - " + entry.getKey());
        }
    }
}
