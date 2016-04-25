Param(
    [Parameter(Mandatory=$true)]
	[string]$connectionString,
    
    [Parameter(Mandatory=$true)]
	[string]$clientId,
    
    [Parameter(Mandatory=$true)]
	[string]$clientSecret,
      
    [Parameter(Mandatory=$true)]
	[string]$configFile,
            
	[bool]$simulated
)

Import-Module $PSScriptRoot\Merge-Tokens.psm1

$content=(Get-Content $configFile | Merge-Tokens -tokens @{'AzureStorageConnectionString'=$connectionString; 'ClientId'=$clientId; 'ClientSecret'=$clientSecret; }) | Out-String;

if($simulated){
    Write-Output $content;
}
else {
    Out-File -FilePath $configFile -InputObject $content
}
