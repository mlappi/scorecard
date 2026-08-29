param(
    [string]$SourcePrefix = "data/hsqldb/devdb",
    [string]$DestinationPrefix = "deploy/public-seed/devdb"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$source = [System.IO.Path]::GetFullPath((Join-Path $projectRoot $SourcePrefix))
$destination = [System.IO.Path]::GetFullPath((Join-Path $projectRoot $DestinationPrefix))
$lockFile = "$source.lck"

if (Test-Path -LiteralPath $lockFile) {
    throw "Tietokanta on auki ($lockFile). Sammuta paikallinen sovellus ennen julkaisukannan muodostamista."
}

$dependencyDirectory = Join-Path $projectRoot "target/public-seed-tools"
& mvn -q dependency:copy-dependencies "-DincludeArtifactIds=hsqldb" "-DoutputDirectory=$dependencyDirectory"
if ($LASTEXITCODE -ne 0) {
    throw "HSQLDB-ajurin hakeminen epäonnistui."
}

$hsqldbJar = Get-ChildItem -LiteralPath $dependencyDirectory -Filter "hsqldb-*.jar" |
    Sort-Object Name -Descending |
    Select-Object -First 1
if ($null -eq $hsqldbJar) {
    throw "HSQLDB-ajuria ei löytynyt hakemistosta $dependencyDirectory."
}

Push-Location $projectRoot
try {
    & java --class-path $hsqldbJar.FullName tools/ExportPublicDatabase.java $source $destination
    if ($LASTEXITCODE -ne 0) {
        throw "Julkaisukannan muodostaminen epäonnistui."
    }
} finally {
    Pop-Location
}
