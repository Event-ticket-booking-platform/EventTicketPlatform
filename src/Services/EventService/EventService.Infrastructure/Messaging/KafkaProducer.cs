using Confluent.Kafka;
using DnsClient.Internal;
using EventService.Application.Interfaces;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace EventService.Infrastructure.Messaging
{
    public class KafkaProducer : IKafkaProducer, IDisposable
    {
        private readonly IProducer<string, string> _producer;
        private readonly ILogger<KafkaProducer> _logger;
        private readonly string _defaultTopic;

        public KafkaProducer(IConfiguration cfg, ILogger<KafkaProducer> logger)
        {
            _logger = logger;

            var bootstrap = cfg["Kafka:BootstrapServers"] ?? cfg["Kafka__BootstrapServers"] ?? "kafka:9092";
            _defaultTopic = cfg["Kafka:Topics:EventCreated"] ?? "event-created";

            var config = new ProducerConfig
            {
                BootstrapServers = bootstrap,
                Acks = Acks.All,
                MessageTimeoutMs = 5000,
                EnableIdempotence = true
            };

            _producer = new ProducerBuilder<string, string>(config).Build();
        }

        public async Task PublishEventCreatedAsync(object message, string topic = "event-created")
        {
            var t = string.IsNullOrWhiteSpace(topic) ? _defaultTopic : topic;

            // serialize your DTO (EventCreatedMessage, etc.)
            var payload = JsonSerializer.Serialize(message);

            string? key = message?.GetType().GetProperty("EventId")?.GetValue(message)?.ToString();
            try
            {
                var result = await _producer.ProduceAsync(t, new Message<string, string>
                {
                    Key = key ?? Guid.NewGuid().ToString(),
                    Value = payload
                });
                _logger.LogInformation("Kafka produced: topic={Topic} partition={Partition} offset={Offset}",
                    result.Topic, result.Partition, result.Offset);
            }
            catch (ProduceException<string, string> ex)
            {
                _logger.LogError(ex, "Kafka produce failed for topic {Topic}", t);
                throw; // bubble up so the caller can handle if needed
            }
        }

        public void Dispose() => _producer?.Dispose();
    }
}
