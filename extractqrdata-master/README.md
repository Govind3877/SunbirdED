## Extractqrdata Tool

### Tool Briefing
Due to manual addition of QR codes in the textbooks, wrong mapping of QR codes were observed. To cross verify the QR code mapping before 
the textbooks is printed/circulated the Reverse Verification tool was introduced.
Reverse Verification tool has 2 parts:<br>
- RV Microservice<br>
- QR Extract Data<br>
### Introduction:
In this Section we are working on installation of RV Extractqrdata tool. The RV QR extract tool does below mentioned operation:<br>
- Extraction of qr codes from the pdf files.<br>
- Gathering of Packets info (TextBook details) which need to be reverse verified.<br>
- Generation of reports in .xls format for QR codes present in shared packets.<br>

#### Minimum Hardware Requirements

  >  Single Core server<br> 
  >  8 GB RAM<br> 
  >  25GB Harddisk<br>
- Oracle Java 11 and above<br>
-  Maven<br>                                       
- Tokens generated from Microservices server is used as token in settings.json<br>
#### Project Setup Instructions<br> 
- ##### Step 1 <br />
	-	Clone or download project.
	- Provide user inputs like `booksDir`,`token` (Token generated using createToken service), `isReverseVerificationRequired` with y/n value and `maxBookLimit` maximum book to be reverse verified in settings.json. <br><br>
	##### Input Configuration:<br>
	settings.json <br>
  ```
  "booksDir":"path_to_root_directory_of_specified_books",
  "token":"token_shared",
  "isReverseVerificationRequired":"y/n",
  "maxBookLimit":"number_of_books_to_be_reverse_verified"
  ``` 
	 packets.json
     
  ```
  Array of all Packet names of the books which need to be verified.
  These books are placed in "bookDir" path which is mentioned in settings.json.
  ex of Packet.json entries are as shown below.
  ["DO_12345678910101112","DO_12345678910101212"]
  ```
- ##### Step 2 <br>

  ##### Generating Executable Jar
  `mvn clean install` <br>
  - The JAR will be available inside target folder. <br>
  - move the generated jar from target to `standalone-tool\lib` path<br>
  - move the `settings.json` and `packets.json` to 'standalone-tool` folder.

  ##### Configure Prod_RVTool.sh<br>
  
  ```
  "max_books" : < Count of Total Books which need to be reverse verified >,
  "pool_workers" : <Count of Parallel instances, by default its set to 4>
  "LIB_PATH" : <Path of the standalone-tool\lib folder >
  java -Xms1024m -Xmx4096m -cp ${CLASSPATH} reverseverification.service.ReverseVerificationMain ${max_books} ${pool_workers} platform_url_for_dial_code rv_microservices_server true
  
  ```
	##### Here <br>
	- platform_url_for_dial_code - `https://domain_name/dial`<br>
	- rv_microservices_server - `https://qr_domain_name/api/rv/verify`<br>

- ##### Step 3 <br>
	- Before running Prod_RVTool.sh make sure the RV Microservice is running. The Microservice services is called to validate the QR code fetched from QR extractor tool.
	- Post configuring "Prod_RVTool.sh" run the same for Reverse Verifying the Books.
	- Once the Job is completed, the Reports folder will be opened and the RV report will be available in the <Present Date>/"RV Report_DD-MM-YYYY HHHHHH.csv" file.
<br><br><br><br>
