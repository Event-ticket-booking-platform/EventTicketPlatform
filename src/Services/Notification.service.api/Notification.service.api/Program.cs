
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

            var messages = new List<object>();

            builder.Services.AddSingleton(config);
            builder.Services.AddSingleton(messages);
            builder.Services.AddHostedService<KafkaConsumerService>();

            var app = builder.Build();

            app.MapGet("/", () => Results.Ok(new { Service = "NotificationService", Version = "1.0" }));
            app.MapGet("/notifications", (List<object> msgs) => Results.Ok(msgs));
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
