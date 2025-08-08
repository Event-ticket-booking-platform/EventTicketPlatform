# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy main API project
COPY ["src/Services/UserService/UserService.Api/UserService.Api.csproj", "UserService.Api/"]
COPY ["src/Services/UserService/UserService.Application/UserService.Application.csproj", "UserService.Application/"]
COPY ["src/Services/UserService/UserService.Domain/UserService.Domain.csproj", "UserService.Domain/"]
COPY ["src/Services/UserService/UserService.Infrastructure/UserService.Infrastructure.csproj", "UserService.Infrastructure/"]

# Restore NuGet packages
RUN dotnet restore "UserService.Api/UserService.Api.csproj"

# Copy everything else
COPY . .

# Build
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