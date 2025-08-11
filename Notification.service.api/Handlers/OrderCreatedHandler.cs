using Notification.service.api.Models;
using Notification.service.api.Services;

namespace Notification.service.api.Handlers
{
    public class OrderCreatedHandler
    {
        private readonly INotificationSender _sender;
        private readonly ILogger<OrderCreatedHandler> _logger;

        public OrderCreatedHandler(INotificationSender sender, ILogger<OrderCreatedHandler> logger)
        {
            _sender = sender;
            _logger = logger;
        }

        public Task HandleAsync(OrderCreatedEvent evt)
        {
            _logger.LogInformation("Handling OrderCreated event for order {OrderId}", evt.OrderId);

            var subject = $"Booking confirmed: {evt.EventName}";
            var body = $"Hi,\n\nYour booking (Order: {evt.OrderId}) for '{evt.EventName}' is confirmed.\nOrder date: {evt.OrderDate:u}\n\nTicketId: {evt.TicketId}\n\nThank you.";
            return _sender.SendAsync(evt.UserEmail, subject, body);
        }
    }
}
