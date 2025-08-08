# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy solution file (if exists)
# COPY *.sln .

# Copy main API project
COPY ["src/Services/EventService/EventService.Api/EventService.Api.csproj", "EventService.Api/"]
COPY ["src/Services/EventService/EventService.Application/EventService.Application.csproj", "EventService.Application/"]
COPY ["src/Services/EventService/EventService.Domain/EventService.Domain.csproj", "EventService.Domain/"]
COPY ["src/Services/EventService/EventService.Infrastructure/EventService.Infrastructure.csproj", "EventService.Infrastructure/"]

# Restore NuGet packages
RUN dotnet restore "EventService.Api/EventService.Api.csproj"

# Copy everything else
COPY . .

# Build
WORKDIR "/src/EventService.Api"
RUN dotnet build "EventService.Api.csproj" -c Release -o /app/build

# Stage 2: Publish
FROM build AS publish
RUN dotnet publish "EventService.Api.csproj" -c Release -o /app/publish

# Stage 3: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 80
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "EventService.Api.dll"]