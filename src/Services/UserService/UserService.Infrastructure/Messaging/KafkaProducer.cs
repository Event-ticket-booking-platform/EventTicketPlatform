using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using UserService.Application.Interfaces;
using UserService.Application.Messaging;


namespace UserService.Infrastructure.Messaging
{
    public class KafkaProducer : IKafkaProducer
    {
        private readonly IProducer<Null, string> _producer;

        public KafkaProducer()
        {
            var config = new ProducerConfig { BootstrapServers = "localhost:9092" };
            _producer = new ProducerBuilder<Null, string>(config).Build();
        }

        public async Task SendUserCreatedAsync(KafkaUserCreatedMessage message)
        {
            var json = JsonSerializer.Serialize(message);
            await _producer.ProduceAsync("user-created", new Message<Null, string> { Value = json });
        }
    }
}
