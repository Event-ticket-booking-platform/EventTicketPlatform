//using EventService.Application.Interfaces;
//using EventService.Application.Services;
//using EventService.Infrastructure.DI;
//using MongoDB.Bson.Serialization;
//using MongoDB.Bson.Serialization.Serializers;
//using MongoDB.Bson;
//// using EventService.Infrastructure.Messaging; // optional: keep commented

//var builder = WebApplication.CreateBuilder(args);

//// ✅ Register custom Guid serializer for MongoDB
//BsonSerializer.RegisterSerializer(new GuidSerializer(BsonType.String));

//// ✅ Add CORS
//builder.Services.AddCors(options =>
//{
//    options.AddPolicy("AllowAll", policy =>
//    {
//        policy.AllowAnyOrigin()
//              .AllowAnyHeader()
//              .AllowAnyMethod();
//    });
//});

//// ✅ Add Controllers, Swagger, etc.
//builder.Services.AddControllers();
//builder.Services.AddEndpointsApiExplorer();
//builder.Services.AddSwaggerGen();

//// ✅ Register dependencies
//builder.Services.AddScoped<IEventService, EventService.Application.Services.EventService>();
//builder.Services.AddInfrastructure(builder.Configuration);

//// ❌ Kafka disabled for now
//// builder.Services.AddHostedService<KafkaConsumer>();

//// ✅ Set custom port
////builder.WebHost.UseUrls("http://localhost:5000");

//var app = builder.Build();

//// ✅ Swagger for dev
//if (app.Environment.IsDevelopment())
//{
//    app.UseSwagger();
//    app.UseSwaggerUI(c =>
//    {
//        c.SwaggerEndpoint("/swagger/v1/swagger.json", "EventService API V1");
//        c.RoutePrefix = string.Empty; // Swagger at root URL
//    });
//}

//// ❌ Disable HTTPS for local dev
//// app.UseHttpsRedirection();

//app.UseCors("AllowAll");
//app.UseAuthorization();
//app.MapControllers();

//app.Run();


using EventService.Application.Interfaces;
using EventService.Application.Services;
using EventService.Infrastructure.DI;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using MongoDB.Bson;
using MongoDB.Bson.Serialization;
using MongoDB.Bson.Serialization.Serializers;
using System.Security.Claims;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// Mongo: store Guid as string
BsonSerializer.RegisterSerializer(new GuidSerializer(BsonType.String));

// CORS
builder.Services.AddCors(opts =>
{
    opts.AddPolicy("AllowAll", p => p
        .AllowAnyOrigin()
        .AllowAnyHeader()
        .AllowAnyMethod());
});

// MVC + Swagger
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.AddSecurityDefinition("Bearer", new()
    {
        Name = "Authorization",
        Type = Microsoft.OpenApi.Models.SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = Microsoft.OpenApi.Models.ParameterLocation.Header,
        Description = "Enter: Bearer {your JWT}"
    });
    c.AddSecurityRequirement(new()
    {
        {
            new() { Reference = new() { Type = Microsoft.OpenApi.Models.ReferenceType.SecurityScheme, Id = "Bearer" } },
            Array.Empty<string>()
        }
    });
});


builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(opt =>
    {
        opt.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidAudience = builder.Configuration["Jwt:Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"]!)),
            RoleClaimType = ClaimTypes.Role   // lets [Authorize(Roles="Admin")] work
        };
    });

// DI
builder.Services.AddScoped<IEventService, EventService.Application.Services.EventService>();
builder.Services.AddInfrastructure(builder.Configuration);
builder.Services.AddAuthorization(options =>
{
    // require the role claim to be exactly "Admin"
    options.AddPolicy("AdminOnly",
        policy => policy.RequireClaim(ClaimTypes.Role, "Admin"));
});

// Bind to all interfaces on port 5000 (overridable by env URLS)
builder.WebHost.UseUrls("http://0.0.0.0:5000");

var app = builder.Build();

// Swagger ALWAYS (root path)
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "EventService API V1");
    c.RoutePrefix = string.Empty; // Swagger at "/"
});

// HTTPS off in container (you’re exposing HTTP)
//// app.UseHttpsRedirection();

app.UseCors("AllowAll");
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

// Simple health probe
app.MapGet("/health", () => Results.Ok("OK"));

app.Run();
