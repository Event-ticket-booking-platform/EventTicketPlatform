using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EventService.Application.Messaging
{
    public class EventCreatedMessage
    {
        public Guid EventId { get; set; }
        public string Title { get; set; }
        public string Location { get; set; }
        public DateTime StartUtc { get; set; }
        public DateTime EndUtc { get; set; }
        public string OrganizerId { get; set; }
        public DateTime CreatedUtc { get; set; }
    }
}
