package workers;

import services.Indicator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorWorker {
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();
    private final BlockingQueue<String> queueOfSymbols = new ArrayBlockingQueue<String>(5);
    private final ConcurrentHashMap<Character, AtomicInteger> countOfCharsMap = new ConcurrentHashMap<Character, AtomicInteger>(); // ConcurrentHashMap: <Symbol, CountOfSymbols>
    private final String filePath;
    private final Indicator indicator = new Indicator();
    private final ExecutorService readerService = Executors.newSingleThreadExecutor();
    private final ExecutorService symbolsAnalyzer = Executors.newFixedThreadPool(ExecutorWorker.availableProcessors);

    public ExecutorWorker(String filename) {
        this.filePath = filename;
    }

    public ConcurrentHashMap<Character, AtomicInteger> getCountOfCharsMap() {
        return countOfCharsMap;
    }

    public void start() throws Exception {
        File file = new File(filePath);
        if (!file.canRead()) {
            throw new IOException("Can't opening file for reading!");
        }

        final Producer producer = new Producer(this.queueOfSymbols, this.filePath, this.indicator);
        readerService.submit(producer);

        final List<Future> workerList = new ArrayList<Future>();
        for (int i = 0; i < ExecutorWorker.availableProcessors; i++) {
            final Consumer consumer = new Consumer(this.queueOfSymbols, this.countOfCharsMap, this.indicator);
            Future worker = symbolsAnalyzer.submit(consumer);
            workerList.add(worker);
        }

        // Wait a end of work
        for (Future worker: workerList) {
            worker.get();
        }

        // Shutdown all thread's
        readerService.shutdown();
        symbolsAnalyzer.shutdown();
    }
}