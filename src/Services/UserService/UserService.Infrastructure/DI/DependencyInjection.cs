using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Infrastructure.Persistence;

namespace UserService.Infrastructure.DI
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration config)
        {
            services.AddDbContext<UserDbContext>(options =>
                options.UseNpgsql(config.GetConnectionString("Postgres")));
            services.AddScoped<IUserRepository, UserRepository>();
            return services;
        }
    }

}
