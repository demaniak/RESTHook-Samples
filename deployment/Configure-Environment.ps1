Param(
    [Parameter(Mandatory=$true)]
	[string]$connectionString,
    
    [Parameter(Mandatory=$true)]
	[string]$clientId,
    
    [Parameter(Mandatory=$true)]
	[string]$clientSecret,
      
    [Parameter(Mandatory=$true)]
	[string]$configTemplate,
    
    [Parameter(Mandatory=$true)]
	[string]$configFile,
            
	[int]$Port=4567,
    
    [string]$Url="http://localhost:4567/",
            
	[bool]$simulated
)

Import-Module $PSScriptRoot\Merge-Tokens.psm1

$content=(Get-Content $configTemplate | Merge-Tokens -tokens @{'AzureStorageConnectionString'=$connectionString; 'ClientId'=$clientId; 'ClientSecret'=$clientSecret; 'Url'=$Url; 'Port'=$Port });

if($simulated){
    Write-Output $content;
}
else {
    Out-File -FilePath $configFile -InputObject $content -Encoding "ascii"
}
