# TornAPIKeyManager
Thread-safe key manager for multi-threaded Torn API requests

## Single Key Use (Default values)

```
ApiKey apiKey = new ApiKey("yourKeyHere"); // Cycle defined as 60 seconds, and maximum calls set to 100.

apiKey.use() // Returns String value. Is thread safe.
```

## Single Key Use (Modified values)

```
ApiKeyBuilder apiKeyBuilder = new ApiKeyBuilder("[yourApiKey]");
apiKeyBuilder.setSecondsPerCycle(30);
apiKeyBuilder.setMaximumCallsPerCycle(40);

ApiKey apiKey = apiKeyBuilder.build();

apiKey.use() // Returns String value. Is thread safe.
```

## Multiple Key Use

```
ApiKeyManager.addKey(new ApiKey("yourKeyHere"));
ApiKeyManager.addKey(new ApiKeyBuilder("otherKeyHere").setMaximumCallsPerCycle(30).build());

apiKeyManager.takeKeyPhrase() // Returns String value. Is thread safe.
```