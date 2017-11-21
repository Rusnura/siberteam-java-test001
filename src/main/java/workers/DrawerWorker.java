package workers;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DrawerWorker {
    public static void draw(ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap, long symbolsCount) {
        try {
            TreeMap<Character, AtomicInteger> sortedHashMap = new TreeMap<Character, AtomicInteger>(countOfCharsMap);
            // Loop gets entry from map
            for (Map.Entry<Character, AtomicInteger> entry : sortedHashMap.entrySet()) {
                char symbol = entry.getKey();
                int count = entry.getValue().get();
                // Calculate the percent of symbols: CountOfSymbol / TotalCountOfSymbolsInText * 100
                float frequency = (count / (float) symbolsCount) * 100;

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
            // If exception occurred: log it in logger
            System.err.println("Histogram drawing exception: " + e);
        }
    }
}
