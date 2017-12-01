package services;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IDone {
    AtomicBoolean isDone = new AtomicBoolean(false);

    AtomicBoolean getIsDone();
}
