#!/bin/bash 


function separator() { echo ""; echo "************************************************************" ;}

separator

war=..
#cat $war/WEB-INF/toolkit.properties

########################################################################
echo "Looking in $war/WEB-INF/toolkit.properties to find External Cache location"
EC=`awk -f cert-check-files/external_cache.awk < $war/WEB-INF/toolkit.properties`
echo "External Cache is <$EC>" 

if [ ! -d $EC ]
then
	echo "External Cache directory does not exist"
	exit
fi

########################################################################
echo ""
echo "Looking in tk_props.txt file ($EC/tk_props.txt) for DNS domain name"
DOMAIN=`awk -f cert-check-files/domain_name.awk < $EC/tk_props.txt`
echo "DOMAIN is <$DOMAIN>"

if [[ "$DOMAIN" == "" ]]
then
	echo "No DNS domain name found"
	exit
fi

separator

########################################################################
echo "Can encrypt using public key for sending to the following domains:"
pub_enc_keys=`ls $EC/direct/encrypt_certs`
for key in $pub_enc_keys
do
	if [[ "$key" == *.der ]]
	then
		echo "    " `basename $key .der`
	fi
done
echo "which are represented by der files in $EC/direct/encrypt_certs"

separator

########################################################################
echo "Checking config for signing messages..."
if [[ -e $EC/direct/signing_cert/$DOMAIN.p12 ]]
then
	echo "Good, Found private cert for signing for domain $DOMAIN: " 
	echo "    " $EC/direct/signing_cert/$DOMAIN.p12
else
	echo "Bad, Did not find private cert for signing for domain $DOMAIN: " 
	echo "    " $EC/direct/signing_cert/$DOMAIN.p12
fi

if [[ -e $EC/direct/signing_cert/password.txt ]]
then
	echo "Good, Found password file for private cert for signing for domain $DOMAIN: " 
	echo "    " $EC/direct/signing_cert/password.txt
else
	echo "Bad, Did not password file for private cert for signing for domain $DOMAIN: " 
	echo "    " $EC/direct/signing_cert/password.txt
fi

separator

########################################################################
echo "Examining the public certs we offer for download from TTT..."
echo "   Looking in $war/pubcert..."
pubpubcert=`awk -v propname=direct.pubcert.pubcert -f cert-check-files/tk_props_get.awk < $EC/tk_props.txt`
if [[ -e $war/pubcert/$pubpubcert ]]
then
	echo "     Good, found $pubpubcert"
	echo "     which seems to be the public cert for this domain"
else
	echo "     Bad, did not find $pubpubcert"
	echo "     which was expected for this domain"
fi

if [[ "$DOMAIN.der" != "$pubpubcert" ]]
then
	echo ""
	echo "     OOPS, the public cert or this domain should have the filename $DOMAIN.der"
fi

echo ""
anchorcert=`awk -v propname=direct.pubcert.trustanchor -f cert-check-files/tk_props_get.awk < $EC/tk_props.txt`
if [[ -e $war/pubcert/$anchorcert ]]
then
	echo "     Good, found $anchorcert"
	echo "     which seems to be the trust anchor cert for this domain"
else
	echo "     Bad, did not find $anchorcert"
	echo "     which was expected for this domain"
fi

if [[ `basename $anchorcert .der` == $anchorcert ]]
then
	echo ""
	echo "     Bad, anchor cert must be in der format and have .der extension"
fi

echo ""
invanchorcert=`awk -v propname=direct.pubcert.invtrustrelanchor -f cert-check-files/tk_props_get.awk < $EC/tk_props.txt`
if [[ -e $war/pubcert/$invanchorcert ]]
then
	echo "     Good, found $invanchorcert"
	echo "     which seems to be the invalid relationship trust anchor cert"
else
	echo "     Bad, did not find $invanchorcert"
	echo "     which was expected"
fi

if [[ `basename $invanchorcert .der` == $invanchorcert ]]
then
	echo ""
	echo "     Bad, invalid truct relationship anchor cert must be in der format and have .der extension"
fi

separator

########################################################################
echo "Checking private key used for decryption..."
echo "    Looking in $war/WEB-INF/privcert"
if [[ `ls -1 $war/WEB-INF/privcert | wc -l` -eq 1 ]]
then
	echo "    Good, only a single file found"
	single=1
else
	echo "    Bad, should only find a single file here"
	echo "      Found `ls -1 $war/WEB-INF/privcert`"
	single=0
fi

if [[ "$single" == 1 ]] 
then
	if [[ `basename $war/WEB-INF/privcert/* .p12` == "$war/WEB-INF/privcert/*" ]] 
	then
		echo "    Bad, private key file must be in .p12 format"
		echo "      Found `ls -1 $war/WEB-INF/privcert`"
	else
		echo "    Good, .p12 format found"
		echo "      Found `ls -1 $war/WEB-INF/privcert`"
	fi
fi


separator 
