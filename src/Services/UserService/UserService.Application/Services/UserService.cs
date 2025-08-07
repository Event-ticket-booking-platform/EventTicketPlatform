using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Application.DTOs;
using UserService.Application.Interfaces;
using UserService.Domain.Entities;

namespace UserService.Application.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _repo;
        private readonly IKafkaProducer _producer;
        public UserService(IUserRepository repo, IKafkaProducer producer)
        {
            _repo = repo;
            _producer = producer;
        }

        public async Task<Guid> RegisterAsync(RegisterUserDTO dto)
        {
            var user = new User
            {
                Id = Guid.NewGuid(),
                Username = dto.Username,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password),
                Email = dto.Email,
                Role = dto.Role
            };
            await _repo.AddAsync(user);

            await _producer.SendUserCreatedAsync(new Messaging.KafkaUserCreatedMessage
            {
                Id = user.Id,
                Username = user.Username,
                Email = user.Email,
                Role = user.Role.ToString()
            });
            return user.Id;
        }

        public Task<List<User>> GetAllAsync() => _repo.GetAllAsync();
    }

}
