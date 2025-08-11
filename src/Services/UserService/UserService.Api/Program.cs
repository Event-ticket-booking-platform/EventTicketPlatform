using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;
using UserService.Application.Interfaces;
using UserService.Infrastructure.DI;
using UserService.Infrastructure.Persistence;

var builder = WebApplication.CreateBuilder(args);

// Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Controllers (keep ONLY once)
builder.Services
    .AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
    });

// App services + infrastructure (this reads ConnectionStrings:DefaultConnection)
builder.Services.AddScoped<IUserService, UserService.Application.Services.UserService>();
builder.Services.AddInfrastructure(builder.Configuration);

var app = builder.Build();

// One-time startup probe (useful while debugging)
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<UserDbContext>();
    var conn = db.Database.GetDbConnection();
    Console.WriteLine(">>> Connected to DB: " + conn.ConnectionString);
    Console.WriteLine(">>> Can Connect?     : " + db.Database.CanConnect());
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();

app.MapControllers();

// Debug endpoint to confirm what the app sees
app.MapGet("/debug/conn", (IConfiguration cfg) =>
    Results.Text(cfg.GetConnectionString("DefaultConnection") ?? "<null>"));

app.Run();

record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
{
    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
}



//using Microsoft.EntityFrameworkCore;
//using System.Text.Json.Serialization;
//using UserService.Application.Interfaces;
//using UserService.Infrastructure.DI;
//using UserService.Infrastructure.Persistence;

//var builder = WebApplication.CreateBuilder(args);
////builder.WebHost.UseUrls("http://0.0.0.0:80");

//// Add services to the container.
//// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
//builder.Services.AddEndpointsApiExplorer();
//builder.Services.AddSwaggerGen();
//builder.Services
//    .AddControllers()
//    .AddJsonOptions(options =>
//    {
//        options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
//    });

//// Add services
//builder.Services.AddControllers();
//builder.Services.AddScoped<IUserService, UserService.Application.Services.UserService>();
//builder.Services.AddInfrastructure(builder.Configuration);


//var app = builder.Build();

//using (var scope = app.Services.CreateScope())
//{
//    var db = scope.ServiceProvider.GetRequiredService<UserDbContext>();
//    var conn = db.Database.GetDbConnection();
//    Console.WriteLine(">>> Connected to DB: " + conn.ConnectionString);
//    Console.WriteLine(">>> Can Connect?     : " + db.Database.CanConnect());
//}

//app.UseAuthorization();
//app.MapControllers();

//// Configure the HTTP request pipeline.
//if (app.Environment.IsDevelopment())
//{
//    app.UseSwagger();
//    app.UseSwaggerUI();
//}

//app.UseHttpsRedirection();

//var summaries = new[]
//{
//    "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
//};

//app.MapGet("/weatherforecast", () =>
//{
//    var forecast =  Enumerable.Range(1, 5).Select(index =>
//        new WeatherForecast
//        (
//            DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
//            Random.Shared.Next(-20, 55),
//            summaries[Random.Shared.Next(summaries.Length)]
//        ))
//        .ToArray();
//    return forecast;
//})
//.WithName("GetWeatherForecast")
//.WithOpenApi();

//app.Run();

//record WeatherForecast(DateOnly Date, int TemperatureC, string? Summary)
//{
//    public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);
//}
