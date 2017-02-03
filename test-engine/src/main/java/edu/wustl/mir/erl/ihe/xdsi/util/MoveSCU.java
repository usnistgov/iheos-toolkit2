/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;


import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.IncompatibleConnectionException;
import org.dcm4che3.net.QueryOption;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.ExtendedNegotiation;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.SafeClose;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 */
@SuppressWarnings("javadoc")
public class MoveSCU {

    public static enum InformationModel {
        PatientRoot(UID.PatientRootQueryRetrieveInformationModelMOVE, "STUDY"),
        StudyRoot(UID.StudyRootQueryRetrieveInformationModelMOVE, "STUDY"),
        PatientStudyOnly(UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired, "STUDY"),
        CompositeInstanceRoot(UID.CompositeInstanceRootRetrieveMOVE, "IMAGE"),
        HangingProtocol(UID.HangingProtocolInformationModelMOVE, null),
        ColorPalette(UID.ColorPaletteInformationModelMOVE, null);

        final String cuid;
        final String level;

        InformationModel(String cuid, String level) {
            this.cuid = cuid;
            this.level = level;
       }
        public String getCuid() {
            return cuid;
        }

    }
    
    private static Logger log;

    private static ResourceBundle rb = new MoveSCUResources();
      //  ResourceBundle.getBundle("org.dcm4che3.tool.movescu.messages");

    private static final int[] DEF_IN_FILTER = {
        Tag.SOPInstanceUID,
        Tag.StudyInstanceUID,
        Tag.SeriesInstanceUID
    };

    private ApplicationEntity ae = new ApplicationEntity("MOVESCU");
    private final Connection conn = new Connection();
    private final Connection remote = new Connection();
    private final AAssociateRQ rq = new AAssociateRQ();
    private Device device;
    private int priority;
    private String destination;
    private InformationModel model;
    private Attributes keys = new Attributes();
    private int[] inFilter = DEF_IN_FILTER;
    private Association as;

    public MoveSCU() {
        this.device = new Device("movescu");
        this.device.addConnection(conn);
        this.device.addApplicationEntity(ae);
        ae.addConnection(conn);
    }

    public MoveSCU(ApplicationEntity ae) {
        this.ae = ae;
        this.device = ae.getDevice();
    }

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final void setInformationModel(InformationModel model, String[] tss,
            boolean relational) {
       this.model = model;
       rq.addPresentationContext(new PresentationContext(1, model.cuid, tss));
       if (relational)
           rq.addExtendedNegotiation(new ExtendedNegotiation(model.cuid, 
                   QueryOption.toExtendedNegotiationInformation(EnumSet.of(QueryOption.RELATIONAL))));
       if (model.level != null)
           addLevel(model.level);
    }

    public ApplicationEntity getApplicationEntity() {
        return ae;
    }

    public Connection getRemoteConnection() {
        return remote;
    }
    
    public AAssociateRQ getAAssociateRQ() {
        return rq;
    }
    
    public Association getAssociation() {
        return as;
    }

    public Device getDevice() {
        return device;
    }    
    
    public Attributes getKeys() {
        return keys;
    }
    public void addLevel(String s) {
        keys.setString(Tag.QueryRetrieveLevel, VR.CS, s);
    }

    public final void setDestination(String destination) {
        this.destination = destination;
    }

    public void addKey(int tag, String... ss) {
    	log = Utility.getLog();
    	log.debug("MoveSCU::addKey tag=" + Integer.toHexString(tag));
    	for (String s: ss) {
    		log.debug(" Value=" + s);
    	}
        VR vr = ElementDictionary.vrOf(tag, keys.getPrivateCreator(tag));
        keys.setString(tag, vr, ss);
    }

    public final void setInputFilter(int[] inFilter) {
        this.inFilter  = inFilter;
    }

    private static CommandLine parseComandLine(String[] args)
                throws ParseException {
            Options opts = new Options();
            addServiceClassOptions(opts);
            addKeyOptions(opts);
            addRetrieveLevelOption(opts);
            addDestinationOption(opts);
            CLIUtils.addConnectOption(opts);
            CLIUtils.addBindOption(opts, "MOVESCU");
            CLIUtils.addAEOptions(opts);
            CLIUtils.addRetrieveTimeoutOption(opts);
            CLIUtils.addPriorityOption(opts);
            CLIUtils.addCommonOptions(opts);
            return CLIUtils.parseComandLine(args, opts, rb, MoveSCU.class);
    }

    @SuppressWarnings("static-access")
    private static void addRetrieveLevelOption(Options opts) {
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("PATIENT|STUDY|SERIES|IMAGE|FRAME")
                .withDescription(rb.getString("level"))
                .create("L"));
   }

    @SuppressWarnings("static-access")
    private static void addDestinationOption(Options opts) {
        opts.addOption(OptionBuilder
                .withLongOpt("dest")
                .hasArg()
                .withArgName("aet")
                .withDescription(rb.getString("dest"))
                .create());
        
    }

    @SuppressWarnings("static-access")
    private static void addKeyOptions(Options opts) {
        opts.addOption(OptionBuilder
                .hasArgs()
                .withArgName("attr=value")
                .withValueSeparator('=')
                .withDescription(rb.getString("match"))
                .create("m"));
        opts.addOption(OptionBuilder
                .hasArgs()
                .withArgName("attr")
                .withDescription(rb.getString("in-attr"))
                .create("i"));
    }

    @SuppressWarnings("static-access")
    private static void addServiceClassOptions(Options opts) {
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("name")
                .withDescription(rb.getString("model"))
                .create("M"));
        CLIUtils.addTransferSyntaxOptions(opts);
        opts.addOption(null, "relational", false, rb.getString("relational"));
    }
    
    /**
     * Convenience method for study C-MOVE
    * @param sourceAE name of runDirectory .ini file for calling AE
    * @param peerAE name of runDirectory .ini file for peer (called) AE
    * @param destAE name of runDirectory .ini file for destination AE
    * @param uids StudyInstanceUID, SeriesInstanceUID, and SOPInstanceUID for
    * C-MOVE. Specify only those needed for level of move you are doing.
    * @throws Exception on error.
    */
   public static void moveStudy(String sourceAE, String peerAE, String destAE, 
      String studyUID, String seriesUID, String imageUID) throws Exception {
	   log = Utility.getLog();
	   log.debug("MoveSCU::moveStudy enter method peerAE=" + peerAE +
			   " destAE=" + destAE);
       List<String> arguments = new ArrayList<>();
       String level="STUDY";
       
       AEBean peer = AEBean.loadFromConfigurationFile(peerAE);
       arguments.add("--connect");
       arguments.add(peer.getAeTitle() + "@" + peer.getHost() + ":" + peer.getPort());
       
       //AEBean dest = AEBean.loadFromConfigurationFile(sourceAE);
       arguments.add("--dest");
       //arguments.add(dest.getAeTitle());
       arguments.add(destAE);
       
       arguments.add("-m");
       //arguments.add("StudyInstanceUID=\"" + studyUID + "\"");
       arguments.add("StudyInstanceUID=" + studyUID);
       log.debug(" Study Instance UID=" + studyUID);
       if (StringUtils.isNotBlank(seriesUID)) {
    	  log.debug("Series Instance UID=" + seriesUID);
          arguments.add("SeriesInstanceUID=" + seriesUID);
          level="SERIES";
       }
       if (StringUtils.isNotBlank(imageUID)) {
    	   log.debug("SOPInstanceUID=" + imageUID);
          arguments.add("SOPInstanceUID=" + imageUID);
          level="IMAGE";
       }
       arguments.add("-L" + level);
       
       MoveSCU.runMoveSCU(arguments.toArray(new String[0]));
    }

    @SuppressWarnings("unchecked")
    public static void runMoveSCU(String[] args) {
        try {
            CommandLine cl = parseComandLine(args);
            MoveSCU main = new MoveSCU();
            CLIUtils.configureConnect(main.remote, main.rq, cl);
            CLIUtils.configureBind(main.conn, main.ae, cl);
            CLIUtils.configure(main.conn, cl);
            main.remote.setTlsProtocols(main.conn.getTlsProtocols());
            main.remote.setTlsCipherSuites(main.conn.getTlsCipherSuites());
            configureServiceClass(main, cl);
            configureKeys(main, cl);
            main.setPriority(CLIUtils.priorityOf(cl));
            main.setDestination(destinationOf(cl));
            ExecutorService executorService =
                    Executors.newSingleThreadExecutor();
            ScheduledExecutorService scheduledExecutorService =
                    Executors.newSingleThreadScheduledExecutor();
            main.device.setExecutor(executorService);
            main.device.setScheduledExecutor(scheduledExecutorService);
            try {
                main.open();
                List<String> argList = cl.getArgList();
                if (argList.isEmpty())
                    main.retrieve();
                else
                    for (String arg : argList)
                        main.retrieve(new File(arg));
            } finally {
                main.close();
                executorService.shutdown();
                scheduledExecutorService.shutdown();
            }
       } catch (ParseException e) {
            System.err.println("movescu: " + e.getMessage());
            System.err.println(rb.getString("try"));
            System.exit(2);
        } catch (Exception e) {
            System.err.println("movescu: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void configureServiceClass(MoveSCU main, CommandLine cl) throws ParseException {
        main.setInformationModel(informationModelOf(cl),
                CLIUtils.transferSyntaxesOf(cl), cl.hasOption("relational"));
    }

    private static String destinationOf(CommandLine cl) throws ParseException {
        if (cl.hasOption("dest"))
            return cl.getOptionValue("dest");
        throw new ParseException(rb.getString("missing-dest"));
    }

    private static void configureKeys(MoveSCU main, CommandLine cl) {
    	log = Utility.getLog();
    	log.debug("MoveSCU::configureKeys enter method");
        if (cl.hasOption("m")) {
            String[] keys = cl.getOptionValues("m");
            for (int i = 1; i < keys.length; i++, i++) {
            	log.debug(" Key=" + CLIUtils.toTag(keys[i-1]) +
            			" Value=" + StringUtils.split(keys[i], '/'));
                main.addKey(CLIUtils.toTag(keys[i - 1]), StringUtils.split(keys[i], '/'));
            }
        }
        if (cl.hasOption("L"))
            main.addLevel(cl.getOptionValue("L"));
        if (cl.hasOption("i"))
            main.setInputFilter(CLIUtils.toTags(cl.getOptionValues("i")));
        
        log.debug("MoveSCU::configureKeys exit method");
    }

    private static InformationModel informationModelOf(CommandLine cl) throws ParseException {
        try {
            return cl.hasOption("M")
                    ? InformationModel.valueOf(cl.getOptionValue("M"))
                    : InformationModel.StudyRoot;
        } catch(IllegalArgumentException e) {
            throw new ParseException(MessageFormat.format(
                    rb.getString("invalid-model-name"),
                    cl.getOptionValue("M")));
        }
    }

    public void open() throws IOException, InterruptedException,
            IncompatibleConnectionException, GeneralSecurityException {
        as = ae.connect(remote, rq);
    }

    public void close() throws IOException, InterruptedException {
        if (as != null && as.isReadyForDataTransfer()) {
            as.waitForOutstandingRSP();
            as.release();
        }
    }

    @SuppressWarnings("resource")
   public void retrieve(File f) throws IOException, InterruptedException {
        Attributes attrs = new Attributes();
        DicomInputStream dis = null;
        try {
            attrs.addSelected(new DicomInputStream(f).readDataset(-1, -1), inFilter);
        } finally {
            SafeClose.close(dis);
        }
        attrs.addAll(keys);
        retrieve(attrs);
    }

    public void retrieve() throws IOException, InterruptedException {
        retrieve(keys);
    }

    @SuppressWarnings("hiding")
   private void retrieve(Attributes keys) throws IOException, InterruptedException {
         DimseRSPHandler rspHandler = new DimseRSPHandler(as.nextMessageID()) {

            @Override
            public void onDimseRSP(Association as, Attributes cmd,
                    Attributes data) {
                super.onDimseRSP(as, cmd, data);
            }
        };

        as.cmove(model.cuid, priority, keys, null, destination, rspHandler);
    }

    @SuppressWarnings("hiding")
   public void retrieve(Attributes keys, DimseRSPHandler handler) throws IOException, InterruptedException {
       as.cmove(model.cuid, priority, keys, null, destination, handler);
   }

    public void setLevel(InformationModel mdl) {
        this.model = mdl;
        if(mdl.level.equalsIgnoreCase("IMAGE")) {
            this.rq.addExtendedNegotiation(new ExtendedNegotiation(model.cuid, new byte[]{1}));
        }
    }
    
    private static class MoveSCUResources extends ListResourceBundle {

      /* (non-Javadoc)
       * @see java.util.ListResourceBundle#getContents()
       */
      @Override
      protected Object[][] getContents() {
         return new Object[][] {
            { "usage", "movescu [options] -c <aet>@<host>:<port> --dest <aet> [dcmfile_in...]" },
            { "try", "Try `movescu --help' for more information." },
            { "description", "\n"
              + "The movescu application implements a Service Class User (SCU) for the Query/Retrieve, the "
              + "Composite Instance Root Retrieve, the Composite Instance Retrieve Without Bulk Data, the "
              + "Hanging Protocol Query/Retrieve and the Color Palette Query/Retrieve Service Class. movescu "
              + "only supports retrieve functionality using the C-MOVE message. It sends matching keys to an "
              + "Service Class Provider (SCP) and waits for responses. Matching keys can be specified in "
              + "DICOM file(s) dcmfile_in or by options -m.\n -\n"
              + "Options:" },
            { "example", "-\n"
              + "Examples:\n"
              + "$ movescu -c DCMQRSCP@localhost:11112 -m StudyInstanceUID=1.2.3.4 --dest STORESCP\n"
              + "Retrieve from Query/Retrieve Service Class Provider DCMQRSCP listening on local port 11112 the "
              + "Study with Study Instance UID = 1.2.3.4 to the Storage Service Class Provider STORESCP" },
            { "missing-dest", "you must specify a move destination by option --dest" },
            { "model", "specifies Information Model. Supported names: PatientRoot, StudyRoot, PatientStudyOnly, "
               + "CompositeInstanceRoot, WithoutBulkData, HangingProtocol or ColorPalette. If no Information "
               + "Model is specified, StudyRoot will be used." },
            { "invalid-model-name", "{0} is not a supported Information Model name" },
            { "level", "specifies retrieve level. Use STUDY for PatientRoot, StudyRoot, PatientStudyOnly, and "
              + "IMAGE for CompositeInstanceRoot by default." },
            { "match", "specify matching key. attr can be specified by keyword or tag value "
              + "(in hex), e.g. StudyInstanceUID or 00200000D. Overrides matching keys "
              + "specified in DICOM file(s)." }, 
            { "in-attr", "specifies which attribute(s) of given DICOM file(s) dcmfile_in will be included in the "
              + "C-MOVE RQ. attr can be specified by its keyword or tag value (in hex), e.g.: StudyInstanceUID or "
              + "00100020. By default, Study Instance UID, Series Instance UID and SOP Instance UID from the "
              + "file(s) will be included." },
            { "dest", "specifies AE title of the Move Destination." },
            { "relational", "negotiate relational-retrieve support" }
         };
      }
    }
    
    /**
     * Test harness for static methods.
     * <ol>
     * <li>First argument indicates method to test</li>
     * <ol>
     * <li>MOVESTUDY = {@link MoveSCU#moveStudy}</li>
     * </ol>
     * <li>Remainder of arguments are passed to method in order.</li>
     * </ol>
     * 
     * @param args arguments
     */
   public static void main(String[] args) {
      String cmd;
      log = Utility.getLog();
      cmd = getArg(args, 0);
      try {
         if (cmd.equalsIgnoreCase("MOVESTUDY")) {
            MoveSCU.moveStudy(getArg(args, 1), getArg(args, 2), getArg(args, 3), getArg(args, 4), getArg(args, 5),
               getArg(args, 6));
         }

         log.info(cmd + " test completed");
      } catch (Exception e) {
         log.fatal(cmd + " test failed");
         e.printStackTrace();
      }
   }

    private static String getArg(String[] args, int arg) {
       if (args.length > arg) {
          String a = args[arg];
          if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_") || a.equalsIgnoreCase("null")) return null;
          return a.trim();
       }
       return null;
    }
}
