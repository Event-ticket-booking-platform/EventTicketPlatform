namespace Notification.service.api.Models
{
    public class EventUpdatedEvent
    {
        public Guid EventId { get; set; }
        public string EventName { get; set; } = string.Empty;
        public string UpdateSummary { get; set; } = string.Empty; // what changed: time/venue/other
        public DateTime UpdatedAt { get; set; }
    }
}
