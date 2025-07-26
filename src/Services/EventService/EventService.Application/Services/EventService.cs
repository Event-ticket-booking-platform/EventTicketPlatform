using EventService.Application.DTOs;
using EventService.Application.Interfaces;
using EventService.Domain.Entities;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EventService.Application.Services
{
    public class EventService : IEventService
    {
        private readonly IEventRepository _repository;

        public EventService(IEventRepository repository)
        {
            _repository = repository;
        }

        public async Task<List<Event>> GetAllEventsAsync() => await _repository.GetAllAsync();

        public async Task<Event?> GetEventByIdAsync(Guid id) => await _repository.GetByIdAsync(id);

        public async Task CreateEventAsync(CreateEventDTO dto)
        {
            var evt = new Event
            {
                Title = dto.Title,
                Description = dto.Description,
                Location = dto.Location,
                Date = dto.Date,
                OrganizerId = dto.OrganizerId
            };
            await _repository.AddAsync(evt);
        }
    }
}
