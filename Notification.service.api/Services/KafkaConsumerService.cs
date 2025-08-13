using Confluent.Kafka;
using System.Text.Json;

public class KafkaConsumerService : BackgroundService
{
    private readonly ConsumerConfig _config;
    private readonly List<object> _messages;

    public KafkaConsumerService(ConsumerConfig config, List<object> messages)
    {
        _config = config;
        _messages = messages;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() => ConsumeLoop(stoppingToken), stoppingToken);
    }

    private void ConsumeLoop(CancellationToken ct)
    {
        using var consumer = new ConsumerBuilder<Ignore, string>(_config)
            .SetErrorHandler((_, e) => Console.WriteLine($"Kafka error: {e}"))
            .Build();

        // Subscribe to all topics produced by your services
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

                var jsonOutput = new
                {
                    Topic = cr.Topic,
                    Message = cr.Message.Value,
                    Partition = cr.Partition.Value,
                    Offset = cr.Offset.Value,
                    Timestamp = DateTime.UtcNow
                };

                _messages.Add(jsonOutput);

                Console.WriteLine($"[NotificationService] Received from {cr.Topic}: {cr.Message.Value}");
            }
            catch (ConsumeException ex)
            {
                Console.Error.WriteLine($"Consume error: {ex.Error.Reason}");
                _messages.Add(new { Error = ex.Error.Reason, Timestamp = DateTime.UtcNow });
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Unexpected error: {ex}");
                _messages.Add(new { Error = ex.Message, Timestamp = DateTime.UtcNow });
            }
        }
    }
}
