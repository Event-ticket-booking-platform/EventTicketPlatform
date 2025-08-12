using EventService.Application.Interfaces;
using EventService.Infrastructure.Messaging;
using EventService.Infrastructure.Repositories;
using EventService.Infrastructure.Settings;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace EventService.Infrastructure.DI
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration config)
        {
            // Bind strongly-typed settings (env vars with __ override these)
            services.Configure<MongoDbSettings>(config.GetSection("MongoDbSettings"));
            services.AddSingleton<IKafkaProducer, KafkaProducer>();

            // IMongoClient as singleton (thread-safe)
            services.AddSingleton<IMongoClient>(sp =>
            {
                var s = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
                return new MongoClient(s.ConnectionString);
            });

            // Expose IMongoDatabase too (built from the DB name)
            services.AddSingleton(sp =>
            {
                var s = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
                var client = sp.GetRequiredService<IMongoClient>();
                return client.GetDatabase(s.DatabaseName);
            });

            services.AddScoped<IEventRepository, EventRepository>();
            return services;
        }
    }
}

