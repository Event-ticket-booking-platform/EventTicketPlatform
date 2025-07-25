using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Application.DTOs;
using UserService.Domain.Entities;

namespace UserService.Application.Interfaces
{
    public interface IUserService
    {
        Task<Guid> RegisterAsync(RegisterUserDTO dto);
        Task<List<User>> GetAllAsync();
    }

}
