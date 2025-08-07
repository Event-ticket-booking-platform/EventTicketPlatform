using Confluent.Kafka;
using EventService.Application.Messaging;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace EventService.Infrastructure.Messaging
{
    public class KafkaConsumer : BackgroundService
    {
        private readonly ILogger<KafkaConsumer> _logger;

        public KafkaConsumer(ILogger<KafkaConsumer> logger)
        {
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            var config = new ConsumerConfig
            {
                BootstrapServers = "kafka:9092",
                GroupId = "event-service-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            using var consumer = new ConsumerBuilder<Ignore, string>(config).Build();
            consumer.Subscribe("user-created");

            _logger.LogInformation("✅ Kafka Consumer started for topic 'user-created'...");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = consumer.Consume(stoppingToken);
                    var message = JsonSerializer.Deserialize<KafkaUserCreatedMessage>(result.Message.Value);

                    if (message != null)
                    {
                        _logger.LogInformation($"👤 Consumed User: {message.Username} | Email: {message.Email} | Role: {message.Role}");

                        // TODO: Save or validate OrganizerId, etc.
                    }
                }
                catch (ConsumeException ex)
                {
                    _logger.LogError($"❌ Kafka consume error: {ex.Error.Reason}");
                }

                await Task.Delay(100, stoppingToken); // avoid tight loop
            }
        }
    }
}
