# build
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src
COPY src/Gateway.Api/Gateway.Api.csproj src/Gateway.Api/
RUN dotnet restore src/Gateway.Api/Gateway.Api.csproj
COPY . .
WORKDIR /src/src/Gateway.Api
RUN dotnet publish -c Release -o /app/publish /p:UseAppHost=false

# run
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS final
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "Gateway.Api.dll"]
