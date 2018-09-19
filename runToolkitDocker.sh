# See SunilDocker.properties for original ports.

# Port order in the -p docker run command is:
# 1. toolkit port. Note: the Toolkit_Port needs to match Tomcat port.
# 2. pid port range (avoid conflict with IJ debugger/regular Toolkit session)
# 3. proxy port
# 4. tls port
#

# Remap EC dir 
# Note: the :rw is required, otherwise the EC dir cannot be read/written.
# -v /home/skb1/projects/ec_dir_docker:/home/skb1/projects/ec_dir:rw \

# Note: 
# It is a good idea to remove OLD simdb folder, in case fhir server does not start up.

sudo docker run -it --rm \
-p 8080:8080 \
-p 6000-6015:6000-6015 \
-p 8297:8297 \
-p 8443:8443 \
-v /home/skb1/docker_tomc_webappsdir:/usr/local/tomcat/webapps:rw \
-v /home/skb1/projects/ec_dir_docker:/home/skb1/projects/ec_dir_docker:rw \
tomcat:7-jre8




