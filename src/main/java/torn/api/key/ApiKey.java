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

    private final String keyPhrase;

    private Lock lock = new ReentrantLock(); // Holds thread locking information

    private volatile AtomicInteger locksInCurrentCycle; // Times threads have tried to access the key in current cycle

    private volatile AtomicInteger callsMadeInCurrentCycle; // Times the key has been used in current cycle

    private int maximumCallsPerCycle; // Maximum allowable calls per cycle
    private int secondsPerCycle; // Duration of a cycle

    private ScheduledExecutorService executorService = null;

    ApiKey(String keyPhrase)
    {
        this.keyPhrase = keyPhrase;

        maximumCallsPerCycle = 100;
        secondsPerCycle = 60;

        locksInCurrentCycle = new AtomicInteger(0);
        callsMadeInCurrentCycle = new AtomicInteger(0);
    }

    /**
     * Obtain key phrase to use for querying the Torn API
     * @return the key phrase
     * @throws MaximumCallsReachedException maximum calls have been reached until the next cycle begins
     * @throws KeyInUseException another thread is currently accessing the key
     */
    public String use() throws MaximumCallsReachedException, KeyInUseException
    {
        if (lock.tryLock()) // If not locked then lock for thread use
        {
            try
            {
                locksInCurrentCycle.incrementAndGet();

                if (keyIsAvailable()) // Return key
                {
                    if (executorService == null) startCycle();
                    return keyPhrase;
                }
                else // Maximum calls have been reached
                {
                    throw new MaximumCallsReachedException("Maximum Api calls reached with key: " + keyPhrase);
                }
            }
            finally // Unlock the key for use
            {
                lock.unlock();
            }
        }
        else
        {
            throw new KeyInUseException("Api key is in use by another thread");
        }
    }

    /**
     * Check if maximum calls is greater than calls made in the current cycle
     * @return boolean
     */
    private boolean keyIsAvailable()
    {
        return callsMadeInCurrentCycle.get() < maximumCallsPerCycle;
    }

    /**
     * Start the key reset cycle
     */
    private void startCycle()
    {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new CycleResetter(), secondsPerCycle, TimeUnit.SECONDS);
    }

    @Override
    public String toString()
    {
        return "ApiKey{" +
                "locksInCurrentCycle=" + locksInCurrentCycle +
                ", keyPhrase='" + keyPhrase + '\'' +
                ", callsMadeInCurrentCycle=" + callsMadeInCurrentCycle +
                ", maximumCallsPerCycle=" + maximumCallsPerCycle +
                ", secondsPerCycle=" + secondsPerCycle +
                '}';
    }

    // Package Only Setters (Used with ApiKeyBuilder)

    void setMaximumCallsPerCycle(int maximumCallsPerCycle)
    {
        this.maximumCallsPerCycle = maximumCallsPerCycle;
    }

    void setSecondsPerCycle(int secondsPerCycle)
    {
        this.secondsPerCycle = secondsPerCycle;
    }

    /**
     * Runs from the point the first call is made within the cycle
     * Resets calls made at the end of the defined cycle
     */
    private class CycleResetter implements Runnable
    {
        public void run() {
            executorService.shutdown();
            executorService = null;
            locksInCurrentCycle.set(0);
            callsMadeInCurrentCycle.set(0);
        }
    }

}
