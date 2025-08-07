using EventService.Application.Interfaces;
using EventService.Application.Services;
using EventService.Infrastructure.DI;
using MongoDB.Bson.Serialization;
using MongoDB.Bson.Serialization.Serializers;
using MongoDB.Bson;
using EventService.Infrastructure.Messaging;

var builder = WebApplication.CreateBuilder(args);

// ✅ Register custom Guid serializer for compatibility
BsonSerializer.RegisterSerializer(new GuidSerializer(BsonType.String));

// Add services to the container
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Register Application and Infrastructure dependencies
builder.Services.AddScoped<IEventService, EventService.Application.Services.EventService>();
builder.Services.AddInfrastructure(builder.Configuration);

builder.Services.AddHostedService<KafkaConsumer>();

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();
