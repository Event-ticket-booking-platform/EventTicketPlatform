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
using Microsoft.OpenApi.Models;


var builder = WebApplication.CreateBuilder(args);

builder.Configuration
    .AddJsonFile("appsettings.Local.json", optional: true, reloadOnChange: true)
    .AddEnvironmentVariables();


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
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "EventService", Version = "v1" });

    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Enter: Bearer {your JWT}"
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        { new OpenApiSecurityScheme { Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" } }, Array.Empty<string>() }
    });

    // Add gateway base path when needed
    if (builder.Configuration.GetValue<bool>("Swagger:UseGatewayBasePath"))
        c.AddServer(new OpenApiServer { Url = "/events" });
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
