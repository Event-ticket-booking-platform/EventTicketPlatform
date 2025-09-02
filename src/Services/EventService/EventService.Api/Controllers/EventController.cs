using EventService.Application.DTOs;
using EventService.Application.Services;
using EventService.Domain.Entities;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using EventService.Infrastructure.Messaging;
using EventService.Application.Messaging.Catalog;
using EventService.Application.Interfaces;

namespace EventService.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class EventController : ControllerBase
    {
        private readonly IEventService _eventService;
        private readonly IKafkaProducer _kafka;

        public EventController(IEventService eventService, IKafkaProducer kafka)
        {
            _eventService = eventService;
            _kafka = kafka;
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
            // Debug: should print True
            Console.WriteLine($"IsInRole(Admin) = {User.IsInRole("Admin")}");

            var created = await _eventService.GetEventByIdAsync(id);

            var msg = new EventUpsertMessage
            {
                EventId = created.Id,
                Title = created.Title,
                Description = created.Description,
                Location = created.Location,
                Status = "PUBLISHED",               // or DRAFT based on your workflow
                StartsAt = created.Date,            // adjust if you store start/end separately
                EndsAt = created.Date,
                UpdatedAt = DateTime.UtcNow,
                Version = 1
            };

            await _kafka.PublishEventCreatedAsync(msg, KafkaTopics.EventCatalogUpsert);


            return CreatedAtAction(nameof(GetById), new { id }, new { id });
        }


        [Authorize]
        [HttpGet("whoami")]
        public IActionResult WhoAmI()
             => Ok(User.Claims.Select(c => new { c.Type, c.Value }));

    }
}
