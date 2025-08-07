FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src


COPY UserService.Api/*.csproj ./UserService.Api/
COPY UserService.Application/*.csproj ./UserService.Application/
COPY UserService.Domain/*.csproj ./UserService.Domain/
COPY UserService.Infrastructure/*.csproj ./UserService.Infrastructure/

RUN dotnet restore ./UserService.Api/UserService.Api.csproj

COPY . .

WORKDIR /src/UserService.Api
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "UserService.Api.dll"]
