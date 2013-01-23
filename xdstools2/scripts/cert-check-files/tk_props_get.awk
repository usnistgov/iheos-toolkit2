	BEGIN { FS = ":" }
$1 == propname { 
		gsub(/[ \t]+$/, "", $NF);
		gsub(/^[ \t]+/, "", $NF);
		print $NF 
	}
