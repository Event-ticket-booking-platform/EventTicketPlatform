namespace EventService.Infrastructure.Messaging
{
    public static class KafkaTopics
    {
        public const string EventCatalogUpsert = "event.catalog.upsert.v1";
        public const string EventCatalogDeleted = "event.catalog.deleted.v1";
    }
}