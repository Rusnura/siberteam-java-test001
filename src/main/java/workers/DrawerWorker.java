package workers;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DrawerWorker {
    private static final Logger log = Logger.getLogger(DrawerWorker.class);

    public static void draw(ConcurrentMap<Character, AtomicInteger> countOfCharsMap) {
        try {
            final TreeMap<Character, AtomicInteger> sortedHashMap = new TreeMap<Character, AtomicInteger>(countOfCharsMap);
            // Calculate the total symbols count
            long totalSymbolsCount = 0;
            for (Map.Entry<Character, AtomicInteger> entry : sortedHashMap.entrySet()) {
                totalSymbolsCount += entry.getValue().longValue();
            }

            // Loop gets entry from map
            for (Map.Entry<Character, AtomicInteger> entry : sortedHashMap.entrySet()) {
                char symbol = entry.getKey();
                int count = entry.getValue().get();
                // Calculate the percent of symbols: CountOfSymbol / TotalCountOfSymbolsInText * 100
                float frequency = (count / (float) totalSymbolsCount) * 100;

                // Calculate sharp (#) count: frequency / 2
                // Maximum count of sharps: 50
                // Minimum count of sharps: 1
                int sharpsCount = (int) Math.ceil(frequency / 2);

                // Draw it
                System.out.print(symbol + " (" + String.format("%.2f", frequency) + "%)\t");
                for (int i = 0; i < sharpsCount; i++) {
                    System.out.print('#');
                }

                // Print a new line for next loop iteration
                System.out.println("");
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
