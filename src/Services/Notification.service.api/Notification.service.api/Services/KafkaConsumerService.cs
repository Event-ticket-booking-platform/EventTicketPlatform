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
            "order.created",
            "order.successful",
            "order.cancelled",
            "ticketReserve.error",
            "order-failed",
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
                Dictionary<string, JsonElement>? msg = null;

                // Try to parse as JSON first
                try
                {
                    msg = JsonSerializer.Deserialize<Dictionary<string, JsonElement>>(cr.Message.Value);

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
                }
                catch (JsonException)
                {
                    // Not a JSON payload → fallback to raw string
                    Console.WriteLine("[NotificationService] Non-JSON message, storing as plain string");
                }

                var jsonOutput = new
                {
                    Topic = cr.Topic,
                    Message = cr.Message.Value,
                    UserId = userId,
                    Timestamp = DateTime.UtcNow,
                    IsJson = msg != null
                };

                // Store user-specific messages
                var key = userId != Guid.Empty ? userId : Guid.Empty;
                if (!_userMessages.ContainsKey(key))
                    _userMessages[key] = new List<dynamic>();

                _userMessages[key].Add(jsonOutput);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error consuming Kafka message: {ex}");
            }
        }
    }
}
