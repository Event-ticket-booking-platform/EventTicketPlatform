# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /Notification.service.api

# Copy csproj files first for better caching
COPY src/Services/Notification.service.api/Notification.service.api/Notification.service.api.csproj .

# Restore dependencies
RUN dotnet restore "Notification.service.api.csproj"

# Copy the rest of the source code
COPY . .

# Build the project
WORKDIR /Notification.service.api
RUN dotnet build "Notification.service.api.csproj" -c Release -o /app/build

# Stage 2: Publish
FROM build AS publish
RUN dotnet publish "Notification.service.api.csproj" -c Release -o /app/publish /p:UseAppHost=false

# Stage 3: Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
EXPOSE 80
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Notification.service.api.dll"]
