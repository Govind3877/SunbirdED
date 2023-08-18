package reverseverification.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import reverseverification.domain.RVRequest;
import reverseverification.domain.RVResponse;
import reverseverification.domain.Request;

/**
 * fileName    	:: ReverseVerificationService.java
 * @author 	   	:: ravi
 * description 	:: This class is used to make reverser verification API call
 * @version     :: RVTool-3.0.0
 * 
 * 
 */


class ReverseVerificationService {
	private static final Logger log = LoggerFactory.getLogger(ReverseVerificationService.class);
	private RestTemplate restTemplate = new RestTemplate();

	protected RVResponse reverseVerify(String requestURL, String token, String packetName, List<String> dialCodeList,
			LinkedList<String> fileExecutionDataList, LinkedList<String> pdfCheckSumList, String rvFileName) {
		ResponseEntity<RVResponse> rvResponseEntity;
		rvResponseEntity = null;
		try {
			Request request = new Request();
			request.setPacketName(packetName);
			request.setDialList(dialCodeList);
			request.setFileExecutionData(fileExecutionDataList);
			request.setPdfCheckSum(pdfCheckSumList);
			request.setRvFileName(rvFileName);
			
			RVRequest rvRequest = new RVRequest();
			rvRequest.setRequest(request);
			// preparing authorization headers
			HttpHeaders authorizationHeaders = new HttpHeaders();
			authorizationHeaders.setContentType(MediaType.APPLICATION_JSON);
			authorizationHeaders.add("token", token);
			authorizationHeaders.add("mac", SecurityAttributeService.getMACAddress());
			authorizationHeaders.add("serial", SecurityAttributeService.getMachineDetails());

			HttpEntity<RVRequest> requestEntity = new HttpEntity<>(rvRequest, authorizationHeaders);
			rvResponseEntity = restTemplate.exchange(requestURL, HttpMethod.POST, requestEntity, RVResponse.class);			
		} catch (Exception e) {
			log.error("RVTool did not get response from the server to reverse verify book: " + packetName + " due to",
					e);
			System.out.println("RVTool did not get response from the server to reverse verify book: " + packetName);			
		}
		if (null != rvResponseEntity)
			return rvResponseEntity.getBody();
		else
			return null;

	}

}
