package services;

import java.util.concurrent.atomic.AtomicBoolean;

public class Indicator {
    private AtomicBoolean isDone = new AtomicBoolean(false);

    public void setIsDone() {
        this.isDone.set(true);
    }

    public AtomicBoolean getIsDone() {
        return this.isDone;
    }
}
