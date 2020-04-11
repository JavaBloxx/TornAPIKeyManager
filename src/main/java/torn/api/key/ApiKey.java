package torn.api.key;

import torn.api.exceptions.KeyInUseException;
import torn.api.exceptions.MaximumCallsReachedException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ApiKey {

    private Lock lock = new ReentrantLock();
    private volatile AtomicInteger locksInCurrentCycle;

    private final String keyPhrase;
    private volatile AtomicInteger callsMadeInCurrentCycle;

    private int maximumCallsPerCycle;
    private int secondsPerCycle;

    private ScheduledExecutorService executorService = null;

    ApiKey(String keyPhrase) {
        this.keyPhrase = keyPhrase;

        locksInCurrentCycle = new AtomicInteger(0);
        callsMadeInCurrentCycle = new AtomicInteger(0);
    }

    public String useKeyPhrase() throws MaximumCallsReachedException, KeyInUseException {
        if (lock.tryLock())
        {
            try
            {
                locksInCurrentCycle.incrementAndGet();

                if (keyIsAvailable())
                {
                    if (executorService == null) startCycle();
                    System.out.println(keyPhrase + " current calls: " + callsMadeInCurrentCycle.incrementAndGet());
                    return keyPhrase;
                }
                else
                {
                    throw new MaximumCallsReachedException("Maximum Api calls reached with key: " + keyPhrase);
                }
            }
            finally
            {
                lock.unlock();
            }
        }
        else
        {
            throw new KeyInUseException("Api key is in use by another thread");
        }
    }

    public String viewKeyPhrase() {
        return keyPhrase;
    }

    private boolean keyIsAvailable() {
        return callsMadeInCurrentCycle.get() < maximumCallsPerCycle;
    }

    private void startCycle() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new CycleResetter(), secondsPerCycle, TimeUnit.SECONDS);
    }

    // Package Only Setters

    void setMaximumCallsPerCycle(int maximumCallsPerCycle) {
        this.maximumCallsPerCycle = maximumCallsPerCycle;
    }

    void setSecondsPerCycle(int secondsPerCycle) {
        this.secondsPerCycle = secondsPerCycle;
    }

    private class CycleResetter implements Runnable {

        public void run() {
            executorService.shutdown();
            executorService = null;

            System.out.println(keyPhrase + " cycle reset");
            callsMadeInCurrentCycle.set(0);
        }
    }

    @Override
    public String toString() {
        return "ApiKey{" +
                "locksInCurrentCycle=" + locksInCurrentCycle +
                ", keyPhrase='" + keyPhrase + '\'' +
                ", callsMadeInCurrentCycle=" + callsMadeInCurrentCycle +
                ", maximumCallsPerCycle=" + maximumCallsPerCycle +
                ", secondsPerCycle=" + secondsPerCycle +
                '}';
    }
}
