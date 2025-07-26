using EventService.Application.DTOs;
using EventService.Application.Services;
using EventService.Domain.Entities;
using Microsoft.AspNetCore.Mvc;

namespace EventService.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class EventController : ControllerBase
    {
        private readonly IEventService _eventService;

        public EventController(IEventService eventService)
        {
            _eventService = eventService;
        }

        [HttpGet]
        public async Task<ActionResult<List<Event>>> GetAll()
        {
            var events = await _eventService.GetAllEventsAsync();
            return Ok(events);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Event>> GetById(Guid id)
        {
            var evt = await _eventService.GetEventByIdAsync(id);
            if (evt == null) return NotFound();
            return Ok(evt);
        }

        [HttpPost]
        public async Task<IActionResult> Create(CreateEventDTO dto)
        {
            await _eventService.CreateEventAsync(dto);
            return Ok(new { message = "Event created successfully" });
        }
    }
}
