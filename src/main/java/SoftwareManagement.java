import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

class SoftwareManagement {
  private static final String scriptDir = "/mnt/config/zeroTouch/";

  private static void swDownload(Server smrs, String configScriptRemote, String configScriptLocal,
  String swNameRem, String swNameLocal) {
    String inet = smrs.getInet();
    //String protocol = smrs.getProtocol();
    try {
      System.out.print("Getting SW package and config file from server .");
      String cmd =
      scriptDir + "swDownload.sh " + swNameRem + " " + swNameLocal + " " + configScriptRemote + " "
      + configScriptLocal + " " + inet;
      Process pr = Runtime.getRuntime().exec(cmd);
      while (!pr.waitFor(5, TimeUnit.SECONDS)) {
        System.out.print(".");
      }
      System.out.println();

      List<String> errors = UtilitiesZero.readError(pr);

      if (errors.isEmpty()) {
        System.out.println("No errors with the SW download");

      } else {
        System.out.println("We encountered this errors: " + errors.toString());
        System.exit(1);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void moveFolder(String fromLocation, String toLocation) {
    try {
      String cmd = "mv " + fromLocation + " " + toLocation;
      Runtime.getRuntime().exec(cmd);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void createPackage(String swName) {

    try {
      System.out.println("Creating package");
      String cmd = scriptDir + "createPackage.sh " + swName;

      Process p = Runtime.getRuntime().exec(cmd);
      List<String> lines = UtilitiesZero.readOutput(p);
      String result = UtilitiesZero.checkOutput(lines);

      System.out.println("Result of swdlCreatePackage is " + result);
      if (!result.equals("OK")) {
        System.exit(1);
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  private static void createPackageReportProgress() {

    List<String> output;
    List<String> lines;
    String state = "";
    String result = "";
    String progressPercentage;
    String resultInfo = "";
    try {
      while (!state.equals("FINISHED")) {

        String cmd = scriptDir + "createPackageReportProgress.sh";
        Process p = Runtime.getRuntime().exec(cmd);
        lines = UtilitiesZero.readOutput(p);

        output = UtilitiesZero.checkOutputLong(lines);
        state = output.get(0);
        result = output.get(1);
        progressPercentage = output.get(2);
        resultInfo = output.get(3);

        if (state.equals("RUNNING")) {
          System.out.println("Create Package progress percentage is: " + progressPercentage);
          TimeUnit.SECONDS.sleep(3);

        }
      }
      if (result.equals("SUCCESS")) {
        System.out.println("Create Package SUCCESS");

      } else {
        System.out.println("Create Package FAILURE with info :" + resultInfo);
        System.exit(1);

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void preparePackage(String swName) {
    try {
      System.out.println("Preparing package");

      String cmd = scriptDir + "preparePackage.sh " + swName;

      Process p = Runtime.getRuntime().exec(cmd);

      List<String> lines = UtilitiesZero.readOutput(p);
      String result = UtilitiesZero.checkOutput(lines);

      System.out.println("Result of preparePackage is " + result);
      if (!result.equals("OK")) {
        System.exit(1);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void activatePackage(String swName) {
    try {
      System.out.println("Activating package");

      String cmd = scriptDir + "activatePackage.sh " + swName;

      Process p = Runtime.getRuntime().exec(cmd);

      List<String> lines = UtilitiesZero.readOutput(p);
      String result = UtilitiesZero.checkOutput(lines);

      System.out.println("Result of activatePackage is " + result);
      if (!result.equals("OK")) {
        System.exit(1);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void packageReportProgress(String swName, String action) {
    List<String> output;
    List<String> lines;
    String state = "";
    String result = "";
    String progressPercentage;
    String resultInfo = "";
    try {
      while (!state.equals("FINISHED")) {

        String cmd = scriptDir + "packageReportProgress.sh " + swName;
        Process p = Runtime.getRuntime().exec(cmd);
        lines = UtilitiesZero.readOutput(p);

        output = UtilitiesZero.checkOutputLong(lines);
        state = output.get(0);
        result = output.get(1);
        progressPercentage = output.get(2);
        resultInfo = output.get(3);

        if (state.equals("RUNNING")) {
          System.out.println(action + " package progress percentage is: " + progressPercentage);
          TimeUnit.SECONDS.sleep(3);

        }
      }
      if (result.equals("SUCCESS")) {
        System.out.println(action + " package SUCCESS");

      } else {
        System.out.println(action + " package  FAILURE with info :" + resultInfo);

        System.exit(1);

      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void checkNode() {
    int numTentativi = 5;
    for (int count = 0; count < numTentativi; count++) {
      try {

        String cmd = scriptDir + "checkNode.sh";

        Process pr = Runtime.getRuntime().exec(cmd);
        List<String> errors = UtilitiesZero.readError(pr);

        if (errors.isEmpty()) {
          System.out.println("E' possibile connettersi alla CLI");
          break;
        } else {
          System.out.println("NON E' possibile connettersi alla CLI");
          TimeUnit.SECONDS.sleep(30);
          if (count == numTentativi - 1) {
            System.exit(1);
          }

        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

    }
  }

  private static void confirmPackage(String swName) {
    try {
      System.out.println("Confirming package");

      String cmd = scriptDir + "confirmPackage.sh " + swName;

      Process p = Runtime.getRuntime().exec(cmd);

      List<String> lines = UtilitiesZero.readOutput(p);
      String result = UtilitiesZero.checkOutput(lines);

      System.out.println("Result of confirmPackage is " + result);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static String getStateSwName(String swName) {
    List<String> output;
    List<String> lines;
    String state = "";
    try {
      String cmd = scriptDir + "stateReportProgress.sh " + swName;
      Process p = Runtime.getRuntime().exec(cmd);
      lines = UtilitiesZero.readOutput(p);

      output = UtilitiesZero.checkOutputLong(lines);
      state = (output.size() >= 4) ? output.get(0) : "";

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return state;
  }

  private static void execConfig(String configScriptLocation) {
    try {
      System.out.println("Executing configuration script");

      String cmd = scriptDir + "executeScript.sh " + configScriptLocation;

      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();

      List<String> lines = UtilitiesZero.readOutput(p);
      System.out.println(lines);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void exec(Server smrs, String swNameZip, String configScript,
  String configScriptDestPath) {

    checkNode();
    String swNameTar = swNameZip.replaceAll(".zip", ".tar");
    String stateSwName = getStateSwName(swNameTar);
    if (stateSwName.contains("WAITING_FOR_COMMIT")) {
      confirmPackage(swNameTar);
      packageReportProgress(swNameTar, "Confirm");
      execConfig(configScript);
      try {
        UtilitiesZero.rmTempFiles();
      } catch (IOException e) {
        e.printStackTrace();
      }

    } else if (stateSwName.contains("COMMIT_COMPLETED")) {
      System.out.println("Software " + swNameTar + " already committed.");
      execConfig(configScript);
      try {
        UtilitiesZero.rmTempFiles();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    } else {
      //Download SW e Config?
      swDownload(smrs, configScript, configScriptDestPath + "/" + configScript, swNameZip,
      "/etc/confd/docRoot/webui/upload/" + swNameZip);

      createPackage(swNameZip);
      createPackageReportProgress();

      preparePackage(swNameTar);
      packageReportProgress(swNameTar, "Prepare");

      activatePackage(swNameTar);
      packageReportProgress(swNameTar, "Activate");
    }

  }

}
