# Re-sign a patched Dewd Dynamic RRO with AOSP platform test keys (required for
# system_ext overlay scan). Keys match aosp_tangorpro_car / DewdDynamicAospRRO.orig.
param(
    [Parameter(Mandatory = $true)][string]$InputApk,
    [Parameter(Mandatory = $true)][string]$OutputApk
)

$ErrorActionPreference = "Stop"
$Root = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$KeyDir = Join-Path $Root "scalable-ui-rro\prebuilt\aosp-platform"
New-Item -ItemType Directory -Force -Path $KeyDir | Out-Null
$Pk8 = Join-Path $KeyDir "platform.pk8"
$Pem = Join-Path $KeyDir "platform.x509.pem"

if (-not (Test-Path $Pk8) -or -not (Test-Path $Pem)) {
    $base = "https://raw.githubusercontent.com/aosp-mirror/platform_build/master/target/product/security"
    Invoke-WebRequest -Uri "$base/platform.pk8" -OutFile $Pk8
    Invoke-WebRequest -Uri "$base/platform.x509.pem" -OutFile $Pem
}

$sdk = $env:ANDROID_HOME
if (-not $sdk) { $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$apksigner = Get-ChildItem "$sdk\build-tools" -Recurse -Filter apksigner.bat |
    Sort-Object FullName -Descending | Select-Object -First 1
if (-not $apksigner) { throw "apksigner not found under $sdk\build-tools" }

$tmp = "$OutputApk.signing-tmp.apk"
& $apksigner.FullName sign --key $Pk8 --cert $Pem --out $tmp $InputApk
if ($LASTEXITCODE -ne 0) { throw "apksigner sign failed" }
& $apksigner.FullName verify --verbose $tmp | Out-Null
if ($LASTEXITCODE -ne 0) { throw "apksigner verify failed" }
Move-Item -Force $tmp $OutputApk
Write-Host "Signed Dewd RRO → $OutputApk"
