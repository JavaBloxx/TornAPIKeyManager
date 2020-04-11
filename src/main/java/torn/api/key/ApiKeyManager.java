package torn.api.key;

import torn.api.exceptions.KeyInUseException;
import torn.api.exceptions.MaximumCallsReachedException;

import java.util.LinkedList;

public class ApiKeyManager {

    private static LinkedList<ApiKey> apiKeys = new LinkedList<>();

    public static String takeKeyPhrase()
    {
        while (true)
        for (ApiKey apiKey : apiKeys)
        {
            try
            {
                return apiKey.useKeyPhrase();
            }
            catch (MaximumCallsReachedException | KeyInUseException ignored)
            {}
        }
    }

    public static void addApiKey(ApiKey apiKey)
    {
        apiKeys.add(apiKey);
    }
}
