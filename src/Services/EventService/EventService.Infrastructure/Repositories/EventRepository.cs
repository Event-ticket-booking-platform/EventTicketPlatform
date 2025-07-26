using EventService.Infrastructure.Settings;
using EventService.Infrastructure.Repositories;
using EventService.Domain.Entities;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using EventService.Application.Interfaces;


namespace EventService.Infrastructure.Repositories
{
    public class EventRepository : IEventRepository
    {
        private readonly IMongoCollection<Event> _events;

        public EventRepository(IOptions<MongoDbSettings> settings)
        {
            var client = new MongoClient(settings.Value.ConnectionString);
            var database = client.GetDatabase(settings.Value.DatabaseName);
            _events = database.GetCollection<Event>(settings.Value.EventsCollectionName);
        }

        public async Task<List<Event>> GetAllAsync() => await _events.Find(_ => true).ToListAsync();
        public async Task<Event?> GetByIdAsync(Guid id) => await _events.Find(e => e.Id == id).FirstOrDefaultAsync();
        public async Task AddAsync(Event evt) => await _events.InsertOneAsync(evt);
    }
}
