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
        public Task<User?> FindByUsernameOrEmailAsync(string uoe) =>
             _db.Users.FirstOrDefaultAsync(u => u.Username == uoe || u.Email == uoe);

        // Get user by Id
        public Task<User?> GetByIdAsync(Guid id) =>
            _db.Users.FirstOrDefaultAsync(u => u.Id == id);

        public Task<User?> GetByUsernameAsync(string username) =>
            _db.Users.FirstOrDefaultAsync(u => u.Username == username);
    }

}
