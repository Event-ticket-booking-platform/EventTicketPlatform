using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EventService.Application.Interfaces
{
    public interface IKafkaProducer
    {
        Task PublishEventCreatedAsync(object message, string topic = "event-created");
    }
}
