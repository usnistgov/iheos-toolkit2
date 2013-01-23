	BEGIN { FS = ":" }
$1 == "direct.toolkit.dns.domain" { 
		gsub(/[ \t]+$/, "", $NF);
		gsub(/^[ \t]+/, "", $NF);
		print $NF 
	}
