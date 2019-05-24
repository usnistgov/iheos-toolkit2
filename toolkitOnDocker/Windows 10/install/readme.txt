Readme for Windows 10
The purpose of this Readme is to guide the user in setting up a new instance of Toolkit on Docker for Windows 10. 

File Descriptions i.e., The files available to the Docker context
cert/from-eu-gss-all-purpose-3129 - Certificate #3129 as issued by the European Gazelle Security Suite (GSS). This certificate has the following SubjectAltNames: host.xdstoolkit.test, sut.xdstoolkit.test, latest.xdstoolkit.test, a.xdstoolkit.test, b.xdstoolkit.test, c.xdstoolkit.test. The "host" and the "sut" are intended for the systems running on the Docker Host while all others are reserved for Toolkit on Docker.

buildImage.bat - Batch command file that makes use of the Docker build command and the Docker create command. Line separators must be CRLF.

Dockerfile - Used by Docker build.

Docker issues.txt - Some of the Windows-specific Docker issues encountered during our installation and suggested solutions.

readme.txt - This file.

toolkit.properties - Toolkit.properties file. Note: This file uses UNIX style LF newlines. TODO: Replace the CRLF with newlines in Dockerfile.

First Time Setup
Host System Configuration
C:\Windows\System32\drivers\etc\hosts: You must add an entry for the following hosts all pointing to the same Docker NAT IPv4 (use the address from Control Panel > Network and Internet > Network and Sharing Center > DockerNAT IPv4 address, or alternatively use the output from the ipconfig command):
x.x.x.x latest.xdstoolkit.test, a.xdstoolkit.test, b.xdstoolkit.test, c.xdstoolkit.test

Create a Toolkit Docker Network
This is required for communication between multiple Toolkit containers.
docker network create xdstoolkitNet

Personalizing Toolkit i.e., Configure Toolkit for your Environment
buildImage.bat: 
User must set the "containerHostIp" value to the host's IPv4 address in this script (use the address from Control Panel > Network and Internet > Network and Sharing Center > main Ethernet IPv4 address, or alternatively use the output from the ipconfig command).

toolkit.properties:
User must review the following properties: Toolkit_Port, Proxy_Port, and the Listener_Port_Range. If any changes are desired, they should be updated.
Toolkit_Host property: This is both the container name and FQDN. The value must be one of the certificate's subjectAtlNames which were added to the /etc/hosts file.
External_Cache is automatically configured to use a Docker Volume. 

Running Toolkit
Run "buildImage.bat" to build a Toolkit Docker image and to create a Docker container. If this is successful, instructions on how to start the container and how to access the Toolkit running on Tomcat will be provided when the script completes running. There are two optional parameters: Release number and the toolkit.property file name. Without any parameters, two default parameter values are implied by the script: the latest Toolkit release to be installed/upgraded and a reference the "toolkit.properties" file that exists in the same folder as the script.

Please note: For Toolkit Releases which do not ship with the GSS certificate, an additional step is required: you must manually copy the certificate to the External Cache environment keystore folder. (Footnote 1.)

Managing User Data
External Cache
Toolkit on Docker for Windows makes use of the built-in Docker Volume feature since there may be a firewall issue which prevents File Sharing for the purpose of mapping an External Cache folder on the C:\ drive. Special instructions (Footnote 2) are needed for Windows users to make a copy/snapshot of the External Cache so that it is viewable from the Windows Explorer and to troubleshoot using Toolkit user data components such as the Test Log Cache. (Footnote 2.)

If you wish to reset a given Toolkit container's External Cache, with the intention to remove all user data and to have a clean start, see Footnote 3.

Toolkit.properties
In the case of an upgrade, the toolkit.properties file must be manually backed up to prevent custom settings from being lost from an upgrade. (Footnote 4.)


Footnotes
Special Instructions using Docker commands
Note: The following commands assume you are working with the latest.xdstoolkit.test Toolkit container name. If you are using another container, you must use the proper container name in all of the commands in this section.
Please start the Toolkit container if is not running. For example, to start the latest Toolkit container, you may use this command:
docker start latest.xdstoolkit.test

1. Copying the certificate to External Cache
docker cp cert/from-eu-gss-all-purpose-3129.jks latest.xdstoolkit.test:/opt/ecdir/environment/default/keystore/keystore

2. Making a snapshot of the External Cache/Viewing the External Cache through File Explorer
docker cp latest.xdstoolkit.test:/opt/ecdir .

3. To Remove an existing Docker Volume
docker volume rm ec[latest|ReleaseNumber]

4. Backing up Toolkit.properties file
Note: In addition to the note on the container name being used in the command, If you are not using the "latest" Toolkit Release, replace the "xdstools[latest]" with a proper Toolkit Release number, for example "xdstools7.1.2", in the following command: 
docker cp latest.xdstoolkit.test:/usr/local/tomcat/webapps/xdstoolslatest/WEB-INF/classes/toolkit.properties .

