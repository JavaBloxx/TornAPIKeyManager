package torn.api.key;

public class ApiKeyBuilder
{

    private ApiKey apiKey;

    public ApiKeyBuilder(String keyPhase)
    {
        apiKey = new ApiKey(keyPhase);
    }

    public ApiKeyBuilder setSecondsPerCycle(int seconds)
    {
        apiKey.setSecondsPerCycle(seconds);
        return this;
    }

    public ApiKeyBuilder setMaximumCallsPerCycle(int maximumCalls)
    {
        apiKey.setMaximumCallsPerCycle(maximumCalls);
        return this;
    }

    public ApiKey build()
    {
        return apiKey;
    }
}
