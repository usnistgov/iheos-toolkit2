# iheos-toolkit2
NIST XDS Toolkit

[Documentation](https://github.com/usnistgov/iheos-toolkit2/wiki/Home)

[Building and Releasing XDS Toolkit](https://github.com/usnistgov/iheos-toolkit2/wiki/Building-and-Releasing)

## git repository clone issues

* Filename too long
  * During the git clone process (on Windows file system), you may encounter errors such as:
    * error: unable to create file it-tests/src/test/resources/external_cache/ImageCache/sim/xca-dataset-f/1.3.6.1.4.1.21367.201599.1.201606061008030/1.3.6.1.4.1.21367.201599.2.201606061008031.21/1.3.6.1.4.1.21367.201599.3.201606061008031.37/1.2.840.10008.1.2.4.90: Filename too long
  * The workaround is to use "-c core.longPath=true" option when performing the "git clone" command. 
