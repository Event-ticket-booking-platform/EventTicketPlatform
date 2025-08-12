using EventService.Application.DTOs;
using EventService.Domain.Entities;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EventService.Application.Services
{
    public interface IEventService
    {
        Task<List<Event>> GetAllEventsAsync();
        Task<Event?> GetEventByIdAsync(Guid id);
        Task<Guid> CreateEventAsync(CreateEventDTO dto);
    }
}
