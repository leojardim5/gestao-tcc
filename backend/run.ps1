Write-Host "=== FLYWAY REPAIR ===" -ForegroundColor Yellow
.\mvnw.cmd flyway:repair `
  "-Dflyway.url=jdbc:postgresql://localhost:5432/gestaotcc_db" `
  "-Dflyway.user=postgres" `
  "-Dflyway.password=1234"

Write-Host "=== FLYWAY CLEAN ===" -ForegroundColor Yellow
.\mvnw.cmd flyway:clean `
  "-Dflyway.cleanDisabled=false" `
  "-Dflyway.url=jdbc:postgresql://localhost:5432/gestaotcc_db" `
  "-Dflyway.user=postgres" `
  "-Dflyway.password=1234"

Write-Host "=== FLYWAY MIGRATE ===" -ForegroundColor Yellow
.\mvnw.cmd flyway:migrate `
  "-Dflyway.url=jdbc:postgresql://localhost:5432/gestaotcc_db" `
  "-Dflyway.user=postgres" `
  "-Dflyway.password=1234"

Write-Host "=== BUILDING PROJECT ===" -ForegroundColor Yellow
.\mvnw.cmd clean package

Write-Host "=== RUNNING APPLICATION ===" -ForegroundColor Green
java -jar "target\gestaotcc-backend-0.0.1-SNAPSHOT.jar"
