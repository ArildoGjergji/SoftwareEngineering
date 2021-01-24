import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class dhcp {

	private static final String scriptDir = "/mnt/config/zeroTouch/";
	private static final String interface_mgmt = "fm1-mac3";
	private static final String curDir = System.getProperty("user.dir");

	//Metodo per ottenere interfaccia
	public static String getInterfaceName(){
		return interface_mgmt;
	}

	//DHCP process start
	public static void dhcpRequest(String interfaccia) throws IOException {
		try {
			String cmd = "udhcpc -s " + scriptDir + "dhcphook.sh -t 5 -T 6 -A 10 -b -S -V xps18 -q -i " + interfaccia;
			Process pr = Runtime.getRuntime().exec(cmd);
			pr.waitFor(10, TimeUnit.SECONDS);
			pr.destroyForcibly();

		}catch (Exception ex){
			ex.printStackTrace();
		}

		String interface_ip = dhcpCheck(interfaccia);

		if(interface_ip.isEmpty()) {
			System.out.println("No IP assigned to Device, DHCP Failed");
			System.exit(1);
		}else {
			System.out.println("DHCP Succeded, assigned IP address: " + interface_ip);
		}
	}

	//Controllo se l'IP è assegnato all'interfaccia
	static String dhcpCheck(String ifname) throws IOException{

		String ip_addr = "";
		NetworkInterface ipIf = NetworkInterface.getByName(ifname);

		if (ipIf == null)
		{
			return ip_addr;
		}

		//No loopback, return null
		if (ipIf.isLoopback())
			return ip_addr;

		//No down Interface, return null
		if (!ipIf.isUp())
			return ip_addr;

		// iterate over the addresses associated with the interface
		Enumeration<InetAddress> addresses = ipIf.getInetAddresses();
		for (InetAddress address : Collections.list(addresses)) {
			//No ipv4 addresses
			if (address instanceof Inet6Address)
				continue;

			//No Unreachable
			if (!address.isReachable(3000))
				continue;

			String addr = address.toString();
			ip_addr = addr.substring(addr.indexOf("/") + 1);
			break;
		}

		return ip_addr;

	}

	/**
	 * Leggo file /mnt/config/ztcInfo.txt per estrarre tftpAddress e boot file name
	 */
	private static  Map<String, String> getDHCPInfosMap(){
		BufferedReader reader;
		Map<String, String> returnValuesMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(
			"/mnt/config/ztcInfo.txt"));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				if (line.contains("tftpAddress")) {
					returnValuesMap.put("tftpAddress", line.substring(line.indexOf(":") + 1));
				} else if (line.contains("bootFileName")) {
					returnValuesMap.put("bootFileName", line.substring(line.indexOf(":") + 1));
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnValuesMap;
	}


	//Metodo che esegue il Download di ICF.json tramite tftp
	private static void icfDownload(String tftp_ip, String file_name, String local_file) {
		try {
			System.out.println("Getting ICF file from server .");
			String cmd = scriptDir + "icfDownload.sh " + tftp_ip + " " + file_name  + " " + local_file;

			Process pr = Runtime.getRuntime().exec(cmd);
			while(!pr.waitFor(5, TimeUnit.SECONDS))
			{
				System.out.print(".");
			}
			System.out.println();

			List<String> errors= UtilitiesZero.readError(pr);

			if(errors.isEmpty()) {
				System.out.println("No errors with the ICF download");

			}else {
				System.out.println("We encountered this errors: " + errors.toString());
				System.exit(1);
			}

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	//Metodo per eseguire richiesta DHCP e download ICF
	public static void exec() throws IOException {

		String interface_name = getInterfaceName();

		dhcpRequest(interface_name);

		Map<String, String> tftpInfos = getDHCPInfosMap();
		icfDownload(tftpInfos.get("tftpAddress"),tftpInfos.get("bootFileName"), scriptDir + tftpInfos.get("bootFileName"));
	}
}
