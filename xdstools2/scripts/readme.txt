The script grab-release.sh will pull a particular release, identified by 
a build number, from SVN on SourceForge.  It includes several 
packages that would be downloaded into a directory. One could then
cd xdstools2 and run ant war to create an loadable war file.  But,
it would not work.  XDS Toolkit GUI is built on the Google Web
Toolkit which allows me to write my GUI code in Java and a tool
compiles that Jave into JavaScript for auto-loading into the
browser.  But, Google hasn't released an ant task for doing that
compile.  The only way to do it (that I know of) is to use the 
Eclipse plugin.  This Eclipse plugin must be used to generate
the JavaScript before the war that gets generated has any
real value.