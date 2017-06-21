/**
 * setec - set the location of the external cache
 * This is used by scripts only.
 * Sets the path into the file ~/.toolkitec
 * Usage:
 *    setec ec-location
 */

def location=args[0]

File home = new File(System.getProperty('user.home'))
new File(home, '.toolkitec').write(location)
