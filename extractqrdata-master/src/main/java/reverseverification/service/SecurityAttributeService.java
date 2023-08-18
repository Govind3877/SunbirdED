package reverseverification.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fileName    	:: SecurityAttributeService.java
 * @author 	   	:: ravi
 * description 	:: This class is used to find MAC , serial, manufacturer and sha256 checksum of PDFs
 * @version     :: RVTool-3.0.0
 * 
 * 
 */


class SecurityAttributeService {
	private static final Logger log = LoggerFactory.getLogger(SecurityAttributeService.class);
	private static String macAddress = null;
	private static String systemDetails = null;

	// method for fetching MAC details
	protected static String getMACAddress() {
		try {
			if (macAddress == null) {
				StringBuffer stringBuffer = new StringBuffer();
				final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface networkInterface = networkInterfaces.nextElement();
					if (networkInterface.isUp()) {
						final byte[] macAddress = networkInterface.getHardwareAddress();
						if (macAddress != null && !isVMMac(networkInterface.getHardwareAddress())) {
							for (int i = 0; i < macAddress.length; i++)
								stringBuffer.append(String.format("%02X%s", macAddress[i],
										(i < macAddress.length - 1) ? "-" : "|"));
						}
					}
				}
				macAddress = stringBuffer.toString().isEmpty() ? "NA" : stringBuffer.toString();
			}

		} catch (SocketException e) {
			log.error("Error finding mac address", e);
		}
		return macAddress;
	}

	// method for fetching system serial and manufacturer details
	protected static String getMachineDetails() {
		try {
			if (systemDetails == null) {
				String OSName = System.getProperty("os.name");
				if (OSName.contains("Windows")) {
					systemDetails = getWindowsMachineDetails();
				} else if (OSName.contains("Linux")) {
					systemDetails = getLinuxMachineDetails();
				} else {
					systemDetails = "NA";
				}
			}
		} catch (Exception e) {
			log.error("Could not reterive machine details", e);
			return "";
		}
		return systemDetails;
	}

	// method for generating file checksum
	protected static String getPDFCheckSum(String pdfFilePath) {
		StringBuffer stringBuffer = new StringBuffer("");
		try (FileInputStream fileInputStream = new FileInputStream(pdfFilePath)) {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			byte[] dataBytes = new byte[1024];
			int bytesRead = 0;

			while ((bytesRead = fileInputStream.read(dataBytes)) != -1) {
				messageDigest.update(dataBytes, 0, bytesRead);
			}
			byte[] digestBytes = messageDigest.digest();

			for (int i = 0; i < digestBytes.length; i++) {
				stringBuffer.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
			}

		} catch (NoSuchAlgorithmException e) {
			log.error("Incorrect encryption type", e);
		} catch (FileNotFoundException e) {
			log.error(pdfFilePath + "file not found to generate checksum", e);
		} catch (IOException e) {
			log.error("I/O exception has occurred while generating checksum for the file" + pdfFilePath, e);
		}

		return stringBuffer.toString();
	}

	// method for filtering VM MACs
	private static boolean isVMMac(byte[] mac) {
		if (null == mac)
			return false;
		byte invalidMacs[][] = { { 0x00, 0x05, 0x69 }, // VMWare
				{ 0x00, 0x1C, 0x14 }, // VMWare
				{ 0x00, 0x0C, 0x29 }, // VMWare
				{ 0x00, 0x50, 0x56 }, // VMWare
				{ 0x08, 0x00, 0x27 }, // Virtualbox
				{ 0x0A, 0x00, 0x27 }, // Virtualbox
				{ 0x00, 0x03, (byte) 0xFF }, // Virtual-PC
				{ 0x00, 0x15, 0x5D }, // Hyper-V
				{ 0x02, 0x15, 0x7A } // Hyper-V

		};

		for (byte[] invalid : invalidMacs) {
			if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2])
				return true;
		}

		return false;
	}

	private static String getLinuxMachineDetails() {
		String machineDetails = null;
		BufferedReader bufferedReader = null;
		try {
			Process systemManufacturer = Runtime.getRuntime().exec("sudo dmidecode -s system-manufacturer");
			Process sytemSerial = Runtime.getRuntime().exec("sudo dmidecode -s system-serial-number");

			bufferedReader = new BufferedReader(new InputStreamReader(systemManufacturer.getInputStream()));
			machineDetails = bufferedReader.readLine();
			systemManufacturer.waitFor();

			bufferedReader = new BufferedReader(new InputStreamReader(sytemSerial.getInputStream()));
			machineDetails = machineDetails + "-" + bufferedReader.readLine();

			sytemSerial.waitFor();
		} catch (Exception e) {
			log.error("Could not fetch Linux machine details", e);
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error("Error closing resource BufferedReader");
			}
		}
		return machineDetails;

	}

	private static String getWindowsMachineDetails() {
		String machineDetails = "";
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				Runtime.getRuntime().exec("wmic bios get serialnumber,Manufacturer").getInputStream()));) {
			String commandOutput;
			while ((commandOutput = bufferedReader.readLine()) != null) {
				machineDetails += commandOutput;
			}
		} catch (Exception e) {
			log.error("Could not fetch Windows machine details", e);
		}
		return machineDetails.replace("Manufacturer", "").replace("SerialNumber", "").trim().replaceAll("\\s{2,}", "-");
	}
}
