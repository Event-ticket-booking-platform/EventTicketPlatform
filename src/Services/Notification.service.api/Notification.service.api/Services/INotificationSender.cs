namespace Notification.service.api.Services
{
    public interface INotificationSender
    {
        Task SendAsync(string to, string subject, string body);
    }
}
