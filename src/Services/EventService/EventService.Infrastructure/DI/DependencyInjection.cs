using EventService.Application.Interfaces;
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



//using EventService.Application.Interfaces;
//using EventService.Infrastructure.Repositories;
//using EventService.Infrastructure.Settings;
//using Microsoft.Extensions.Configuration;
//using Microsoft.Extensions.DependencyInjection;
//using MongoDB.Driver;
//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using System.Threading.Tasks;

//namespace EventService.Infrastructure.DI
//{
//    public static class DependencyInjection
//    {
//        public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration config)
//        {
//            services.Configure<MongoDbSettings>(config.GetSection("MongoDbSettings"));

//            services.AddSingleton<IMongoClient>(sp =>
//            {
//                var settings = config.GetSection("MongoDbSettings").Get<MongoDbSettings>();
//                return new MongoClient(settings.ConnectionString);
//            });

//            services.AddScoped<IEventRepository, EventRepository>();
//            return services;
//        }
//    }
//}
