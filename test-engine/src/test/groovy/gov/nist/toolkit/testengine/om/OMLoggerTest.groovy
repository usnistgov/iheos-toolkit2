package gov.nist.toolkit.testengine.om


import gov.nist.toolkit.utilities.xml.OMFormatter
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import org.apache.axiom.om.OMException
import spock.lang.Specification

class OMLoggerTest extends Specification {

    def 'test original'() {
        given:
        def msg = '''
<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://www.w3.org/2003/05/soap-envelope' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/o0asis-200401-wss-wssecurity-secext-1.0.xsd' xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>
    <SOAP-ENV:Header>
        <wsa:Action>urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
        <wsa:MessagelD>urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>>
        <wsa:RelatesTo>e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
        <wsa:To>http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
        <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7">
                <Created>2019-02-14T02:18 : 58Z</Created>
                <Expires>2019-02-14T02:23:58Z</Expires>
            </Timestamp>
            <SignatureConfirmation xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa" wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7" Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW="/>
        </Security>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
        <PRPA_IN201306UV02 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3" xmlns:hl7="urn:hl7-org.v3" ITSVersion="XML_1.0" />
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
    '''
        when:
        OMElement orig = Util.parse_xml(msg)
        println orig.toString()


        def log = new OMFormatter(orig).toString()
        println log

        then:
        true
    }

    def 'test undeclared namespace prefix'() {
        /* Caused by: com.ctc.wstx.exc.WstxParsingException: Undeclared namespace prefix "wsu" (for attribute "Id") */
        given:
        def msg = '''
<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://www.w3.org/2003/05/soap-envelope' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/o0asis-200401-wss-wssecurity-secext-1.0.xsd' xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>
    <SOAP-ENV:Header>
        <wsa:Action>urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
        <wsa:MessagelD>urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>>
        <wsa:RelatesTo>e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
        <wsa:To>http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
        <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7">
                <Created>2019-02-14T02:18 : 58Z</Created>
                <Expires>2019-02-14T02:23:58Z</Expires>
            </Timestamp>
            <SignatureConfirmation xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa" wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7" Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW="/>
        </Security>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
        <!--<PRPA_IN201306UV02 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3" xmlns:hl7="urn:hl7-org.v3" ITSVersion="XML_1.0" />-->
        <PRPA_IN201306UV02 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 ../../xsd/NE2008/multicacheschemas/PRPA_IN201306UV02.xsd" xmlns="urn:hl7-org:v3" ITSVersion="XML_1.0">
	<id root="57e77e59-1743-4d36-a8a2-de1735793e52"/>
	<creationTime value="20110316161212"/>
	<interactionId extension="PRPA_IN201306UV02" root="2.16.840.1.113883.1.6"/>
	<processingCode code="T"/>
	<processingModeCode code="T"/>
	<acceptAckCode code="NE"/>
	<receiver typeCode="RCV">
		<device classCode="DEV" determinerCode="INSTANCE">
			<id root="2.16.840.1.113883.3.72.6.4"/>
			<asAgent classCode="AGNT">
				<representedOrganization classCode="ORG" determinerCode="INSTANCE">
					<id root="2.16.840.1.113883.3.72.6.1"/>
				</representedOrganization>
			</asAgent>
		</device>
	</receiver>
	<sender typeCode="SND">
		<device classCode="DEV" determinerCode="INSTANCE">
			<id root="2.16.840.1.113883.3.72.6.6.18"/>
			<asAgent classCode="AGNT">
				<representedOrganization classCode="ORG" determinerCode="INSTANCE">
					<id root="2.16.840.1.113883.3.72.6.1"/>
				</representedOrganization>
			</asAgent>
		</device>
	</sender>
	<acknowledgement>
		<typeCode code="AA"/>
		<targetMessage>
			<id extension="35423" root="1.2.840.114350.1.13.0.1.7.1.1"/>
		</targetMessage>
	</acknowledgement>
	<controlActProcess classCode="CACT" moodCode="EVN">
		<code code="PRPA_TE201306UV02" codeSystem="2.16.840.1.113883.1.6"/>
		<subject typeCode="SUBJ">
			<registrationEvent classCode="REG" moodCode="EVN">
				<id nullFlavor="NA"/>
				<statusCode code="active"/>
				<subject1 typeCode="SBJ">
					<patient classCode="PAT">
						<id assigningAuthorityName="domain1" extension="801" root="1.2.3.4.5.1000"/>
						<statusCode code="active"/>
						<patientPerson>
							<name>
								<given>Chip</given>
								<family>Moore</family>
							</name>
							<administrativeGenderCode code="M"/>
							<birthTime value="19849711"/>
							<addr>
								<city>Montreal</city>
							</addr>
						</patientPerson>
						<providerOrganization classCode="NAT" determinerCode="INSTANCE">
							<id root="2.16.840.1.113883.3.333"/>
							<contactParty classCode="CON" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
						</providerOrganization>
						<subjectOf1>
							<queryMatchObservation moodCode="EVN" classCode="ALRT">
								<code code="IHE_PDQ"/>
								<value xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="INT" value="100"/>
							</queryMatchObservation>
						</subjectOf1>
					</patient>
				</subject1>
				<custodian typeCode="CST">
					<assignedEntity classCode="ASSIGNED">
						<id/>
					</assignedEntity>
				</custodian>
			</registrationEvent>
		</subject>
		<queryAck>
			<queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999"/>
			<queryResponseCode code="OK"/>
			<resultTotalQuantity nullFlavor="NA"/>
			<resultCurrentQuantity nullFlavor="NA"/>
			<resultRemainingQuantity nullFlavor="NA"/>
		</queryAck>
		<queryByParameter>
			<queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999"/>
			<statusCode code="new"/>
			<responseModalityCode code="R"/>
			<responsePriorityCode code="I"/>
			<parameterList>
				<!-- Gender (Required, one and unique)-->
				<livingSubjectAdministrativeGender>
					<value code="M"/>
					<semanticsText representation="TXT">LivingSubject.administrativeGender</semanticsText>
				</livingSubjectAdministrativeGender>
				<!-- Birthday (Required, one and unique)-->
				<livingSubjectBirthTime>
					<value value="19849711"/>
					<semanticsText>LivingSubject.birthTime</semanticsText>
				</livingSubjectBirthTime>
				<!-- Name (This element is required, one or multiple. The first given and family name is required, second given name is not required, neither suffix) -->
				<livingSubjectName>
					<value>
						<given partType="GIV">Chip</given>
						<family partType="FAM">Moore</family>
					</value>
					<semanticsText representation="TXT">LivingSubject.name</semanticsText>
				</livingSubjectName>
			</parameterList>
		</queryByParameter>
	</controlActProcess>
</PRPA_IN201306UV02>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
    '''
        and:
        OMElement o = Util.parse_xml(msg)
        when:
//        println 'initial stage ' + new OMFormatter(msg).toString()
        // This is the serialization at the point when header is stored in Soap.java inHeader = Util.deep_copy(in.getEnvelope().getHeader())
        OMElement header = o.getChildrenWithLocalName("Header").next()

        // Notice how the proximity of namespaces are positioned in this header.toString() (top-level scope vs current element scope)

        println "header.getQName()" // {http://www.w3.org/2003/05/soap-envelope}Header
        println header.getQName() // {http://www.w3.org/2003/05/soap-envelope}Header
        println "header.getLocalName()" // Header
        println header.getLocalName() // Header
        println "header.toString()"
        println header.toString()
        /*
        <SOAP-ENV:Header xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
        <wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
        <wsa:MessagelD xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>&gt;
        <wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
        <wsa:To xmlns:wsa="http://www.w3.org/2005/08/addressing">http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
        <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7">
                <Created>2019-02-14T02:18 : 58Z</Created>
                <Expires>2019-02-14T02:23:58Z</Expires>
            </Timestamp>
            <SignatureConfirmation xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7" Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW=" />
        </Security>
    </SOAP-ENV:Header>
         */

        OMElement headerCopy = Util.deep_copy(header)
        println "headerCopy.toString()"
        println headerCopy.toString()
        // Fixed exception when OMFormatter assumes Timestamp is also using "wsu" prefix but in this case, it uses a default namespace
        println "Util.deep_copy(headerCopy)"
        println Util.deep_copy(headerCopy)
        /*
        <SOAP-ENV:Header xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
   <wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
   <wsa:MessagelD xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>
   <wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
   <wsa:To xmlns:wsa="http://www.w3.org/2005/08/addressing">http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
   <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
      <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7">
         <Created>2019-02-14T02:18 : 58Z</Created>
         <Expires>2019-02-14T02:23:58Z</Expires>
      </Timestamp>
      <SignatureConfirmation xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7" Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW=" />
   </Security>
</SOAP-ENV:Header>
         */
        println "new OMFormatter(headerCopy).toString()"
        println new OMFormatter(headerCopy).toString()

        /*
<SOAP-ENV:Header xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
   <wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
   <wsa:MessagelD xmlns:wsa="http://www.w3.org/2005/08/addressing">urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>
   <wsa:RelatesTo xmlns:wsa="http://www.w3.org/2005/08/addressing">e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
   <wsa:To xmlns:wsa="http://www.w3.org/2005/08/addressing">http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
   <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
      <Timestamp wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
            xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
         <Created xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">2019-02-14T02:18 : 58Z</Created>
         <Expires xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">2019-02-14T02:23:58Z</Expires>
      </Timestamp>
      <SignatureConfirmation wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7"
            Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW=" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
            xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa"/>
   </Security>
</SOAP-ENV:Header>
         */

        Iterator it =  o.getChildrenWithLocalName("Body")
        if (it != null && it.hasNext()) {
            OMElement body = it.next()
            if (body != null) {
                println body.getQName() //
                println body.getLocalName() //
//                println body.toString()
                /*
                {http://www.w3.org/2003/05/soap-envelope}Body
Body
<SOAP-ENV:Body xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
        <!--<PRPA_IN201306UV02 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3" xmlns:hl7="urn:hl7-org.v3" ITSVersion="XML_1.0" />-->
        <PRPA_IN201306UV02 xmlns="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:hl7-org:v3 ../../xsd/NE2008/multicacheschemas/PRPA_IN201306UV02.xsd" ITSVersion="XML_1.0">
	<id root="57e77e59-1743-4d36-a8a2-de1735793e52" />
	<creationTime value="20110316161212" />
	<interactionId extension="PRPA_IN201306UV02" root="2.16.840.1.113883.1.6" />
	<processingCode code="T" />
	<processingModeCode code="T" />
	<acceptAckCode code="NE" />
	<receiver typeCode="RCV">
		<device classCode="DEV" determinerCode="INSTANCE">
			<id root="2.16.840.1.113883.3.72.6.4" />
			<asAgent classCode="AGNT">
				<representedOrganization classCode="ORG" determinerCode="INSTANCE">
					<id root="2.16.840.1.113883.3.72.6.1" />
				</representedOrganization>
			</asAgent>
		</device>
	</receiver>
	<sender typeCode="SND">
		<device classCode="DEV" determinerCode="INSTANCE">
			<id root="2.16.840.1.113883.3.72.6.6.18" />
			<asAgent classCode="AGNT">
				<representedOrganization classCode="ORG" determinerCode="INSTANCE">
					<id root="2.16.840.1.113883.3.72.6.1" />
				</representedOrganization>
			</asAgent>
		</device>
	</sender>
	<acknowledgement>
		<typeCode code="AA" />
		<targetMessage>
			<id extension="35423" root="1.2.840.114350.1.13.0.1.7.1.1" />
		</targetMessage>
	</acknowledgement>
	<controlActProcess classCode="CACT" moodCode="EVN">
		<code code="PRPA_TE201306UV02" codeSystem="2.16.840.1.113883.1.6" />
		<subject typeCode="SUBJ">
			<registrationEvent classCode="REG" moodCode="EVN">
				<id nullFlavor="NA" />
				<statusCode code="active" />
				<subject1 typeCode="SBJ">
					<patient classCode="PAT">
						<id assigningAuthorityName="domain1" extension="801" root="1.2.3.4.5.1000" />
						<statusCode code="active" />
						<patientPerson>
							<name>
								<given>Chip</given>
								<family>Moore</family>
							</name>
							<administrativeGenderCode code="M" />
							<birthTime value="19849711" />
							<addr>
								<city>Montreal</city>
							</addr>
						</patientPerson>
						<providerOrganization classCode="NAT" determinerCode="INSTANCE">
							<id root="2.16.840.1.113883.3.333" />
							<contactParty classCode="CON" xsi:nil="true" />
						</providerOrganization>
						<subjectOf1>
							<queryMatchObservation moodCode="EVN" classCode="ALRT">
								<code code="IHE_PDQ" />
								<value xsi:type="INT" value="100" />
							</queryMatchObservation>
						</subjectOf1>
					</patient>
				</subject1>
				<custodian typeCode="CST">
					<assignedEntity classCode="ASSIGNED">
						<id />
					</assignedEntity>
				</custodian>
			</registrationEvent>
		</subject>
		<queryAck>
			<queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999" />
			<queryResponseCode code="OK" />
			<resultTotalQuantity nullFlavor="NA" />
			<resultCurrentQuantity nullFlavor="NA" />
			<resultRemainingQuantity nullFlavor="NA" />
		</queryAck>
		<queryByParameter>
			<queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999" />
			<statusCode code="new" />
			<responseModalityCode code="R" />
			<responsePriorityCode code="I" />
			<parameterList>
				<!-- Gender (Required, one and unique)-->
				<livingSubjectAdministrativeGender>
					<value code="M" />
					<semanticsText representation="TXT">LivingSubject.administrativeGender</semanticsText>
				</livingSubjectAdministrativeGender>
				<!-- Birthday (Required, one and unique)-->
				<livingSubjectBirthTime>
					<value value="19849711" />
					<semanticsText>LivingSubject.birthTime</semanticsText>
				</livingSubjectBirthTime>
				<!-- Name (This element is required, one or multiple. The first given and family name is required, second given name is not required, neither suffix) -->
				<livingSubjectName>
					<value>
						<given partType="GIV">Chip</given>
						<family partType="FAM">Moore</family>
					</value>
					<semanticsText representation="TXT">LivingSubject.name</semanticsText>
				</livingSubjectName>
			</parameterList>
		</queryByParameter>
	</controlActProcess>
</PRPA_IN201306UV02>
    </SOAP-ENV:Body>
                 */

                OMElement bodyCopy = Util.deep_copy(body)
                println "bodyCopy.toString()"
                println bodyCopy.toString()
                println "Util.deep_copy(bodyCopy)"
                println Util.deep_copy(bodyCopy)
                println "new OMFormatter(bodyCopy).toString()"
                println new OMFormatter(bodyCopy).toString()
                /*

<SOAP-ENV:Body xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope">
   <PRPA_IN201306UV02 xsi:schemaLocation="urn:hl7-org:v3 ../../xsd/NE2008/multicacheschemas/PRPA_IN201306UV02.xsd"
         ITSVersion="XML_1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="urn:hl7-org:v3">
      <id root="57e77e59-1743-4d36-a8a2-de1735793e52"
            xmlns="urn:hl7-org:v3"/>
      <creationTime value="20110316161212" xmlns="urn:hl7-org:v3"/>
      <interactionId extension="PRPA_IN201306UV02"
            root="2.16.840.1.113883.1.6" xmlns="urn:hl7-org:v3"/>
      <processingCode code="T" xmlns="urn:hl7-org:v3"/>
      <processingModeCode code="T" xmlns="urn:hl7-org:v3"/>
      <acceptAckCode code="NE" xmlns="urn:hl7-org:v3"/>
      <receiver typeCode="RCV" xmlns="urn:hl7-org:v3">
         <device classCode="DEV" determinerCode="INSTANCE"
               xmlns="urn:hl7-org:v3">
            <id root="2.16.840.1.113883.3.72.6.4" xmlns="urn:hl7-org:v3"/>
            <asAgent classCode="AGNT" xmlns="urn:hl7-org:v3">
               <representedOrganization classCode="ORG"
                     determinerCode="INSTANCE" xmlns="urn:hl7-org:v3">
                  <id root="2.16.840.1.113883.3.72.6.1"
                        xmlns="urn:hl7-org:v3"/>
               </representedOrganization>
            </asAgent>
         </device>
      </receiver>
      <sender typeCode="SND" xmlns="urn:hl7-org:v3">
         <device classCode="DEV" determinerCode="INSTANCE"
               xmlns="urn:hl7-org:v3">
            <id root="2.16.840.1.113883.3.72.6.6.18"
                  xmlns="urn:hl7-org:v3"/>
            <asAgent classCode="AGNT" xmlns="urn:hl7-org:v3">
               <representedOrganization classCode="ORG"
                     determinerCode="INSTANCE" xmlns="urn:hl7-org:v3">
                  <id root="2.16.840.1.113883.3.72.6.1"
                        xmlns="urn:hl7-org:v3"/>
               </representedOrganization>
            </asAgent>
         </device>
      </sender>
      <acknowledgement xmlns="urn:hl7-org:v3">
         <typeCode code="AA" xmlns="urn:hl7-org:v3"/>
         <targetMessage xmlns="urn:hl7-org:v3">
            <id extension="35423" root="1.2.840.114350.1.13.0.1.7.1.1"
                  xmlns="urn:hl7-org:v3"/>
         </targetMessage>
      </acknowledgement>
      <controlActProcess classCode="CACT" moodCode="EVN"
            xmlns="urn:hl7-org:v3">
         <code code="PRPA_TE201306UV02" codeSystem="2.16.840.1.113883.1.6"
               xmlns="urn:hl7-org:v3"/>
         <subject typeCode="SUBJ" xmlns="urn:hl7-org:v3">
            <registrationEvent classCode="REG" moodCode="EVN"
                  xmlns="urn:hl7-org:v3">
               <id nullFlavor="NA" xmlns="urn:hl7-org:v3"/>
               <statusCode code="active" xmlns="urn:hl7-org:v3"/>
               <subject1 typeCode="SBJ" xmlns="urn:hl7-org:v3">
                  <patient classCode="PAT" xmlns="urn:hl7-org:v3">
                     <id assigningAuthorityName="domain1"
                           extension="801" root="1.2.3.4.5.1000"
                           xmlns="urn:hl7-org:v3"/>
                     <statusCode code="active" xmlns="urn:hl7-org:v3"/>
                     <patientPerson xmlns="urn:hl7-org:v3">
                        <name xmlns="urn:hl7-org:v3">
                           <given xmlns="urn:hl7-org:v3">Chip</given>
                           <family xmlns="urn:hl7-org:v3">Moore</family>
                        </name>
                        <administrativeGenderCode code="M"
                              xmlns="urn:hl7-org:v3"/>
                        <birthTime value="19849711"
                              xmlns="urn:hl7-org:v3"/>
                        <addr xmlns="urn:hl7-org:v3">
                           <city xmlns="urn:hl7-org:v3">Montreal</city>
                        </addr>
                     </patientPerson>
                     <providerOrganization classCode="NAT"
                           determinerCode="INSTANCE"
                           xmlns="urn:hl7-org:v3">
                        <id root="2.16.840.1.113883.3.333"
                              xmlns="urn:hl7-org:v3"/>
                        <contactParty classCode="CON"
                              xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xmlns="urn:hl7-org:v3"/>
                     </providerOrganization>
                     <subjectOf1 xmlns="urn:hl7-org:v3">
                        <queryMatchObservation moodCode="EVN"
                              classCode="ALRT" xmlns="urn:hl7-org:v3">
                           <code code="IHE_PDQ" xmlns="urn:hl7-org:v3"/>
                           <value xsi:type="INT" value="100" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                 xmlns="urn:hl7-org:v3"/>
                        </queryMatchObservation>
                     </subjectOf1>
                  </patient>
               </subject1>
               <custodian typeCode="CST" xmlns="urn:hl7-org:v3">
                  <assignedEntity classCode="ASSIGNED"
                        xmlns="urn:hl7-org:v3">
                     <id xmlns="urn:hl7-org:v3"/>
                  </assignedEntity>
               </custodian>
            </registrationEvent>
         </subject>
         <queryAck xmlns="urn:hl7-org:v3">
            <queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999"
                  xmlns="urn:hl7-org:v3"/>
            <queryResponseCode code="OK" xmlns="urn:hl7-org:v3"/>
            <resultTotalQuantity nullFlavor="NA" xmlns="urn:hl7-org:v3"/>
            <resultCurrentQuantity nullFlavor="NA"
                  xmlns="urn:hl7-org:v3"/>
            <resultRemainingQuantity nullFlavor="NA"
                  xmlns="urn:hl7-org:v3"/>
         </queryAck>
         <queryByParameter xmlns="urn:hl7-org:v3">
            <queryId extension="3809840989" root="1.2.840.114350.1.13.28.1.18.5.999"
                  xmlns="urn:hl7-org:v3"/>
            <statusCode code="new" xmlns="urn:hl7-org:v3"/>
            <responseModalityCode code="R" xmlns="urn:hl7-org:v3"/>
            <responsePriorityCode code="I" xmlns="urn:hl7-org:v3"/>
            <parameterList xmlns="urn:hl7-org:v3">
               <livingSubjectAdministrativeGender xmlns="urn:hl7-org:v3">
                  <value code="M" xmlns="urn:hl7-org:v3"/>
                  <semanticsText representation="TXT"
                        xmlns="urn:hl7-org:v3">LivingSubject.administrativeGender</semanticsText>
               </livingSubjectAdministrativeGender>
               <livingSubjectBirthTime xmlns="urn:hl7-org:v3">
                  <value value="19849711" xmlns="urn:hl7-org:v3"/>
                  <semanticsText xmlns="urn:hl7-org:v3">LivingSubject.birthTime</semanticsText>
               </livingSubjectBirthTime>
               <livingSubjectName xmlns="urn:hl7-org:v3">
                  <value xmlns="urn:hl7-org:v3">
                     <given partType="GIV" xmlns="urn:hl7-org:v3">Chip</given>
                     <family partType="FAM" xmlns="urn:hl7-org:v3">Moore</family>
                  </value>
                  <semanticsText representation="TXT"
                        xmlns="urn:hl7-org:v3">LivingSubject.name</semanticsText>
               </livingSubjectName>
            </parameterList>
         </queryByParameter>
      </controlActProcess>
   </PRPA_IN201306UV02>
</SOAP-ENV:Body>

                 */

            }
        }


        then:
//        def e = thrown(OMException)
//        print e.printStackTrace()
//        println e.cause
//        e.cause.toString().contains('Undeclared namespace prefix')
//        println 'copied header: ' + Util.deep_copy(headerCopy).toString()
        println 'Done.'

    }

    /*
                <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7" xmlns:wsu="http://xxdocs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsu='http://yydocs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>

 causes

org.apache.axiom.om.OMException: com.ctc.wstx.exc.WstxParsingException: Duplicate declaration for namespace prefix 'wsu'.
 at [row,col {unknown-source}]: [9,278]

	at org.apache.axiom.om.impl.builder.StAXOMBuilder.next(StAXOMBuilder.java:293)
	at org.apache.axiom.om.impl.llom.OMElementImpl.getNextOMSibling(OMElementImpl.java:337)
	at org.apache.axiom.om.impl.traverse.OMChildrenIterator.next(OMChildrenIterator.java:102)
	at org.apache.axiom.om.impl.llom.OMElementImpl.internalSerialize(OMElementImpl.java:952)
	at org.apache.axiom.om.impl.llom.OMSerializableImpl.serialize(OMSerializableImpl.java:125)
	at org.apache.axiom.om.impl.llom.OMSerializableImpl.serialize(OMSerializableImpl.java:113)
	at org.apache.axiom.om.impl.llom.OMElementImpl.toString(OMElementImpl.java:1088)
	at gov.nist.toolkit.testengine.om.OMLoggerTest.test original(OMLoggerTest.groovy:34)
Caused by: com.ctc.wstx.exc.WstxParsingException: Duplicate declaration for namespace prefix 'wsu'.
 at [row,col {unknown-source}]: [9,278]
	at com.ctc.wstx.sr.StreamScanner.constructWfcException(StreamScanner.java:630)
	at com.ctc.wstx.sr.StreamScanner.throwParseError(StreamScanner.java:461)
	at com.ctc.wstx.sr.BasicStreamReader.handleNsAttrs(BasicStreamReader.java:3022)
	at com.ctc.wstx.sr.BasicStreamReader.handleStartElem(BasicStreamReader.java:2936)
	at com.ctc.wstx.sr.BasicStreamReader.nextFromTree(BasicStreamReader.java:2848)
	at com.ctc.wstx.sr.BasicStreamReader.next(BasicStreamReader.java:1019)
	at org.apache.axiom.om.impl.builder.StAXOMBuilder.parserNext(StAXOMBuilder.java:672)
	at org.apache.axiom.om.impl.builder.StAXOMBuilder.next(StAXOMBuilder.java:214)
	... 7 more
     */

    /***
     * NOT USED
    def 'test duplication declaration exception' () {
       given:
        def msg = '''
<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://www.w3.org/2003/05/soap-envelope' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:s='http://www.w3.org/2001/XMLSchema' xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/o0asis-200401-wss-wssecurity-secext-1.0.xsd' xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>
    <SOAP-ENV:Header>
        <wsa:Action>urn:hl7-org:v3:PRPA_IN201306U0V02:CrossGatewayPatientDiscovery</wsa:Action>
        <wsa:MessagelD>urn:uuid:E2E5DD4A-2FFE-11E9-A926-005056A6P8F7</wsa:MessagelD>>
        <wsa:RelatesTo>e70ba0ff7-4075-4eca-bec8-444b7da25446</wsa:RelatesTo>
        <wsa:To>http://www.w3.org/2005/08/addressing/anonymous</wsa:To>
        <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <Timestamp xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="Timestamp-E2E552P8-2FFE-11E9-A926-O05056A6F8F7" xmlns:wsu='http://xxxdocs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd' xmlns:wsu='http://yyydocs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'>
                <Created>2019-02-14T02:18 : 58Z</Created>
                <Expires>2019-02-14T02:23:58Z</Expires>
            </Timestamp>
            <SignatureConfirmation xmlns="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsqa" wsu:Id="Id-E2E5562C-2PPE-11E9-A926-O05056A6F8F7" Value="gOv6FWOOVUT6r3JtC2JbJ2kPmws/TPOtGwEnH6cZMpqPjc6/jBBGdY4vzveyjbLgtG92azhxDNVZojR0eX60Bwb1GVM/LdShhvwkahUBCrN9/bzTXLdoWwQX24y3pvykj+0Ae6w2yDYV5ACSvks1E+0M4Y1H2BDUEbg8+ELbvDhUjoErLemJaNGCPZ9LCXZ/1qBDR0itelSMOHCUnAc4rTsKRiP04/2S80KA2aElvugh8J8tBRMS5WArDgmQTzc8Dof4UCsf4FfABCU7TKhozA04vM5wGHh1 Prcw6MQ/ZvDqr5EDSi9XOPAdX00C1zWq3jvUpTz2QvVdSQEP2jDWHV4EW="/>
        </Security>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
        <PRPA_IN201306UV02 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3" xmlns:hl7="urn:hl7-org.v3" ITSVersion="XML_1.0" />
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
    '''

        when:
        OMElement o = Util.parse_xml(msg)
        println 'initial stage ' + new OMFormatter(msg).toString()
        then:
        def e = thrown(OMException)
        println e.cause
        e.cause.toString().contains('Duplicate declaration for namespace prefix')

//        OMElement header = o.getChildrenWithLocalName("Header").next()
//        String headerString = header.toString() //.replace('xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"')
//        println 'orig header extract: ' + new OMFormatter(headerString).toString()
//        OMElement headerCopy = Util.deep_copy(header)
//        println 'copied header: ' + Util.deep_copy(headerCopy).toString()

    }
*/
}
