using Notification.service.api.Models;
using Notification.service.api.Services;

namespace Notification.service.api.Handlers
{
    public class EventUpdatedHandler
    {
        private readonly INotificationSender _sender;
        private readonly ILogger<EventUpdatedHandler> _logger;

        public EventUpdatedHandler(INotificationSender sender, ILogger<EventUpdatedHandler> logger)
        {
            _sender = sender;
            _logger = logger;
        }

        public Task HandleAsync(EventUpdatedEvent evt)
        {
            _logger.LogInformation("Handling EventUpdatedEvent for EventId {EventId}", evt.EventId);

            // In a real app you'd target only impacted users. For now, assume the event contains necessary info.
            var subject = $"Update: {evt.EventName}";
            var body = $"The event '{evt.EventName}' was updated:\n\n{evt.UpdateSummary}\n\nUpdated at: {evt.UpdatedAt:u}\n\nPlease check your booking details.";
            // Example: send to a support or broadcast address; for demo we log to console via sender
            return _sender.SendAsync("broadcast@example.com", subject, body);
        }
    }
}
