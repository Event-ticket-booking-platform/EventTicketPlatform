# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy only what's needed (relative to context)
COPY ../UserService.Api/UserService.Api.csproj ./UserService.Api/
COPY ../UserService.Application/UserService.Application.csproj ./UserService.Application/
COPY ../UserService.Domain/UserService.Domain.csproj ./UserService.Domain/
COPY ../UserService.Infrastructure/UserService.Infrastructure.csproj ./UserService.Infrastructure/

RUN dotnet restore "UserService.Api/UserService.Api.csproj"

COPY .. .  # Copy everything in ../UserService folder

WORKDIR "/src/UserService.Api"
RUN dotnet build "UserService.Api.csproj" -c Release -o /app/build

# Stage 2: Publish
FROM build AS publish
RUN dotnet publish "UserService.Api.csproj" -c Release -o /app/publish

# Stage 3: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 80
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "UserService.Api.dll"]
