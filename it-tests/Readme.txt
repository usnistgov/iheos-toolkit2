Solving error message: "Class not found: "gov.nist.toolkit.itTests.xds.xxx"Empty test suite."
This error can happen when running some of the tests from this it-tests package. It comes from
Intellij not interpreting the mvn build correctly. To correct it, select the "src/test/groovy/gov" directory,
click right and mark the directory as Test Sources Root.