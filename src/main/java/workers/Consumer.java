package workers;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private static final Logger log = Logger.getLogger(Consumer.class);
    private final BlockingQueue<String> queueOfSymbols;
    private final ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap;

    public Consumer(BlockingQueue<String> q, ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap) {
        this.queueOfSymbols = q;
        this.countOfCharsMap = countOfCharsMap;
    }

    @Override
    public void run() {
        String line;
        try {
            while (((line = queueOfSymbols.poll(100, TimeUnit.MILLISECONDS)) != null)) {
            /*
             * For each symbol:
             * if symbol isn't exists in map: add it with 1 count
             * else get symbol count from map and increment it
             */
                for (Character character : line.toCharArray()) {
                    AtomicInteger count = countOfCharsMap.get(character);
                    if (count == null) {
                        AtomicInteger existingCountValue = countOfCharsMap.putIfAbsent(character, new AtomicInteger(0));
                        if (existingCountValue != null) {
                            existingCountValue.incrementAndGet();
                        }
                    } else {
                        count.incrementAndGet();
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }
}
