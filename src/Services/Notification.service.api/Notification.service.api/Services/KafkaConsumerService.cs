using Confluent.Kafka;
using Confluent.Kafka.Admin;
using System.Text.Json;

public class KafkaConsumerService : BackgroundService
{
    private readonly ConsumerConfig _config;
    private readonly List<object> _messages;

    // List of topics to subscribe and auto-create
    private readonly string[] _topics = new[]
    {
        "event.created",
        "user.created",
        "order.successful",
        "order.cancelled",
        "order.failed",
        "payment.processed",
        "error"
    };

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
        // Step 1: Auto-create topics (development/testing only)
        using (var adminClient = new AdminClientBuilder(new AdminClientConfig
        {
            BootstrapServers = _config.BootstrapServers
        }).Build())
        {
            foreach (var topic in _topics)
            {
                try
                {
                    adminClient.CreateTopicsAsync(new TopicSpecification[]
                    {
                        new TopicSpecification { Name = topic, NumPartitions = 1, ReplicationFactor = 1 }
                    }).Wait();
                    Console.WriteLine($"[NotificationService] Topic created: {topic}");
                }
                catch (CreateTopicsException e) when (e.Results[0].Error.Code == Confluent.Kafka.ErrorCode.TopicAlreadyExists)
                {
                    Console.WriteLine($"[NotificationService] Topic already exists: {topic}");
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[NotificationService] Failed to create topic {topic}: {ex.Message}");
                }
            }
        }

        // Step 2: Start consuming
        using var consumer = new ConsumerBuilder<Ignore, string>(_config)
            .SetErrorHandler((_, e) => Console.WriteLine($"Kafka error: {e}"))
            .Build();

        consumer.Subscribe(_topics);

        Console.WriteLine("[NotificationService] Kafka consumer started.");

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

        consumer.Close();
    }
}
