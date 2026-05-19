param(
    [string]$Profile,

    [string]$ProjectPath = "./privgit"
)

$validProfiles = @("dev", "prod")

if (-not $Profile -or $validProfiles -notcontains $Profile) {
    Write-Host ""
    Write-Host "Usage: .\run.ps1 <dev|prod> [-ProjectPath path]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\run.ps1 dev"
    Write-Host "  .\run.ps1 prod"
    Write-Host ""

    exit 1
}

# Save original directory
$originalLocation = Get-Location

try {
    Set-Location $ProjectPath

    Write-Host "Running Spring Boot with profile: $Profile"

    mvn package clean

    mvn spring-boot:run "-Dspring-boot.run.profiles=$Profile"
}
finally {
    # Restore to original directory
    Set-Location $originalLocation
}