namespace Notification.service.api.Services
{
    public class ConsoleNotificationSender: INotificationSender
    {
        private readonly ILogger<ConsoleNotificationSender> _logger;
        public ConsoleNotificationSender(ILogger<ConsoleNotificationSender> logger)
        {
            _logger = logger;
        }

        public Task SendAsync(string to, string subject, string body)
        {
            _logger.LogInformation("[Notification] To: {To}, Subject: {Subject}\n{Body}", to, subject, body);
            
            return Task.CompletedTask;
        }
    }
}
