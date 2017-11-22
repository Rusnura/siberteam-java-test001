package workers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Callable<Integer> {
    private final BlockingQueue<String> queueOfSymbols;
    private final ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap;

    public Consumer(BlockingQueue<String> q, ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap) {
        this.queueOfSymbols = q;
        this.countOfCharsMap = countOfCharsMap;
    }

    @Override
    public Integer call() throws Exception {
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        Integer symbolsCount = 0;
        String line;
        while (((line = queueOfSymbols.take()) != ExecutorWorker.POISON_PILL)) {
            /*
             * For each symbol:
             * if symbol isn't exists in map: add it with 1 count
             * else get symbol count from map and increment it
             */
            for (Character character: line.toCharArray()) {
                Integer value = map.get(character);
                if (value == null) {
                    map.put(character, 1);
                } else {
                    map.put(character, value + 1);
                }
            }
            symbolsCount += line.length();
        }

        // Current thread adding symbols to global HashMap
        for (Map.Entry<Character, Integer> entry: map.entrySet()) {
            Character symbol = entry.getKey();
            Integer count = entry.getValue();

            AtomicInteger symbolCount = countOfCharsMap.get(symbol);
            if (symbolCount == null) {
                AtomicInteger existingCount = countOfCharsMap.putIfAbsent(symbol, new AtomicInteger(count));
                if (existingCount != null) {
                    symbolCount = existingCount;
                    symbolCount.addAndGet(count);
                }
            } else {
                symbolCount.addAndGet(count);
            }
        }
        return symbolsCount;
    }
}
