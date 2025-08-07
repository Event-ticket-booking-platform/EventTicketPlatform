using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Domain.Entities;
using UserService.Application.Interfaces;


namespace UserService.Infrastructure.Persistence
{

    public class UserRepository : IUserRepository
    {
        private readonly UserDbContext _db;
        public UserRepository(UserDbContext db) => _db = db;

        public async Task AddAsync(User user)
        {
            _db.Users.Add(user);
            await _db.SaveChangesAsync();
        }

        public Task<List<User>> GetAllAsync() => _db.Users.ToListAsync();
    }

}
