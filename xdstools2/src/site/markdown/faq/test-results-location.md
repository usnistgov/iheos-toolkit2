# Where are test results stored

There are two approaches and the choice depends on how you run them. If you run tests from the Conformance Test
Tool then you choose a test session to hold your results. The results are stored in
ExternalCache/TestTestLogCache/${testSession}/${testName}. These results stay around as long as you want them to.
Toolkit does not delete them. The easiest way to delete them is by deleting the test session which can be done on
the top banner of most tools.

The second approach is a more automatic approach where the location is created automatically and is later deleted.
The deletion happens when your browser session expires. The logs are stored within the expanded WAR file inside Tomcat in
webapps/xdstools2/SessionCache/${sessionID}. The servlet that runs toolkit automatically deletes this session specific directory
when your browser session expires.