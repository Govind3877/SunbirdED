package reverseverification.service;

/**
 * fileName    	:: QRCodeReverseVerficationApplication.java
 * @author 	   	:: ravi
 * description 	:: This class is used to start rv api service
 * @version     :: RVTool-API-3.0.0
 * 
 * 
 */
import java.time.LocalDateTime;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reverseverification.controller.QRController;

@SpringBootApplication
class QRCodeReverseVerficationApplication {
	private static final Logger log = LoggerFactory.getLogger(QRCodeReverseVerficationApplication.class);

	public static void main(String[] args) {
		log.info("QR Code Reverse Verfication Application is started at: " + LocalDateTime.now());
		
		String port="8080";
		Object objPort=port;
		
		SpringApplication app = new SpringApplication(QRController.class);
        app.setDefaultProperties(Collections
          .singletonMap("server.port", objPort));
        app.run(args);
	}
}
