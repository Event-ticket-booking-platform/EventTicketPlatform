FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY Services/EventService/EventService.Api/EventService.Api.csproj Services/EventService/EventService.Api/
COPY Services/EventService/EventService.Application/EventService.Application.csproj Services/EventService/EventService.Application/
COPY Services/EventService/EventService.Domain/EventService.Domain.csproj Services/EventService/EventService.Domain/
COPY Services/EventService/EventService.Infrastructure/EventService.Infrastructure.csproj Services/EventService/EventService.Infrastructure/

RUN dotnet restore "Services/EventService/EventService.Api/EventService.Api.csproj"

COPY . .

WORKDIR /src/Services/EventService/EventService.Api
RUN dotnet build "EventService.Api.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "EventService.Api.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 80
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "EventService.Api.dll"]
