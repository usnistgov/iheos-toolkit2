Readme for Linux
The purpose of this Readme is to guide the user in setting up an instance of Toolkit on Docker with the intention to retain the External Cache and the toolkit.properties file when Toolkit is upgraded to a newer release. Please note: this assumes that the newer release is compatible with the existing External Cache and expects the user to identify changes in the toolkit.properties from the Release and then propagating those changes to the toolkit.properties that will be used to install/upgrade/run Toolkit.

Disk Contents i.e., The files available to the Docker context
cert/from-eu-gss-all-purpose-3129 - Certificate #3129 as issued by the Gazelle Security Suite (GSS).

buildImage.sh - Shell script that makes use of the Docker build command and the Docker create command.

Dockerfile - Used by Docker build.

readme.txt - This file.

toolkit.properties - Toolkit.properties file.

First time setup
Host System configuration
/etc/hosts: You must add an entry for the following hosts all pointing to the same Docker NAT IPV4 (use ifconfig -a and use the "inet addr" for the "docker0" interface):
x.x.x.x latest.xdstoolkit.test, a.xdstoolkit.test, b.xdstoolkit.test, c.xdstoolkit.test

Create a Toolkit Docker Network
This is required for communication between multiple Toolkit containers.
sudo docker network create xdstoolkitNet

Personalizing Toolkit
buildImage.sh: 
User must set the "toolkitHostIp" value to the host's IPV4 address in this script (use ifconfig -a and use the "inet addr" for the  main Ethernet interface).

toolkit.properties:
User must set the "External_Cache" value to a folder path on the host system.
In addition, User must review the following properties: Toolkit_Port, Proxy_Port, and the Listener_Port_Range. If any changes are desired, they should be updated.
Toolkit_Host property: This is both the container name and FQDN. The value must be one of the certificate's subjectAtlNames and should be present in the /etc/hosts file.

Run "sudo ./buildImage.sh" to build a Toolkit Docker image and to create a Docker container. If this is successful, instructions on how to start the container and how to access the Toolkit running on Tomcat will be provided when the script completes running. Usage: Without any parameters, two defaults are implied by the script: the latest Toolkit release to be installed/upgraded and to reference the "toolkit.properties" file that exists in the same folder as the script.



