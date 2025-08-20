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
            "event.created",
            "user.created",
            "order.successful",
            "order.cancelled",
            "order.failed",
            "payment.processed",
            "error"
        });

        while (!ct.IsCancellationRequested)
        {
            try
            {
                var cr = consumer.Consume(ct);

                // Deserialize to dynamic object
                var msg = JsonSerializer.Deserialize<Dictionary<string, object>>(cr.Message.Value);

                // Try to get UserId from payload
                Guid userId = Guid.Empty;
                if (msg != null && msg.ContainsKey("UserId"))
                {
                    Guid.TryParse(msg["UserId"]?.ToString(), out userId);
                }

                var jsonOutput = new
                {
                    Topic = cr.Topic,
                    Message = cr.Message.Value,
                    UserId = userId,
                    Timestamp = DateTime.UtcNow
                };

                if (userId != Guid.Empty)
                {
                    if (!_userMessages.ContainsKey(userId))
                        _userMessages[userId] = new List<dynamic>();

                    _userMessages[userId].Add(jsonOutput);
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error consuming Kafka message: {ex}");
            }
        }
    }
}
