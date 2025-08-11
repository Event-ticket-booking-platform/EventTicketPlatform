using Notification.service.api.Models;
using Notification.service.api.Services;

namespace Notification.service.api.Handlers
{
    public class PaymentStatusChangedHandler
    {
        private readonly INotificationSender _sender;
        private readonly ILogger<PaymentStatusChangedHandler> _logger;

        public PaymentStatusChangedHandler(INotificationSender sender, ILogger<PaymentStatusChangedHandler> logger)
        {
            _sender = sender;
            _logger = logger;
        }

        public Task HandleAsync(PaymentStatusChangedEvent evt)
        {
            _logger.LogInformation("Handling PaymentStatusChangedEvent for PaymentId {PaymentId}: {Status}", evt.PaymentId, evt.Status);

            var subject = evt.Status.ToLower() == "success" ? "Payment Successful" : "Payment Failed";
            var body = evt.Status.ToLower() == "success"
                ? $"Hi,\n\nYour payment for Order {evt.OrderId} has been received successfully."
                : $"Hi,\n\nYour payment for Order {evt.OrderId} failed. Reason: {evt.Reason}\nPlease try again or contact support.";

            return _sender.SendAsync(evt.UserEmail, subject, body);
        }
    }
}
