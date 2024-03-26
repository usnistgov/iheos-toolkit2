# iheos-toolkit2
XDS Toolkit

[Documentation](https://github.com/usnistgov/iheos-toolkit2/wiki/Home)

## Compiling XDS Toolkit
When building XDS Toolkit 7.11.0 with JDK 17 or above, a shell environment variable needs to be set on some operating systems such as Ubuntu.

This is required because Maven will not function without this. 
Use the following shell variable for Maven 3.6.0 and JDK 17+ on Ubuntu.

`export JDK_JAVA_OPTIONS="--add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED"`

On Windows, the JDK_JAVA_OPTIONS should not be necessary.

Previous XDS Toolkit Releases 7.7.0 to 7.10.0 required a Java JDK 8 compiler.

### Build command
mvn clean install -P YourProfileName,Integration-Tests,WebUI-Tests
