namespace EventService.Application.Messaging.Catalog
{
    public class EventDeletedMessage
    {
        public string EventId { get; set; }
        public DateTime DeletedAt { get; set; }
    }
}