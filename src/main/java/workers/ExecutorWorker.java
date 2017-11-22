package workers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorWorker {
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();
    public static final String POISON_PILL = new String(); // Add it to queue end
    private final BlockingQueue<String> queueOfSymbols = new ArrayBlockingQueue<String>(10);
    private final ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap = new ConcurrentHashMap<Character, AtomicInteger>(); // ConcurrentHashMap: <Symbol, CountOfSymbols>
    private final String filePath;
    private final ExecutorService readerService = Executors.newSingleThreadExecutor();
    private final ExecutorService symbolsAnalyzer = Executors.newFixedThreadPool(ExecutorWorker.availableProcessors);
    private long symbolsCount = 0;

    public ExecutorWorker(String filename) {
        this.filePath = filename;
    }

    public long getSymbolsCount() {
        return symbolsCount;
    }

    public ConcurrentHashMap<Character, AtomicInteger> getCountOfCharsMap() {
        return countOfCharsMap;
    }

    public void start() throws Exception {
        File file = new File(filePath);
        if (!file.canRead()) {
            throw new IOException("Can't opening file for reading!");
        }
        final Producer producer = new Producer(this.queueOfSymbols, this.filePath);
        final List<Future<Integer>> workerList = new ArrayList<Future<Integer>>();
        readerService.submit(producer);

        for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
            final Consumer consumer = new Consumer(this.queueOfSymbols, this.countOfCharsMap);
            Future<Integer> worker = symbolsAnalyzer.submit(consumer);
            workerList.add(worker);
        }

        for (Future<Integer> future: workerList) {
            symbolsCount += future.get();
        }
        readerService.shutdown();
        symbolsAnalyzer.shutdown();
    }
}