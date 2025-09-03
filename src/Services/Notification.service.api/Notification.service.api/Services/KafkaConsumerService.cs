using Confluent.Kafka;
using System.Text.Json;

public class KafkaConsumerService : BackgroundService
{
    private readonly ConsumerConfig _config;
    private readonly Dictionary<Guid, List<dynamic>> _userMessages;

    public KafkaConsumerService(ConsumerConfig config, Dictionary<Guid, List<dynamic>> userMessages)
    {
        _config = config;
        _userMessages = userMessages;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() => ConsumeLoop(stoppingToken), stoppingToken);
    }

    private void ConsumeLoop(CancellationToken ct)
    {
        using var consumer = new ConsumerBuilder<Ignore, string>(_config).Build();

        consumer.Subscribe(new[]
        {
            "event-created",
            "user-created",
            "order-successful",
            "order-cancelled",
            "order-failed",
            "payment-processed",
            "error"
        });

        while (!ct.IsCancellationRequested)
        {
            try
            {
                var cr = consumer.Consume(ct);

                Console.WriteLine($"[NotificationService] Consumed from {cr.Topic}: {cr.Message.Value}");

                // Deserialize JSON message into a dictionary
                var msg = JsonSerializer.Deserialize<Dictionary<string, JsonElement>>(cr.Message.Value);

                // Extract UserId (normalize various naming styles)
                Guid userId = Guid.Empty;

                if (msg != null)
                {
                    if (msg.TryGetValue("UserId", out var userIdElement) && userIdElement.ValueKind == JsonValueKind.String)
                    {
                        Guid.TryParse(userIdElement.GetString(), out userId);
                        Console.WriteLine($"[NotificationService] Extracted UserId: {userId}");
                    }
                    else if (msg.TryGetValue("Id", out var idElement) && idElement.ValueKind == JsonValueKind.String)
                    {
                        Guid.TryParse(idElement.GetString(), out userId);
                        Console.WriteLine($"[NotificationService] Extracted Id: {userId}");
                    }
                    else if (msg.TryGetValue("User_Id", out var user_IdElement) && user_IdElement.ValueKind == JsonValueKind.String)
                    {
                        Guid.TryParse(user_IdElement.GetString(), out userId);
                        Console.WriteLine($"[NotificationService] Extracted User_Id: {userId}");
                    }
                }


                var jsonOutput = new
                {
                    Topic = cr.Topic,
                    Message = cr.Message.Value,
                    UserId = userId,
                    Timestamp = DateTime.UtcNow
                };

                // Store user-specific messages
                if (userId != Guid.Empty)
                {
                    if (!_userMessages.ContainsKey(userId))
                        _userMessages[userId] = new List<dynamic>();

                    _userMessages[userId].Add(jsonOutput);
                }
                else
                {
                    // Store under a "global" user group for events without UserId
                    var globalId = Guid.Empty;
                    if (!_userMessages.ContainsKey(globalId))
                        _userMessages[globalId] = new List<dynamic>();

                    _userMessages[globalId].Add(jsonOutput);
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error consuming Kafka message: {ex}");
            }
        }
    }
}
