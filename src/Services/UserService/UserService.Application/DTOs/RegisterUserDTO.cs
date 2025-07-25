using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using UserService.Domain.Enums;

namespace UserService.Application.DTOs
{
    public class RegisterUserDTO
    {
        public string Username { get; set; }
        public string Password { get; set; } // Will be hashed
        public string Email { get; set; }
        public Role Role { get; set; }
    }

}
