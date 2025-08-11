using EventService.Application.DTOs;
using EventService.Application.Interfaces;
using EventService.Domain.Entities;
using EventService.Application.Messaging;
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
        private readonly IKafkaProducer _producer;

        public EventService(IEventRepository repository, IKafkaProducer producer)
        {
            _repository = repository;
            _producer = producer;
        }

        public async Task<List<Event>> GetAllEventsAsync() => await _repository.GetAllAsync();

        public async Task<Event?> GetEventByIdAsync(Guid id) => await _repository.GetByIdAsync(id);

        public async Task<Guid> CreateEventAsync(CreateEventDTO dto)
        {
            if (dto.StartUtc >= dto.EndUtc)
            {
                throw new ArgumentException("StartUtc must be before EndUtc.");
            }
            var evt = new Event
            {
                Id = Guid.NewGuid(),
                Title = dto.Title,
                Description = dto.Description,
                Location = dto.Location,
                Date = dto.StartUtc,
                OrganizerId = dto.OrganizerId
            };
            await _repository.AddAsync(evt);

            // Publish to Kafka
            await _producer.PublishEventCreatedAsync(new EventCreatedMessage
            {
                EventId = evt.Id,
                Title = evt.Title,
                Location = evt.Location,
                StartUtc = dto.StartUtc,
                EndUtc = dto.EndUtc,
                OrganizerId = dto.OrganizerId,
                CreatedUtc = DateTime.UtcNow
            });
            return evt.Id;
        }
    }
}
