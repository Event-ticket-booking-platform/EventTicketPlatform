FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src


COPY EventService.Api/*.csproj ./EventService.Api/
COPY EventService.Application/*.csproj ./EventService.Application/
COPY EventService.Domain/*.csproj ./EventService.Domain/
COPY EventService.Infrastructure/*.csproj ./EventService.Infrastructure/

RUN dotnet restore ./EventService.Api/EventService.Api.csproj

COPY . .

WORKDIR /src/EventService.Api
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "EventService.Api.dll"]
