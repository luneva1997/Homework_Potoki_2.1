import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();



    public static void main(String[] args){
        final ExecutorService threads = Executors.newFixedThreadPool(1000);

        Thread lieder = new Thread(() -> {
            while(!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.isEmpty()) {
                        try  {
                            sizeToFreq.wait();
                            Map.Entry<Integer, Integer> maxEntry = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue());
                            System.out.println("Самое частое количество повторений " + maxEntry.getKey() + " (встретилось " + maxEntry.getValue() + " раз)");
                        } catch (InterruptedException e) {
                            return;
                        }
                    } else {
                        System.out.println("Список пустой!");
                    }
                }
            }
        });

        lieder.start();

        for (int j=0; j < 1000; j++) {
            char [] answer;
            int sumR = 0;

            try {
                Future<String> test = threads.submit(() -> generateRoute("RLRFR", 100));
                answer = test.get().toCharArray();

                for (int i=0; i < 100; i++) {
                    if (answer[i] == 'R') {
                        sumR += 1;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                return;
            }

            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(sumR)) {
                    sizeToFreq.replace(sumR, sizeToFreq.get(sumR) + 1);
                    sizeToFreq.notify();
                } else {
                    sizeToFreq.put(sumR, 1);
                    sizeToFreq.notify();
                }

            }


        }


        threads.shutdown();
        lieder.interrupt();

        Map<Integer, Integer> sortedSizeToFreQ = new TreeMap<>(sizeToFreq);

        for (Map.Entry<Integer,Integer> e: sortedSizeToFreQ.entrySet()) {
            System.out.println("- " + e.getKey() + " (" + e.getValue() + " раз)");
        }

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i=0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
