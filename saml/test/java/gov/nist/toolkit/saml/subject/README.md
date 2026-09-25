# KeyInfo Extraction Testing with Real SAML Assertions

This directory contains comprehensive tests for KeyInfo extraction from SAML assertions, specifically designed for testing the `HolderOfKeySubjectConfirmationValidator` functionality in the iheos-toolkit2 project.

## Test Files

### 1. KeyInfoExtractionTest.java
**Purpose**: Unit tests for KeyInfo extraction with various SAML assertion structures
**Coverage**:
- RSA KeyValue extraction
- X509Certificate extraction  
- Real SAML assertion XML parsing
- Failure scenarios (missing KeyInfo, wrong keys, no presenter key)

### 2. RealKeyInfoExtractionTest.java  
**Purpose**: Practical tests using real-world SAML assertion examples
**Coverage**:
- Gazelle STS-style assertions
- Multiple KeyInfo elements
- Complete validation workflow
- Edge cases and error handling

### 3. KeyInfoExtractionIntegrationTest.java
**Purpose**: End-to-end integration tests demonstrating practical usage
**Coverage**:
- Loading real SAML assertion files
- Complete validation workflow
- Gazelle STS integration scenarios
- Performance and reliability testing

## Key Features Tested

### KeyInfo Extraction Methods
1. **RSA KeyValue Extraction**
   - Extracts RSA modulus and exponent from SubjectConfirmationData
   - Validates against provided RSA public keys
   - Handles various RSA key formats

2. **X509Certificate Extraction**
   - Extracts X.509 certificates from KeyInfo
   - Validates certificate chains
   - Handles certificate encoding variations

3. **Multiple KeyInfo Handling**
   - Processes first KeyInfo when multiple present
   - Handles mixed KeyInfo types (RSA + X509)
   - Graceful fallback mechanisms

### Validation Scenarios
1. **Successful Validation**
   - Matching RSA keys
   - Matching X509 certificates
   - Proper KeyInfo extraction

2. **Failure Scenarios**
   - Missing KeyInfo in SubjectConfirmationData
   - Key/certificate mismatch
   - No presenter key provided
   - Invalid assertion structure

## Real SAML Assertion Examples

### Gazelle STS Assertions
The tests include realistic Gazelle STS SAML assertions with:
```xml
<saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:holder-of-key">
  <saml2:SubjectConfirmationData NotOnOrAfter="2023-03-27T10:35:00.000Z">
    <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
      <ds:KeyValue>
        <ds:RSAKeyValue>
          <ds:Modulus>...</ds:Modulus>
          <ds:Exponent>AQAB</ds:Exponent>
        </ds:RSAKeyValue>
      </ds:KeyValue>
    </ds:KeyInfo>
  </saml2:SubjectConfirmationData>
</saml2:SubjectConfirmation>
```

### X509Certificate Assertions
Tests also cover assertions with X509 certificates:
```xml
<ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
  <ds:X509Data>
    <ds:X509Certificate>BASE64_CERTIFICATE_HERE</ds:X509Certificate>
  </ds:X509Data>
</ds:KeyInfo>
```

## Running the Tests

### Prerequisites
1. **OpenSAML Bootstrap**: Tests automatically bootstrap OpenSAML 4.0.1
2. **Java 8+**: Required for the test framework
3. **JUnit 5**: Test execution framework

### Execution Commands
```bash
# Run all KeyInfo extraction tests
mvn test -Dtest=KeyInfoExtractionTest

# Run real-world assertion tests  
mvn test -Dtest=RealKeyInfoExtractionTest

# Run integration tests
mvn test -Dtest=KeyInfoExtractionIntegrationTest

# Run all KeyInfo tests
mvn test -Dtest="*KeyInfo*Test"
```

### Test Output
The tests provide detailed console output showing:
- SAML assertion parsing results
- KeyInfo extraction details
- Validation outcomes
- Error messages for failure cases

## Integration with iheos-toolkit2

### Connection to StsSamlValidator
These tests complement the existing `StsSamlValidator` by testing the KeyInfo extraction component that's essential for holder-of-key subject confirmation validation.

### Connection to Gazelle STS Integration
The tests use the same SAML assertion structures that are returned by the Gazelle STS service, ensuring compatibility with the existing integration test in `StsSamlAssertionSpec.groovy`.

### Usage in Production
The `HolderOfKeySubjectConfirmationValidator` is designed to be used in:
- SAML assertion validation workflows
- XDS/XCA transaction security
- IHE interoperability testing

## Test Data Sources

### Sample Assertions
The tests include sample SAML assertions that represent:
- **Gazelle STS responses**: Realistic assertions from the IHE Gazelle STS
- **XUA assertions**: Cross-Community User Authentication assertions
- **Test assertions**: Various test scenarios and edge cases

### Real Assertion Files
The integration tests attempt to load real SAML assertion files from:
- `it-tests/src/test/resources/war/toolkitx/testkit/examples/GazelleSts/`
- `saml/test/resources/`
- `src/test/resources/`

## Troubleshooting

### Common Issues
1. **OpenSAML Initialization**: Ensure `OpenSamlBootStrap.bootstrap()` is called first
2. **XML Parsing**: Verify SAML assertions are well-formed XML
3. **Namespace Handling**: Ensure proper SAML 2.0 namespaces are used
4. **Key Generation**: Test keys must be compatible with assertion KeyInfo

### Debug Information
Enable debug output by setting:
```java
System.setProperty("org.opensaml", "DEBUG");
```

### Test Failures
If tests fail, check:
1. SAML assertion XML structure
2. KeyInfo element placement
3. Namespace declarations
4. Key/certificate formats

## Extending the Tests

### Adding New Assertion Types
To test additional SAML assertion formats:
1. Add new assertion XML samples
2. Create corresponding test methods
3. Update helper methods as needed

### Testing Different Key Types
To support additional key algorithms:
1. Extend `generateKeyPair()` methods
2. Add KeyInfo extraction logic
3. Update validation matching logic

### Integration with External STS
To test with other STS implementations:
1. Add STS-specific assertion samples
2. Customize KeyInfo extraction if needed
3. Update validation parameters

## References

### IHE Specifications
- [IHE XUA Profile](https://www.ihe.net/uploadedFiles/Documents/ITI/ITI-71.pdf)
- [IHE BPPC Profile](https://www.ihe.net/uploadedFiles/Documents/ITI/ITI-55.pdf)

### SAML Standards
- [SAML 2.0 Core](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-core-2.0-os.pdf)
- [SAML 2.0 Holder-of-Key](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-holder-of-key-2.0-os.pdf)

### Gazelle STS
- [Gazelle STS Documentation](https://gazelle.ihe.net/content/sts)
- [Toolkit Integration Guide](https://github.com/usnistgov/iheos-toolkit2/wiki/SAML-Validation-against-Gazele)

## Future Enhancements

### Planned Improvements
1. **Performance Testing**: Add timing benchmarks for KeyInfo extraction
2. **Memory Testing**: Verify memory usage with large assertions
3. **Concurrent Testing**: Test thread safety of validation components
4. **Algorithm Support**: Extend support for additional key algorithms (ECDSA, EdDSA)

### Integration Opportunities
1. **Automated Testing**: Integrate with CI/CD pipelines
2. **Load Testing**: Test with high volumes of SAML assertions
3. **Fuzz Testing**: Test with malformed SAML assertions
4. **Compatibility Testing**: Test with various STS implementations
