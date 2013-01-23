	BEGIN { FS = "=" }
$1 == "External_Cache" { print $NF }
