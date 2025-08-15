using Notification.service.api.Models;
using Notification.service.api.Services;

namespace Notification.service.api.Handlers
{
    public class EventCanceledHandler
    {
        private readonly INotificationSender _sender;
        private readonly ILogger<EventCanceledHandler> _logger;

        public EventCanceledHandler(INotificationSender sender, ILogger<EventCanceledHandler> logger)
        {
            _sender = sender;
            _logger = logger;
        }

        public Task HandleAsync(EventCanceledEvent evt)
        {
            _logger.LogInformation("Handling EventCanceledEvent for EventId {EventId}", evt.EventId);

            var subject = $"Event canceled: {evt.EventName}";
            var body = $"We regret to inform that the event '{evt.EventName}' has been canceled.\nReason: {evt.Reason}\nCanceled at: {evt.CanceledAt:u}\nPlease contact support for refund details.";
            return _sender.SendAsync("broadcast@example.com", subject, body);
        }
    }
}
