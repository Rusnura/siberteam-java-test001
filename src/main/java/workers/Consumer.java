package workers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Callable<AtomicInteger> {
    private final BlockingQueue<String> queueOfSymbols;
    private ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap;
    private AtomicInteger symbolsCount = new AtomicInteger(0);

    public Consumer(BlockingQueue<String> q, ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap) {
        this.queueOfSymbols = q;
        this.countOfCharsMap = countOfCharsMap;
    }

    public AtomicInteger call() throws Exception {
        try {
            String line;
            while (((line = queueOfSymbols.take()) != ExecutorWorker.POISON_PILL)) {
                /*
                 * For each symbol:
                 * if symbol isn't exists in map: add it with 1 count
                 * else get symbol count from map and increment it
                 */
                for (Character character: line.toCharArray()) {
                    AtomicInteger value = countOfCharsMap.get(character);
                    if (value == null) {
                        AtomicInteger existingValue = countOfCharsMap.putIfAbsent(character, new AtomicInteger(1));
                        if (existingValue != null) {
                            value = existingValue;
                            value.incrementAndGet();
                        }
                    } else {
                        value.incrementAndGet();
                    }
                }
                symbolsCount.getAndAdd(line.length());
            }
            return symbolsCount;
        } catch (InterruptedException e) {
            System.err.println("InterruptedException: " + e);
        }
        return null;
    }
}