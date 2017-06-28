import gov.nist.toolkit.configDatatypes.SimulatorProperties
import SimSupportBase
import gov.nist.toolkit.itSupport.xc.GatewayBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
/**
 * Initialize toolkit to support XDS-XCA-I_homeCommunityID test
 */

Tk.init()

def remoteToolkitPort = '8080'

SimulatorBuilder simBuilder  = SimSupportBase.getSimulatorApi('localhost', remoteToolkitPort, Tk.toolkitName)
String pid = 'P20170106143728.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO'
String testSession = 'cat';
ToolkitApi api = UnitTestEnvironmentManager.localToolkitApi()

// delete old simulators
simBuilder.delete('rg', testSession)
simBuilder.delete('ig', testSession)

// build and link simulators

def iConfig, rConfigs

// builds IG and RG and loads regrep behing RG with two documents
(iConfig, rConfigs) = GatewayBuilder.build(api, simBuilder, 1, 'cat', 'default', pid)
SimConfig igConfig = iConfig
SimConfig[] rgConfigs = rConfigs
SimConfig rgConfig = rgConfigs[0]
def igId = igConfig.id
def rgId = rgConfig.id
igConfig.setProperty(SimulatorProperties.locked, true)
simBuilder.update(igConfig)

//lock rg
rgConfig.setProperty(SimulatorProperties.locked, true)
simBuilder.update(rgConfig)
