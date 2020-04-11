package torn.api.key;

import torn.api.exceptions.KeyInUseException;
import torn.api.exceptions.MaximumCallsReachedException;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApiKeyManager {

    private static LinkedList<ApiKey> apiKeys = new LinkedList<>();
    private static BlockingQueue<String> availableKeyPhrases = new LinkedBlockingQueue<>();

    public static String takeKeyPhrase()
    {
        while (true)
        for (ApiKey apiKey : apiKeys) {
            try
            {
                return apiKey.useKeyPhrase();
            }
            catch (MaximumCallsReachedException | KeyInUseException ignored)
            {}
        }
    }

    public static void addApiKey(ApiKey apiKey) {
        apiKeys.add(apiKey);
    }
}
