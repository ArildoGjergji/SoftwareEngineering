import java.io.File;
import java.io.IOException;
import java.util.Map;

class TestCode{
	
    private static final String scriptDir = "/mnt/config/zeroTouch/";

    public static void main(String[] args) throws IOException{

            if (dhcp.dhcpCheck(dhcp.getInterfaceName()).isEmpty())
            {
                System.out.println("Not provisioned node. Starting zero touch configuration.");
                dhcp.exec();
            }
            else
            {
                System.out.println("Node already provisioned. Exiting.");
 //               System.exit(1);
            }

        File jsonFileRead = new File(scriptDir + "ICF.json");
        if(!jsonFileRead.exists()) {
            System.out.println("File doesn't exist: EXITING");
            System.exit(1);
        }

        ICF icf = ICF.readICFFromJsonFile(jsonFileRead);
        Configuration conf = icf.getConfiguration();

        String configScript = conf.getConfigScript();
        String configScriptDestPath = conf.getConfigScriptDestPath();
        String swName= conf.getSoftwarePackage();

        Map<String, Server> serverTab = icf.getServerTable();

        Server smrs_server = serverTab.get("SMRS");

        try {
          SoftwareManagement.exec(smrs_server,swName,configScript,configScriptDestPath);
        } catch (Exception e) {
          e.printStackTrace();
        }

    }

}
