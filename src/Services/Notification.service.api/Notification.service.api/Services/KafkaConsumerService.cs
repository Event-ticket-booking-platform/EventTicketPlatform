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
        => Task.Run(() => ConsumeLoop(stoppingToken), stoppingToken);

    private void ConsumeLoop(CancellationToken ct)
    {
        // CHANGE: capture Kafka key too
        using var consumer = new ConsumerBuilder<string, string>(_config).Build();

        consumer.Subscribe(new[]
        {
            "event-created",
            "user-created",
            "order.created",
            "order.successful",
            "order.cancelled",
            "ticketReserve.error",
            "order.failed",
            "paymentProcessing.failed",
            "ticketCancel.failed",
            "ticketCancel.error",
            "orderCancelling.error",
            "orderAlready.cancelled",
            "error",
            "payment.processed",
            "order.error",
            "orderCancel.error",
            "orderCancel.failed",
            "orderCancel.successful",
            "ticket.reserved",
            "ticket.expired"
        });

        while (!ct.IsCancellationRequested)
        {
            try
            {
                var cr = consumer.Consume(ct);
                Console.WriteLine($"[NotificationService] Consumed from {cr.Topic}: {cr.Message.Value}");

                Guid userId = Guid.Empty;

                // 1) Try JSON extraction with more keys (OrganizerId)
                try
                {
                    using var doc = JsonDocument.Parse(cr.Message.Value);
                    var root = doc.RootElement;

                    string? Extract(string name)
                        => root.TryGetProperty(name, out var el) && el.ValueKind == JsonValueKind.String
                           ? el.GetString()
                           : null;

                    // Candidate keys in priority order
                    var candidates = new[]
                    {
                        "UserId", "OrganizerId", "Id", "User_Id", "CustomerId", "OwnerId"
                    };

                    foreach (var key in candidates)
                    {
                        var val = Extract(key);
                        if (!string.IsNullOrWhiteSpace(val) && Guid.TryParse(val, out var g))
                        {
                            userId = g;
                            Console.WriteLine($"[NotificationService] Extracted {key}: {userId}");
                            break;
                        }
                    }
                }
                catch (JsonException)
                {
                    Console.WriteLine("[NotificationService] Non-JSON message, storing as plain string");
                }

                // 2) Fallback: try Kafka message key as GUID
                if (userId == Guid.Empty && !string.IsNullOrWhiteSpace(cr.Message.Key))
                {
                    if (Guid.TryParse(cr.Message.Key, out var keyGuid))
                    {
                        userId = keyGuid;
                        Console.WriteLine($"[NotificationService] Using Kafka key as userId: {userId}");
                    }
                }

                var jsonOutput = new
                {
                    Topic = cr.Topic,
                    Message = cr.Message.Value,
                    UserId = userId,
                    Timestamp = DateTime.UtcNow,
                    IsJson = true
                };

                var bucket = userId != Guid.Empty ? userId : Guid.Empty;
                if (!_userMessages.ContainsKey(bucket))
                    _userMessages[bucket] = new List<dynamic>();

                _userMessages[bucket].Add(jsonOutput);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error consuming Kafka message: {ex}");
            }
        }
    }
}
