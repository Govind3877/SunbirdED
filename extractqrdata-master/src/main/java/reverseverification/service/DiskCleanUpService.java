package reverseverification.service;
/**
 * this class is used for 
 * clean up service
 */
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;


class DiskCleanUpService {
	private static final Logger log = LoggerFactory.getLogger(DiskCleanUpService.class);
		
	//for deleting generated images and payload directory
	protected static void deleteMultipleDirectory(Set<String> deletionSet) {
		try {
			deletionSet.forEach(file->{						
				boolean result = false;
				File deleteFile = new File(file);
				if(deleteFile.exists()) {
					result = FileSystemUtils.deleteRecursively(deleteFile);
				}
				if(!result)
					log.info(deleteFile.getAbsolutePath()+" could not be deleted or does not exist");
			});
		} catch (Exception e) {
			log.error("Error in directory deletion",e);
		}
	}
}
