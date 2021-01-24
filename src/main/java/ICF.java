//import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class ICF {

  private Map<String, Server> serverTable;

  private Configuration configuration;

  public ICF() {
    super();
  }

  public ICF(Map<String, Server> serverTable, Configuration configuration) {
    this.serverTable = new HashMap<>(serverTable);
    this.configuration = configuration;
  }

  public Map<String, Server> getServerTable() {
    return serverTable;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  @Override public String toString() {
    return "{ serverTable: " + serverTable.toString() + " }\n" + "{ configuration: " + configuration
    .toString() + " }\n";
  }

  /**
   * readICFFromJsonFile: Read ICF class from a JSON file
   *
   * @param file: the Json file to read
   * @return Optional<ICF>: An Optional containing the ICF class
   */
  static ICF readICFFromJsonFile(File file) {

    ICF icf = null;
    ObjectMapper mapper = new ObjectMapper();

    try {
      icf = mapper.readValue(file, new TypeReference<ICF>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    return icf;
  }

  /**
   * writeICFToJsonFile: Write an ICF class to a JSON file
   *
   * @param icf:  the ICF class
   * @param file: the JSON file
   */
  static void writeICFToJsonFile(ICF icf, File file) {

    ObjectMapper mapper = new ObjectMapper();

    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(file, icf);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main(String[] args) {

    File jsonFileWrite = new File("ICF.json");
    ICF.writeICFToJsonFile(CreateICFExample(), jsonFileWrite);

    File jsonFileRead = new File("ICF.json");
    ICF.readICFFromJsonFile(jsonFileRead);

  }

  private static ICF CreateICFExample() {
    Map<String, Server> serverTab = new HashMap<>();

    serverTab.put("RA_CA", new Server("10.0.0.2", "", "8093", "http", "RACAUser", "RACAPassword"));
    serverTab.put("SMRS",
    new Server("10.42.169.8", "", "", "tftp", "", ""));
    serverTab.put("MS",
    new Server("10.0.0.3", "fe80::4c7f:1ff:fe74:ba8b", "162", "snmp", "MSUser", "MSPassword"));

    String swFileOnServer = "CXP9035658_10_R1AD21Q10109.zip";
    String configurationFileNameOnServer = "ConfigNodeA.txt";
    String configurationFilePathOnNode = "/mnt/config/config-scripts";
    List<String> trustedCertificates = new ArrayList<>();
    trustedCertificates
    .add("pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/5094675d722fe76c/active/ENM_PKI_Root_CA");
    trustedCertificates
    .add("pki-ra-tdps/ca_entity/ENM_Infrastructure_CA/3ad39bed30ea7ba8/active/ENM_PKI_Root_CA");
    trustedCertificates
    .add("pki-ra-tdps/ca_entity/ENM_OAM_CA/323a959159562dfc/active/ENM_Infrastructure_CA");
    trustedCertificates
    .add("pki-ra-tdps/ca_entity/NE_OAM_CA/2f73eaf52567b638/active/ENM_PKI_Root_CA");

    Configuration configuration =
    new Configuration(swFileOnServer, configurationFileNameOnServer, configurationFilePathOnNode,
    trustedCertificates);

    return new ICF(serverTab, configuration);
  }

}
