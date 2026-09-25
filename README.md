# iheos-toolkit2
NIST XDS Toolkit

[Documentation](https://github.com/usnistgov/iheos-toolkit2/wiki/Home)

[Building and Releasing XDS Toolkit](https://github.com/usnistgov/iheos-toolkit2/wiki/Building-and-Releasing)

## Recent Updates

### Security Vulnerability Remediation (April 2026)

This update includes comprehensive security vulnerability remediation across the toolkit, addressing multiple critical and high-severity CVEs.

#### CVE Remediation
**Critical CVEs Fixed:**
- **CVE-2015-7501** - Fixed by updating Apache Commons version
- **CVE-2020-13936** - Fixed by updating Apache Velocity
- **CVE-2019-10202** - Fixed by replacing old Jackson 1.9.12

**High CVEs Fixed:**
- **CVE-2023-24998** - Fixed by updating commons-fileupload from 1.4 to 1.5
- **CVE-2025-48976** - Fixed by updating commons-fileupload from 1.4 to 1.5
- **CVE-2024-47554** - Fixed by updating commons-io from 2.4 to 2.14.0
- **CVE-2021-40690** - Fixed by updating xmlsec from 1.3.0 to 1.5.6
- **CVE-2022-42003** - Fixed by updating jackson-databind to 2.12.7.1
- **CVE-2022-42004** - Fixed by updating jackson-databind to 2.12.7.1
- **CVE-2025-52999** - Fixed by updating jackson-core to 2.15.0

#### Critical Vulnerabilities Fixed (Non-CVE)
- **Apache Axis2 XXE injection issue** - Updated Axis2 libraries
- **Apache Commons uncontrolled recursion issue** - Updated Commons libraries
- **Shell Command Injection** - Updated plexus-utils from 1.5.7 to 3.0.16
- **Race Condition** - Updated jersey-client from 2.33 to 2.46
- **Spring Authorization Bypass** - Updated spring-core from 4.3.6 to 6.2.11
- **Jackson Resource Allocation** - Updated jackson-core from 2.15.0 to 2.21.1
- **Woodstox XXE Injection** - Updated woodstox-core from 5.0.3 to 5.3.0
- **BouncyCastle Crypto Issues** - Updated bcprov-jdk15on from 1.60 to 1.69

#### OpenSAML Migration
- **Upgraded OpenSAML from 2.5.3 to 5.1.4** - Major security and functionality upgrade

### OpenSAML 5.1.4 Migration (April 2026)

This update includes a comprehensive migration to OpenSAML 5.1.4 with security improvements and build system enhancements.

#### Additional Security Improvements
- **Resolved CVE-2024-14378** - Added Jackson exclusions to prevent vulnerability
- **Enhanced SAML security** - Improved bootstrap and configuration handling

#### Build System Updates
- **Maven 3.9.14** - Updated from older Maven version for better compatibility
- **Java 17 compatibility** - All modules now compile successfully with Java 17
- **Fixed GWT compilation** - Restored xdstools2 module compilation with original configuration
- **Enhanced module packaging** - Fixed plugins module packaging with proper JAR creation

#### Technical Changes
- **OpenSAML Service Configuration** - Added proper META-INF service initializer
- **SAML Utility Classes** - Added Organization utility for SAML handling
- **Test Updates** - Updated SAML tests for OpenSAML 5.1.4 compatibility
- **GWT Plugin Configuration** - Restored working compileSourcesArtifact format

#### Build Requirements
- **Java 17** - Required for all modules
- **Maven 3.9.14** - Recommended for optimal compatibility
- **All 57 modules** - Successfully build and install

#### Known Issues
- **GWT Deprecation Warnings** - Using deprecated compileSourcesArtifact format for compatibility
- **External Cache Warnings** - Non-critical warnings during test execution

#### Migration Notes
- All existing functionality preserved
- No breaking changes to public APIs
- Backward compatibility maintained
- Full test suite passes

## Build Instructions

### Prerequisites
- Java 17 JDK
- Maven 3.9.14 or later

### Building the Project
```bash
# Clean and build all modules
mvn clean install

# Build specific module
mvn clean install -pl <module-name>

# Build with specific Java version
export JAVA_HOME=/path/to/java17
mvn clean install
```

### Troubleshooting
- If GWT compilation fails, ensure all dependencies are installed
- For Maven version issues, verify Maven 3.9.14 is being used
- Java 17 is required for OpenSAML 5.1.4 compatibility

