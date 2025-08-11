using EventService.Application.DTOs;
using EventService.Application.Services;
using EventService.Domain.Entities;
using Microsoft.AspNetCore.Authorization;
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

            return CreatedAtAction(nameof(GetById), new { id }, new { id });
        }


        [Authorize]
        [HttpGet("whoami")]
        public IActionResult WhoAmI()
             => Ok(User.Claims.Select(c => new { c.Type, c.Value }));

    }
}
