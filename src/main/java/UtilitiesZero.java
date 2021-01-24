import java.io.*;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

class UtilitiesZero {

    private static final String scriptDir = "/mnt/config/zeroTouch/";
    public static final File stateFile = new File("/mnt/config/zeroTouch/state.txt");
    private static final String curDir = System.getProperty("user.dir");

    //Funzione per ottenere l'output di un processo
    public static List<String> readOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<String> lines = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
    //Funzione per ottenere l'errore di un processo

    public static  List<String>   readError(Process process) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    public static void checkIfAlreadyProvisioned(String interfaceName) throws IOException{
        NetworkInterface interf = NetworkInterface.getByName(interfaceName);
        //Se non sono associate interfacce lancia NullPoint. Exc.
        try {
            interf.getInterfaceAddresses();
            System.out.println("Already provisioned");
            System.exit(1);
        }catch(NullPointerException npe){
            System.out.println("Not provisioned");
            String fromDir = curDir +"/src/sh_scripts/";
            makeDirectory(fromDir,scriptDir);
        }
    }

    private static void makeDirectory(String sourceDir, String destDir) throws IOException {
        //Rimuovo Dir se esiste,la ricreo e salvo file temporanei(shell scripts).
        String[] cmd = {"/bin/sh","-c","rm -rf "+scriptDir+ ";mkdir -p "+scriptDir+";cp -R " + sourceDir + " " + destDir};
        Process pr = Runtime.getRuntime().exec(cmd);
    }
    //Rimuovo file temporanei
    public static String checkOutput(List<String> lines) {
        String result = "";
        for(String l : lines) {
            if(l.contains("OK")) {
                result = "OK";
            }else if(l.contains("Failure")) {
                result = "FAILURE";
            }else if(l.contains("Syntax")){
                result = "SYNTAX_ERROR";
            }
        }
        return result;
    }

    public  static List<String> checkOutputLong(List<String> lines){

        String state = "";
        String result = "";
        String progressPercentage = "";
        String actionName = "";

        List<String> output = new ArrayList<>();
        for(String s : lines) {

            if(s.contains("state")) {
                if(s.contains("FINISHED")) {
                    state = "FINISHED";
                }else if (s.contains("RUNNING")){
                    state = "RUNNING";
                }
                else if (s.contains("WAITING_FOR_COMMIT")){
                    state = "WAITING_FOR_COMMIT";
                }
                else if (s.contains("COMMIT_COMPLETED")){
                    state = "COMMIT_COMPLETED";
                }
                else
                {
                    state = "";
                }
            }

            if(s.contains("result")) {
                if(s.contains("SUCCESS")) {
                    result = "SUCCESS";
                }else {
                    result = "FAILURE";
                }

            }

            if(s.contains("actionName")) {
                actionName = s;
            }

            if(s.contains("progressPercentage")) {
                progressPercentage = s;
            }
        }

        output.add(state);
        output.add(result);
        output.add(progressPercentage);
        output.add(actionName);


        return output;
    }
    public static void rmTempFiles() throws IOException {
        String cmd = "rm -rf " + scriptDir;
        Process pr = Runtime.getRuntime().exec(cmd);
    }
}
