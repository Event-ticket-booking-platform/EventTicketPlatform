namespace Notification.service.api.Models
{
    public class EventCanceledEvent
    {
        public Guid EventId { get; set; }
        public string EventName { get; set; } = string.Empty;
        public string Reason { get; set; } = string.Empty;
        public DateTime CanceledAt { get; set; }
    }
}
