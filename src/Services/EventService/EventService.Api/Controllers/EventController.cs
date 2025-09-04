using EventService.Application.DTOs;
using EventService.Application.Services;
using EventService.Domain.Entities;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using EventService.Infrastructure.Messaging;
using EventService.Application.Messaging.Catalog;
using EventService.Application.Interfaces;
using System.Text.Json;


namespace EventService.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class EventController : ControllerBase
    {
        private readonly IEventService _eventService;
        private readonly IKafkaProducer _kafka;
        private readonly ILogger<EventController> _logger;

        public EventController(IEventService eventService, IKafkaProducer kafka, ILogger<EventController> logger)
        {
            _eventService = eventService;
            _kafka = kafka;
            _logger = logger;
        }

        [HttpGet]
        public async Task<ActionResult<List<Event>>> GetAll()
        {
            var events = await _eventService.GetAllEventsAsync();
            return Ok(events);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Event>> GetById(string id)
        {
            if (!Guid.TryParse(id, out var gid))
                return BadRequest("Invalid id format.");

            var evt = await _eventService.GetEventByIdAsync(gid);
            return evt is null ? NotFound() : Ok(evt);
        }


         // [Authorize(Roles = "Admin")]
        [Authorize(Policy = "AdminOnly")]
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CreateEventDTO dto)
        {
            var id = await _eventService.CreateEventAsync(dto);

            _logger.LogInformation("IsInRole(Admin) = {IsAdmin}", User.IsInRole("Admin"));

            // ✅ FIX: FIRST load the created event...
            var created = await _eventService.GetEventByIdAsync(id);

            // ...THEN check for null (previously this check came before the declaration)
            if (created is null)
            {
                _logger.LogError("Created event {EventId} could not be loaded back from store.", id);
                return StatusCode(500, "Event was created but could not be loaded.");
            }

            var msg = new EventUpsertMessage
            {
                EventId = created.Id,
                Title = created.Title,
                Description = created.Description,
                Location = created.Location,
                Status = "PUBLISHED",   // or DRAFT based on your workflow
                StartsAt = created.Date,
                EndsAt = created.Date,
                UpdatedAt = DateTime.UtcNow,
                TicketPrice = created.TicketPrice,
                Version = 1
            };

            // ✅ FIX: JsonSerializer requires using System.Text.Json;
            var json = JsonSerializer.Serialize(msg);
            _logger.LogInformation("Publishing EventUpsertMessage to {Topic}: {Payload}",
                KafkaTopics.EventCatalogUpsert, json);

            try
            {
                //Json format
                // await _kafka.PublishEventCreatedAsync(msg, KafkaTopics.EventCatalogUpsert);
                // String format
                // Build a raw string
            var payload = $"EventId={created.Id}; Title={created.Title}; Location={created.Location}; Date={created.Date}";

            // Send using raw string publisher
            await _kafka.PublishRawStringAsync(payload, KafkaTopics.EventCatalogUpsert);

                _logger.LogInformation("Published EventUpsertMessage for EventId {EventId}", msg.EventId);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to publish EventUpsertMessage for EventId {EventId}", msg.EventId);
                return StatusCode(500, "Event created, but failed to publish to Kafka.");
            }

            return CreatedAtAction(nameof(GetById), new { id }, new { id });
        }


        [Authorize]
        [HttpGet("whoami")]
        public IActionResult WhoAmI()
             => Ok(User.Claims.Select(c => new { c.Type, c.Value }));

    }
}
