namespace Notification.service.api.Models
{
    public class OrderCreatedEvent
    {
        public Guid OrderId { get; set; }
        public Guid TicketId { get; set; }
        public Guid UserId { get; set; }
        public string UserEmail { get; set; } = string.Empty;
        public string EventName { get; set; } = string.Empty;
        public DateTime OrderDate { get; set; }
    }
}
