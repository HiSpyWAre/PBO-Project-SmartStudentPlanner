# run.ps1
$JAVAFX_PATH = "D:\JavaFX\javafx-sdk-17.0.17\lib"  # ← SESUAIKAN PATH INI
$SQLITE_JAR = "lib\sqlite-jdbc-3.44.1.0.jar"

Write-Host "🔨 Compiling..." -ForegroundColor Yellow

# Clean output
if (Test-Path out) {
    Remove-Item -Recurse -Force out
}
New-Item -ItemType Directory -Force -Path out | Out-Null

# Check JavaFX path exists
if (-not (Test-Path $JAVAFX_PATH)) {
    Write-Host "❌ JavaFX path not found: $JAVAFX_PATH" -ForegroundColor Red
    Write-Host "Please update JAVAFX_PATH in run.ps1" -ForegroundColor Yellow
    exit 1
}

# Compile
javac -encoding UTF-8 `
      --module-path $JAVAFX_PATH `
      --add-modules javafx.controls,javafx.fxml `
      -cp $SQLITE_JAR `
      -d out `
      src/model/*.java `
      src/ui/*.java `
      src/controller/*.java `
      src/StudyPlannerApp.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Compilation successful!" -ForegroundColor Green

# Copy resources
if (Test-Path "src/resources/styles.css") {
    Copy-Item "src/resources/styles.css" "out/styles.css" -Force
}

Write-Host "🚀 Running application..." -ForegroundColor Yellow

# Run with FULL module path
java --module-path $JAVAFX_PATH `
     --add-modules javafx.controls,javafx.fxml `
     -cp "out;$SQLITE_JAR" `
     StudyPlannerApp

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Application crashed!" -ForegroundColor Red
    exit 1
}
# # run.ps1
# $JAVAFX_PATH = "D:\JavaFX\javafx-sdk-17.0.17\lib"
# # $SQLITE_JAR = "lib\sqlite-jdbc-3.44.1.0.jar"

# Write-Host "🔨 Compiling..." -ForegroundColor Yellow

# # Clean and create output directory
# if (Test-Path out) {
#     Remove-Item -Recurse -Force out
# }
# New-Item -ItemType Directory -Force -Path out | Out-Null

# # Compile all Java files with UTF-8 encoding
# javac -encoding UTF-8 `
#       --module-path $JAVAFX_PATH `
#       --add-modules javafx.controls,javafx.fxml `
#       -d out `
#       src/model/*.java `
#       src/ui/*.java `
#       src/controller/*.java `
#       src/StudyPlannerApp.java

# if ($LASTEXITCODE -ne 0) {
#     Write-Host "❌ Compilation failed!" -ForegroundColor Red
#     exit 1
# }

# Write-Host "✅ Compilation successful!" -ForegroundColor Green

# # Copy resources to multiple locations (to be safe)
# if (Test-Path "src/resources/styles.css") {
#     Copy-Item "src/resources/styles.css" "out/styles.css" -Force
#     Write-Host "📦 Resources copied to out/styles.css" -ForegroundColor Cyan
    
#     # Also create resources subfolder
#     New-Item -ItemType Directory -Force -Path "out/resources" | Out-Null
#     Copy-Item "src/resources/styles.css" "out/resources/styles.css" -Force
#     Write-Host "📦 Resources copied to out/resources/styles.css" -ForegroundColor Cyan
# } else {
#     Write-Host "⚠️  Warning: styles.css not found - app will run without styling" -ForegroundColor Yellow
# }

# Write-Host "🚀 Running application..." -ForegroundColor Yellow

# # Run the application
# java --module-path $JAVAFX_PATH `
#      --add-modules javafx.controls,javafx.fxml `
#      -cp out `
#      StudyPlannerApp

# if ($LASTEXITCODE -ne 0) {
#     Write-Host "❌ Application crashed!" -ForegroundColor Red
#     exit 1
# }