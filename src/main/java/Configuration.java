import java.util.List;
import java.util.ArrayList;

public class Configuration {

  private String softwarePackage = "";

  private String configScript = "";

  private String configScriptDestPath = "";

  private List<String> trustedCertificates;

  Configuration() {
    super();
  }

  Configuration(String softwarePackage, String configScript, String configFilePathOnNode,
  List<String> trustedCertificatesUri) {
    this.softwarePackage = softwarePackage;
    this.configScript = configScript;
    this.configScriptDestPath = configFilePathOnNode;
    this.trustedCertificates = new ArrayList<>(trustedCertificatesUri);
  }

  public String getConfigScript() {
    return configScript;
  }

  public String getConfigScriptDestPath() {
    return configScriptDestPath;
  }

  public String getSoftwarePackage() {
    return softwarePackage;
  }

  public List<String> getTrustedCertificates() {
    return trustedCertificates;
  }

  @Override public String toString() {
    return "{ softwarePackage: " + this.softwarePackage + "{ configScript: " + this.configScript + ", configScriptDestPath: "
    + this.configScriptDestPath + ", trustedCertificates: " + this.trustedCertificates + " }";
  }
}
