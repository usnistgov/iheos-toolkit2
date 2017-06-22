package gov.nist.toolkit.xdstools2.scripts

/**
 * Created by bmajur on 11/12/14.
 */

import gov.nist.toolkit.simcommon.server.SiteServiceManager
import gov.nist.toolkit.results.foofoo.Result
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager

/**
 * Created by bmajur on 11/12/14.
 */

def warHome = new File('/Users/bmajur/workspace/toolkit/xdstools2/war')
SiteServiceManager siteServiceManager = SiteServiceManager.getSiteServiceManager()
Session session = new Session(warHome, siteServiceManager)
session.setEnvironment('NA2015')
def mgr = new XdsTestServiceManager(session)
def testName = '11990'
def sections = ['submit']
def params = [ : ]
def params2 = [ : ]
String[] areas = ['tests', 'testdata']
def stopOnFailure = true
def siteName = 'pub'

System.setProperty('site', siteName)
Result result
result = mgr.xdstest(testName, sections, params, params2, areas, stopOnFailure)
