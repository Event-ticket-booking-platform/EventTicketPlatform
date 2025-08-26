using Yarp.ReverseProxy;

var builder = WebApplication.CreateBuilder(args);

// ----- YARP: load routes/clusters from appsettings -----
builder.Services.AddReverseProxy()
    .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"));

// ----- CORS -----
builder.Services.AddCors(o =>
{
    o.AddDefaultPolicy(p => p
        .AllowAnyOrigin()
        .AllowAnyHeader()
        .AllowAnyMethod());
});

var app = builder.Build();

app.UseCors();

// ----- Swagger UI aggregator -----
app.UseSwaggerUI(c =>
{
    // These point to downstream services via the gateway
    c.SwaggerEndpoint("/users/swagger/v1/swagger.json", "UserService");
    c.SwaggerEndpoint("/events/swagger/v1/swagger.json", "EventService");
    c.RoutePrefix = "swagger"; // available at /swagger
});

// ----- Proxy all requests to downstream services -----
app.MapReverseProxy();

// Health check root
app.MapGet("/", () => Results.Ok("Gateway is up"));

app.Run();
