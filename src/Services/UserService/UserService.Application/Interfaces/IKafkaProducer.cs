using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Application.Messaging;

namespace UserService.Application.Interfaces
{
    public interface IKafkaProducer
    {
        Task SendUserCreatedAsync(KafkaUserCreatedMessage message);
    }
}
