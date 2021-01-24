import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class CertificatesManagement{
	
	//Path to files
	private static final String currentDir = System.getProperty("user.dir");
	private static final String scriptDir = "";
	//Funzione che restituisce l'output di un processo lanciato con JRE
	private static List<String> readOutput(Process process) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}	
	
	//Funzione che restituisce l'errore di un processo lanciato con JRE

	public static  List<String>   readError(Process process) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
	
	//Funzione per controllare l'output di un comando verso fw 
	private static String checkOutput(List<String> lines) {
		String result = "";
		for(String l : lines) {
			if(l.contains("OK")) {
				result = "OK";
			}else if(l.contains("Failure")) {
				result = "FAILURE";
			}else if(l.isEmpty()) {
				continue;
			}else if(l.contains("error")){
				result = "SYNTAX_ERROR";
			}
		}
		return result;
	}
	
	//Funzione per controllare l'avanzamento di un comando verso fw 

	private  static List<String> checkOutputLong(List<String> lines){
		
		String state = "";
		String result = "";
		String progressPercentage = "";
		String additionalInfo = "";

		List<String> output = new ArrayList<>();
		for(String s : lines) {
	
			if(s.contains("state")) {
				if(s.contains("FINISHED")) {
					state = "FINISHED";
				}else {
					state = "RUNNING";
				}
	
			}
			
			if(s.contains("result")) {
				if(s.contains("SUCCESS")) {
					result = "SUCCESS";
				}else {
					result = "FAILURE";
				}
	
			}
			
			if(s.contains("additionalInfo")) {
				additionalInfo = s;
			}	
			
			if(s.contains("progressPercentage")) {
				progressPercentage = s;
			}
		}
	
		output.add(state);
		output.add(result);
		output.add(progressPercentage);
		output.add(additionalInfo);
	
	
		return output;
	}
	
	//Funzione che inizia l'installazione di uno specifico certificato
	private static String startInstallCertificate(Server RA_CA, String certificate) {
		String result;
		try {	
			
			String protocol = RA_CA.getProtocol();
			String  inet = RA_CA.getInet();
			String tcpPort = RA_CA.getTcpPort();
			
			String location = protocol + "://" + inet + ":" + tcpPort + "/" + certificate;
			
			String cmd = currentDir + scriptDir + "startInstallCertificate.sh " + location;
			
			Process p = Runtime.getRuntime().exec(cmd);
			List<String> lines = readOutput(p);
			result = checkOutput(lines);
			System.out.println("Result of startInstallCertificate is " + result);
			if(!result.equals("OK")){

				System.exit(1);
			}
			
			return result;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return "FAILURE";
	

	}
	//Funzione per controllare l'avanzamento dell'installazione di un certificato
	private static List<String> checkInstallCertificates(){
		List<String> output = new ArrayList<>();
		try {
			
			String cmd = currentDir + scriptDir + "checkInstallCertificate.sh";
			Process p = Runtime.getRuntime().exec(cmd);
			List<String> lines = readOutput(p);
			output = checkOutputLong(lines);
			
			return output;

		}catch (Exception ex){
			ex.printStackTrace();
		}	
		return output;
	}
	//Procedura che esegue l'installazione di tutti i certificati utilizzando le due funzione precedenti
	private static void installAllCertificates(List<String> certificates, Server ra_ca_server) {
	   
	    String resultCertDownload;
	    List<String> resultCheck;
		String state = "";
		String result = "";
		String progressPercentage;
		String additionalInfo = "";

		for (String certificate : certificates) {
			try {
				resultCertDownload = startInstallCertificate(ra_ca_server, certificate);
				if (resultCertDownload.equals("OK")) {
					System.out.println("STARTED installation of certificate " + certificate);

					while (!state.equals("FINISHED")) {

						resultCheck = checkInstallCertificates();

						state = resultCheck.get(0);
						result = resultCheck.get(1);
						progressPercentage = resultCheck.get(2);
						additionalInfo = resultCheck.get(3);

						if (state.equals("RUNNING")) {
							System.out.println("Certificate  " + progressPercentage);
							TimeUnit.SECONDS.sleep(3);
						}
					}
					if (result.equals("SUCCESS")) {
						System.out.println("Certificate successfully installed");

					} else {
						System.out.println(
						"FAILED installation of certificate " + certificate + "with info :"
						+ additionalInfo);
						System.exit(1);

					}

				} else {
					System.out.println("FAILED installation of certificate " + certificate);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static void createTrustCategory(List<String> certificates, String idTrustCat){
		try {
			String cmd = currentDir + scriptDir + "createTrustCategory.sh " + idTrustCat +" "+ certificates.get(0) +" "+ certificates.get(1) +" "+  certificates.get(2) +" "+ certificates.get(3);
			
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of createTrustCategory is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private static void createEnrollmentServerGroup(){
		try {
			String cmd =currentDir + scriptDir + "createEnrollmentServerGroup.sh";
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of createEnrollmentServerGroup is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void addEnrollmentServerGroup(Server RA_CA, String prot){
		try {
			String protocol = RA_CA.getProtocol();
			String port = RA_CA.getTcpPort();
			String inet = RA_CA.getInet();
			String location = protocol + "://" + inet + ":" + port + "/pkira-cmp/NE_OAM_CA/synch";
			String cmd = currentDir + scriptDir + "addEnrollmentServerGroup.sh " + prot + " " + location;
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of addEnrollmentServerGroup is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void createEnrollmentAuthority(String name, String fingerprint){
	 	try {
			String cmd = currentDir + scriptDir + "createEnrollmentAuthority.sh " + name + " " + fingerprint ;
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of createEnrollmentAuthority is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void createNodeCredential(String nodeCredential, String expiryAlarmThreshold,
	String key, String subjname){
	 	try {
			String cmd = currentDir + scriptDir + "createNodeCredential.sh " + nodeCredential +" " +expiryAlarmThreshold+ " " + key +" " + subjname;
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of createNodeCredential is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void startEnrollment(String nodeCredential, String password){
	 	try {
			String cmd = currentDir + scriptDir + "startEnrollment.sh "+ nodeCredential +" " + password;
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of startEnrollment is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void checkResult(){
	 	try {
			String cmd = currentDir + scriptDir + "checkResult.sh ";
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of checkResult is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void enableNetConf(String nodeCredential, String idTrustCat){
	 	try {
			String cmd = currentDir + scriptDir + "enableNetConf.sh " + nodeCredential + " " + idTrustCat;
			Process p = Runtime.getRuntime().exec(cmd);
			
			List<String> lines = readOutput(p);
			String result = checkOutput(lines);

			System.out.println("Result of enableNetConf is " + result);
			if(!result.equals("OK")){
				System.exit(1);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void exec(Server ra_ca,List<String > certificates,String name, String fingerprint,String idTrustCat,String nodeCredential ,String expiryAlarmThreshold,String key,String prot,String subjname,String pass) {
		installAllCertificates(certificates,ra_ca);	
		createTrustCategory(certificates,idTrustCat);
		createEnrollmentServerGroup();
		addEnrollmentServerGroup(ra_ca,prot);
		createEnrollmentAuthority(name, fingerprint);
		createNodeCredential(nodeCredential ,expiryAlarmThreshold,key,subjname);
		startEnrollment(nodeCredential,pass);
		checkResult();
		enableNetConf(nodeCredential,idTrustCat);
	}
}
