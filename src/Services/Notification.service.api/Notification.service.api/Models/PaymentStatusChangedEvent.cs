namespace Notification.service.api.Models
{
    public class PaymentStatusChangedEvent
    {
        public Guid PaymentId { get; set; }
        public Guid OrderId { get; set; }
        public Guid UserId { get; set; }
        public string UserEmail { get; set; } = string.Empty;
        public string Status { get; set; } = string.Empty; // e.g., "success", "failed"
        public string? Reason { get; set; }
        public DateTime OccurredAt { get; set; }
    }
}
