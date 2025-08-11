
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

            builder.Services.AddSingleton<INotificationSender, ConsoleNotificationSender>();
            builder.Services.AddTransient<OrderCreatedHandler>();
            builder.Services.AddTransient<PaymentStatusChangedHandler>();
            builder.Services.AddTransient<EventUpdatedHandler>();
            builder.Services.AddTransient<EventCanceledHandler>();

            // Register the Kafka consumer hosted service
            builder.Services.AddHostedService<KafkaConsumerHostedService>();

            var app = builder.Build();

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
