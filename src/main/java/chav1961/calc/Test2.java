package chav1961.calc;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
//import oshi.software.os.OperatingSystem;

public class Test2 {
	static String generateLicenseKey() {
        SystemInfo systemInfo = new SystemInfo();
//        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
        ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();

//        String vendor = operatingSystem.getManufacturer();
//        String processorSerialNumber = computerSystem.getSerialNumber();
        String processorIdentifier = centralProcessor.getProcessorIdentifier().getProcessorID();
        int processors = centralProcessor.getLogicalProcessorCount();

        String delimiter = "#";

        
        return ":1:"+computerSystem.getBaseboard().getSerialNumber() +
                delimiter +
               ":2:"+computerSystem.getHardwareUUID()+
                delimiter +
               ":3:"+processorIdentifier +
                delimiter +
               ":4:"+processors;
    }
	
	public static void main(String[] args) {
		 String identifier = generateLicenseKey();
	     System.err.println(identifier);
	}
}
