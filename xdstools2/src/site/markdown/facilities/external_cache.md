# External cache

These are the main sections of the external cache

    actors/
    environment/
    simdb/
    TestLogCache/
    
## actors

    actors/
        blue.xml
        red.xml



Collection of XML files that define the sites for toolkit.  A site has a name and a collection of IDs and
endpoints that define the configuration of a site.  A site can hold the configuration for a single actor of each
type.  So, if you have to Document Registries you need to define two sites. But if you have a Document Registry
and a Document Repository they can be configured in a single site.

Although a site file can be edited by hand it is usually edited with the Site/Actor Configuration tool.


![Cannot display Site/Actor Configuration tool graphic](../images/Site_tool.png)

The filename, blue.xml for example, does not define the site name as displayed in toolkit.  In this example, the 
name of the site and its display name is OTHER_NIST_BLUE_2016.

    <site name="OTHER_NIST_BLUE_2016">
       <home>urn:oid:1.3.6.1.4.1.21367.2011.2.6.152</home>
       <transaction name="ret.b" secure="1">https://nist26:9085/tf6/services/xdsrepositoryb</transaction>
       <transaction name="xcr" secure="1">https://nist26:9085/tf6/services/rg</transaction>
       <transaction name="pr.b" secure="1">https://nist26:9085/tf6/services/xdsrepositoryb</transaction>
       <transaction name="pr.b" secure="0">http://nist26:9080/tf6/services/xdsrepositoryb</transaction>
       <transaction name="sq.b" secure="0">http://nist26:9080/tf6/services/xdsregistryb</transaction>
       <transaction name="ret.b" secure="0">http://nist26:9080/tf6/services/xdsrepositoryb</transaction>
       <transaction name="r.b" secure="0">http://nist26:9080/tf6/services/xdsregistryb</transaction>
       <transaction name="sq.b" secure="1">https://nist26:9085/tf6/services/xdsregistryb</transaction>
       <transaction name="xcq" secure="0">http://nist26:9080/tf6/services/rg</transaction>
       <transaction name="igq" secure="1">https://nist26:9085/tf6/services/ig</transaction>
       <transaction name="r.b" secure="1">https://nist26:9085/tf6/services/xdsregistryb</transaction>
       <transaction name="igr" secure="0">http://nist26:9080/tf6/services/ig</transaction>
       <transaction name="igq" secure="0">http://nist26:9080/tf6/services/ig</transaction>
       <transaction name="igr" secure="1">https://nist26:9085/tf6/services/ig</transaction>
       <transaction name="xcq" secure="1">https://nist26:9085/tf6/services/rg</transaction>
       <transaction name="xcr" secure="0">http://nist26:9080/tf6/services/rg</transaction>
       <repository uid="1.3.6.1.4.1.21367.2011.2.3.232"
             secure="1">https://nist26:9085/tf6/services/xdsrepositoryb</repository>
       <repository uid="1.3.6.1.4.1.21367.2011.2.3.232"
             secure="0">http://nist26:9080/tf6/services/xdsrepositoryb</repository>
       <patientIdFeed host="null" port="null"/>
    </site>

## environment

An environment defines the code set used by the Affinity Domain and the certificate used by toolkit to 
initiate TLS transactions.  When toolkit receives transactions, in simulators, the certificate configuration 
is done in Tomcat.

The default environment, installed in the external cache when it is initialized by toolkit, looks like:

    environment/
               default/
                      codes.xml
                      
When we add the configuation for the North American Connectathon in 2016 the environment configuration looks like:

    environment/
               default/
                      codes.xml
               NA2016/
                      codes.xml
                      keystore/
                               keystore
                               keystore.properties
                               
The new directory, keystore, holds the Java keystore and the properties describing it. The contents of
keystore.password are:

    keyStorePassword=changeit

The directory names under the environment directory name the usable environments.  They are chosen at
the top of the tool pages where they are used.

![Not available](../images/environ_choice.png "Environment Selections")

## simdb

This is the database of simulator configurations and data. 


### Test Session

To understand simulator naming you must first understand the use of Test Session.  A Test Session is a separate
collection of data to support a testing situation.  Some think of Test Session as being the same as User.  But
toolkit does not perform user authentation and does not maintain information about users. Test Session is tied to
what you are testing and not who is doing the testing. For example, if you are testing two Document Registry 
implementations and you want to keep their test results separate you would use two Test Sessions.

A test session holds two types of data: 
conformance test results and simulators.  Each of these data types are maintained in different parts of the
external cache.

## simdb continued

The structure of simdb looks like:

    simdb/
          bill__rr/
                  reg/
                  rep/
                  rr/
                  sim_type.txt
                  simctl.ser
                  simId.txt
                  
bill__rr is the name of a simulator. bill is the name of the Test Session that owns the simulator.  rr is the
the local name (within the test session) of the simulator.  The full name (bill__rr) is always used as the
identifer.

sim_type.txt - holds the simulator type

simId.txt - holds the simulator ID (bill__rr in this example)

simctl.ser - holds the simulator configuration (binary file)

This particular simulator is a composite simulator.  It is constructed from multiple simpler simulators.  
This is a combined Document Repository and Document Registry.  The reg directory holds data for the Registry.
The rep directory holds data for the Repository.  The rr directory (simulator local name) holds data for 
managing the composition.

## TestLogCache

This directory holds the results of conformance tests. The structure of this directory looks like:

    TestLogCache/
         bill/
              11966/
                    11966
                    patientFeed/
                                log.xml
                    submit/
                                log.xml
                    eval/
                                log.xml
                           
The directory bill names the Test Session used to run the tests.

The directory 11966 names the test that was run.

The file 11966 is a binary form of the log files used by the Inspector.

patientFeed, submit, eval are test sections - the individual parts of the test.  Each section has its own 
log file, log.xml.

