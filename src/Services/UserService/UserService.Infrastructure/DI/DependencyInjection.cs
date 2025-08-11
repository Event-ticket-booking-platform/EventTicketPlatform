using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Infrastructure.Persistence;
using UserService.Application.Interfaces;
using UserService.Infrastructure.Messaging;


namespace UserService.Infrastructure.DI
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration config)
        {
            //services.AddDbContext<UserDbContext>(options =>
            //    options.UseNpgsql(config.GetConnectionString("Postgres")));
            var cs = config.GetConnectionString("DefaultConnection");
            if (string.IsNullOrWhiteSpace(cs))
                throw new InvalidOperationException("Missing ConnectionStrings:DefaultConnection");

            services.AddDbContext<UserDbContext>(options => options.UseNpgsql(cs));

            services.AddScoped<IUserRepository, UserRepository>();
            services.AddSingleton<IKafkaProducer, KafkaProducer>();
            return services;
        }
    }

}
