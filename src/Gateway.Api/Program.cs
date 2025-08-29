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
    c.SwaggerEndpoint("/users/swagger/v1/swagger.json", "UserService");
    c.SwaggerEndpoint("/events/swagger/v1/swagger.json", "EventService");
    c.SwaggerEndpoint("/notification/swagger/v1/swagger.json", "NotificationService");
    c.SwaggerEndpoint("/orders/swagger/v1/swagger.json", "OrderService");
    c.SwaggerEndpoint("/payments/swagger/v1/swagger.json", "PaymentService");
    c.SwaggerEndpoint("/tickets/swagger/v1/swagger.json", "TicketService");
    c.RoutePrefix = "swagger";
});




// ----- Proxy all requests to downstream services -----
app.MapReverseProxy();

// Health check root
app.MapGet("/", () => Results.Ok("Gateway is up"));

app.Run();
