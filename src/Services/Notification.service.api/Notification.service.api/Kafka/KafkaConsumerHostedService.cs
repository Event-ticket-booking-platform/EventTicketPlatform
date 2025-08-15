using Confluent.Kafka;
using Notification.service.api.Handlers;
using Notification.service.api.Models;
using System.Text.Json;

namespace Notification.service.api.Kafka
{
    public class KafkaConsumerHostedService: BackgroundService
    {
        private readonly IConfiguration _config;
        private readonly IServiceProvider _services;
        private readonly ILogger<KafkaConsumerHostedService> _logger;

        // Topics we listen to
        private readonly string[] _topics = new[] { "order_created", "payment_status_changed", "event_updated", "event_canceled" };

        public KafkaConsumerHostedService(IConfiguration config, IServiceProvider services, ILogger<KafkaConsumerHostedService> logger)
        {
            _config = config;
            _services = services;
            _logger = logger;
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            // Fire-and-forget worker
            Task.Run(() => StartConsumerLoop(stoppingToken), stoppingToken);
            return Task.CompletedTask;
        }

        private void StartConsumerLoop(CancellationToken stoppingToken)
        {
            var bootstrap = _config["KAFKA_BOOTSTRAP_SERVERS"] ?? _config["Kafka:BootstrapServers"] ?? "kafka:9092";

            var conf = new ConsumerConfig
            {
                BootstrapServers = bootstrap,
                GroupId = _config["KAFKA_CONSUMER_GROUP"] ?? "notification-service-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = true
            };

            using var consumer = new ConsumerBuilder<string, string>(conf).Build();
            consumer.Subscribe(_topics);
            _logger.LogInformation("NotificationService: subscribed to topics: {Topics}", string.Join(',', _topics));

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var cr = consumer.Consume(stoppingToken);
                        if (cr == null) continue;

                        _logger.LogInformation("Received Kafka message. Topic={Topic} Key={Key}", cr.Topic, cr.Message.Key);

                        using var scope = _services.CreateScope();
                        switch (cr.Topic)
                        {
                            case "order_created":
                                {
                                    var evt = JsonSerializer.Deserialize<OrderCreatedEvent>(cr.Message.Value);
                                    var handler = scope.ServiceProvider.GetRequiredService<OrderCreatedHandler>();
                                    if (evt != null) handler.HandleAsync(evt).GetAwaiter().GetResult();
                                    break;
                                }
                            case "payment_status_changed":
                                {
                                    var evt = JsonSerializer.Deserialize<PaymentStatusChangedEvent>(cr.Message.Value);
                                    var handler = scope.ServiceProvider.GetRequiredService<PaymentStatusChangedHandler>();
                                    if (evt != null) handler.HandleAsync(evt).GetAwaiter().GetResult();
                                    break;
                                }
                            case "event_updated":
                                {
                                    var evt = JsonSerializer.Deserialize<EventUpdatedEvent>(cr.Message.Value);
                                    var handler = scope.ServiceProvider.GetRequiredService<EventUpdatedHandler>();
                                    if (evt != null) handler.HandleAsync(evt).GetAwaiter().GetResult();
                                    break;
                                }
                            case "event_canceled":
                                {
                                    var evt = JsonSerializer.Deserialize<EventCanceledEvent>(cr.Message.Value);
                                    var handler = scope.ServiceProvider.GetRequiredService<EventCanceledHandler>();
                                    if (evt != null) handler.HandleAsync(evt).GetAwaiter().GetResult();
                                    break;
                                }
                            default:
                                _logger.LogWarning("No handler for topic {Topic}", cr.Topic);
                                break;
                        }
                    }
                    catch (ConsumeException cex)
                    {
                        _logger.LogError(cex, "Kafka consume error");
                    }
                    catch (Exception ex)
                    {
                        _logger.LogError(ex, "Error processing message");
                    }
                }
            }
            catch (OperationCanceledException)
            {
                consumer.Close();
            }
        }
    }
}
