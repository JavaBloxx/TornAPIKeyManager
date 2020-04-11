package torn.api.key;

import torn.api.exceptions.KeyInUseException;
import torn.api.exceptions.MaximumCallsReachedException;

import java.util.LinkedList;

public class ApiKeyManager {

    private LinkedList<ApiKey> apiKeys = new LinkedList<>();

    public String getAnyAvailableKey()
    {
        while (true)
        for (ApiKey apiKey : apiKeys)
        {
            try
            {
                return apiKey.use();
            }
            catch (MaximumCallsReachedException | KeyInUseException ignored)
            {}
        }
    }

    public void addApiKey(ApiKey apiKey)
    {
        apiKeys.add(apiKey);
    }
}
