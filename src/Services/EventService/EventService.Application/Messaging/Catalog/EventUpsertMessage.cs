namespace EventService.Application.Messaging.Catalog
{
    public class EventUpsertMessage
    {
        public Guid EventId { get; set; }
        public string Title { get; set; } = null!;
        public string Description { get; set; } = null!;
        public string Location { get; set; } = null!;
        public string Status { get; set; } = "PUBLISHED";  // DRAFT | PUBLISHED | CANCELLED
        public DateTime StartsAt { get; set; }
        public DateTime EndsAt { get; set; }
        public DateTime UpdatedAt { get; set; }
        public decimal TicketPrice { get; set; }
        public int Version { get; set; }
        public string OrganizerId { get; set; } = null!;
    }
}