namespace EventService.Application.Messaging.Catalog
{
    public class EventDeletedMessage
    {
        public Guid EventId { get; set; }
        public DateTime DeletedAt { get; set; }
    }
}