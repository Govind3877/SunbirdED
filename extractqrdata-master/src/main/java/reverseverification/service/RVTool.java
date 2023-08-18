package reverseverification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RVTool {
	private static final Logger log = LoggerFactory.getLogger(RVTool.class);
	private static final byte JSON_PARSE_EX=100;
	private static final byte JSON_MAP_EX=101;
	private static final byte IO_EX=102;
	
	private static final byte BOOK_COUNT_MISMATCH=10;	
	private static final byte MISSING_VALUE_CONFIG=11;
	private static final byte MISSING_VALUE_SETTINGS=12;
	private static final byte MISSING_VALUE_PACKETS=13;
	private static final byte INVALID_VALUE=14;
	private static final byte API_RESPONSE_ERROR=15;
	private static final byte UNEXPECTED_ERROR_MAIN=16;  
	private static final byte BOOKS_DIR_ERROR=17;
	
	public static void exit(String message) {
		
		switch(message) {
		case "json_parse_ex":
			log.error("RVTool has returned exit Code: "+JSON_PARSE_EX);
			System.out.println("RVTool has returned exit Code: "+JSON_PARSE_EX);
			System.exit(JSON_PARSE_EX);
			
		case "json_map_ex":
			log.error("RVTool has returned exit Code: "+JSON_MAP_EX);
			System.out.println("RVTool has returned exit Code: "+JSON_MAP_EX);
			System.exit(JSON_MAP_EX);
			
		case "io_ex":
			log.error("RVTool has returned exit Code: "+IO_EX);
			System.out.println("RVTool has returned exit Code: "+IO_EX);
			System.exit(IO_EX);
			
		case "book_count_mismatch":
			log.error("RVTool has returned exit Code: "+BOOK_COUNT_MISMATCH);
			System.out.println("RVTool has returned exit Code: "+BOOK_COUNT_MISMATCH);
			System.exit(BOOK_COUNT_MISMATCH);
		
		case "missing_value_config":
			log.error("RVTool has returned exit Code: "+MISSING_VALUE_CONFIG);
			System.out.println("RVTool has returned exit Code: "+MISSING_VALUE_CONFIG);
			System.exit(MISSING_VALUE_CONFIG);
			
		case "invalid_value":
			log.error("RVTool has returned exit Code: "+INVALID_VALUE);
			System.out.println("RVTool has returned exit Code: "+INVALID_VALUE);
			System.exit(INVALID_VALUE);
			
		case "missing_value_packets":
			log.error("RVTool has returned exit Code: "+MISSING_VALUE_PACKETS);
			System.out.println("RVTool has returned exit Code: "+MISSING_VALUE_PACKETS);
			System.exit(MISSING_VALUE_PACKETS);
		
		case "missing_value_settings":
			log.error("RVTool has returned exit Code: "+MISSING_VALUE_SETTINGS);
			System.out.println("RVTool has returned exit Code: "+MISSING_VALUE_SETTINGS);
			System.exit(MISSING_VALUE_SETTINGS);
			
		case "api_response_error":
			log.error("RVTool has returned exit Code: "+API_RESPONSE_ERROR);
			System.out.println("RVTool has returned exit Code: "+API_RESPONSE_ERROR);
			System.exit(API_RESPONSE_ERROR);
			
		case "unexpected_error_main":
			log.error("RVTool has returned exit Code: "+UNEXPECTED_ERROR_MAIN);
			System.out.println("RVTool has returned exit Code: "+UNEXPECTED_ERROR_MAIN);
			System.exit(UNEXPECTED_ERROR_MAIN);
			
		case "books_dir_error":
			log.error("RVTool has returned exit Code: "+BOOKS_DIR_ERROR);
			System.out.println("RVTool has returned exit Code: "+BOOKS_DIR_ERROR);
			System.exit(BOOKS_DIR_ERROR);
			
		}
	}
}
