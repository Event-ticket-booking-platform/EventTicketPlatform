# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy csproj files first (better layer caching)
COPY Services/UserService/UserService.Api/UserService.Api.csproj Services/UserService/UserService.Api/
COPY Services/UserService/UserService.Application/UserService.Application.csproj Services/UserService/UserService.Application/
COPY Services/UserService/UserService.Domain/UserService.Domain.csproj Services/UserService/UserService.Domain/
COPY Services/UserService/UserService.Infrastructure/UserService.Infrastructure.csproj Services/UserService/UserService.Infrastructure/

RUN dotnet restore "Services/UserService/UserService.Api/UserService.Api.csproj"

# Copy the rest of the source
COPY . .

WORKDIR /src/Services/UserService/UserService.Api
RUN dotnet build "UserService.Api.csproj" -c Release -o /app/build

# Stage 2: Publish
FROM build AS publish
RUN dotnet publish "UserService.Api.csproj" -c Release -o /app/publish /p:UseAppHost=false

# Stage 3: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 80
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "UserService.Api.dll"]
