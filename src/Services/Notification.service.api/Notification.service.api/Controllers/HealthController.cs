using Microsoft.AspNetCore.Mvc;

namespace Notification.service.api.Controllers
{
    public class HealthController: ControllerBase
    {
        [HttpGet]
        public IActionResult Get()
        {
            return Ok(new {status= "NotificationService OK",time = DateTime.UtcNow });
        }
    }
    
}
