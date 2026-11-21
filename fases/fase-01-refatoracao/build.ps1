# Compila a fase 1 para Java 17. Com -Run, executa o jogo depois de compilar.
param([switch]$Run)

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (Test-Path out) { Remove-Item -Recurse -Force out }
$fontes = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName

# --release 17 barra qualquer API do Java 21+ mesmo compilando com um JDK mais novo
javac --release 17 -encoding UTF-8 -d out $fontes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Compilado em out/ ($($fontes.Count) arquivos)."

if ($Run) {
    java -cp out br.edu.unifor.missaomarte.Main
}
