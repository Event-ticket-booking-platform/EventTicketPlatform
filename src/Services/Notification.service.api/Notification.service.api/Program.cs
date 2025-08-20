
using Confluent.Kafka;
using Notification.service.api.Handlers;
using Notification.service.api.Kafka;
using Notification.service.api.Services;

namespace Notification.service.api
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            builder.Services.AddAuthorization();

            // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
            builder.Services.AddControllers();
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // configuration (Kafka broker via env or appsettings)
            builder.Configuration.AddEnvironmentVariables();

            // Register handlers and services

            //builder.Services.AddSingleton<INotificationSender, ConsoleNotificationSender>();
            //builder.Services.AddTransient<OrderCreatedHandler>();
            //builder.Services.AddTransient<PaymentStatusChangedHandler>();
            //builder.Services.AddTransient<EventUpdatedHandler>();
            //builder.Services.AddTransient<EventCanceledHandler>();

            //// Register the Kafka consumer hosted service
            //builder.Services.AddHostedService<KafkaConsumerHostedService>();

            var config = new ConsumerConfig
            {
                BootstrapServers = builder.Configuration["Kafka:BootstrapServers"],
                GroupId = "notification-service",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            // Store notifications per user
            var userMessages = new Dictionary<Guid, List<dynamic>>();

            builder.Services.AddSingleton(config);
            builder.Services.AddSingleton(userMessages);
            builder.Services.AddHostedService<KafkaConsumerService>();

            var app = builder.Build();

            app.MapGet("/", () => Results.Ok(new { Service = "NotificationService", Version = "1.0" }));
            // All notifications (admin/debug view)
            app.MapGet("/notifications", (Dictionary<Guid, List<dynamic>> userMsgs) => Results.Ok(userMsgs));

            // Notifications for a specific user
            app.MapGet("/notifications/user/{userId:guid}", (Guid userId, Dictionary<Guid, List<dynamic>> userMsgs) =>
            {
                if (userMsgs.ContainsKey(userId))
                    return Results.Ok(userMsgs[userId]);
                return Results.NotFound(new { Message = "No notifications for this user" });
            });

            app.MapGet("/health/kafka", () =>
            {
                return Results.Ok(new { Status = "Kafka connection OK", Time = DateTime.UtcNow });
            });
            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseHttpsRedirection();

            app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}
