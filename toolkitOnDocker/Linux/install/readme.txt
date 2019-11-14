Readme for Linux
The purpose of this Readme is to guide the user in setting up a new instance of Toolkit on Docker with the intention to retain the External Cache and the toolkit.properties file when Toolkit is upgraded to a newer release. Please note: this assumes that the newer release is compatible with the existing External Cache and relies on the user to identify changes in the toolkit.properties from the Release copy and then propagating those changes to the toolkit.properties that will be used to install/upgrade/run Toolkit.

File Descriptions i.e., The files available to the Docker context
cert/from-eu-gss-all-purpose-3129.jks - Certificate #3129 as issued by the European Gazelle Security Suite (GSS). This certificate has the following SubjectAltNames: host.xdstoolkit.test, sut.xdstoolkit.test, latest.xdstoolkit.test, a.xdstoolkit.test, b.xdstoolkit.test, c.xdstoolkit.test. The "host" and the "sut" are intended for the systems running on the Docker Host while all others are reserved for Toolkit on Docker.

buildImage.sh - Shell script that makes use of the Docker build command and the Docker create command.

Dockerfile - Used by Docker build.

readme.txt - This file.

toolkit.properties - Toolkit.properties file.

First time setup
Host System configuration
/etc/hosts: You must add an entry for the following hosts all pointing to the same Docker NAT IPv4 (use ifconfig -a and use the "inet addr" for the "docker0" interface):
x.x.x.x latest.xdstoolkit.test, a.xdstoolkit.test, b.xdstoolkit.test, c.xdstoolkit.test

Create a Toolkit Docker Network
This is required for communication between multiple Toolkit containers.
sudo docker network create xdstoolkitNet

Personalizing Toolkit
buildImage.sh: 
User must set the "toolkitHostIp" value to the host's IPv4 address in this script (use ifconfig -a and use the "inet addr" for the  main Ethernet interface).

toolkit.properties:
User must set the "External_Cache" value to a folder path on the host system. Please note: For Toolkit Releases which do not ship with the GSS certificate, an additional step is required: you must manually overwrite the keystore file in the External Cache environment keystore folder with the provided keystore "cert/from-eu-gss-all-purpose-3129.jks". An example of this action would look something like sudo cp cert/from-eu-gss-all-purpose-3129.jks /path/to/my/external/cache/environment/default/keystore/keystore. The keystore password is "changeit".
User must review the following properties: Toolkit_Port, Proxy_Port, and the Listener_Port_Range. If any changes are desired, they should be updated.
Toolkit_Host property: This is both the container name and FQDN. The value must be one of the certificate's subjectAtlNames which were added to the /etc/hosts file.

Run "sudo ./buildImage.sh" to build a Toolkit Docker image and to create a Docker container. If this is successful, instructions on how to start the container and how to access the Toolkit running on Tomcat will be provided when the script completes running. There are two optional parameters: Release number and the toolkit.property file name. Without any parameters, two default parameter values are implied by the script: the latest Toolkit release to be installed/upgraded and a reference the "toolkit.properties" file that exists in the same folder as the script.



