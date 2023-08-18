Reverse Verification Microservice
------------------------
### Tool Briefing
Due to manual addition of QR codes in the textbooks, wrong mapping of QR codes were observed. To cross verify the QR code mapping before 
the textbooks is printed/circulated the Reverse Verification tool was introduced.
Reverse Verification tool has 2 parts:<br>
- RV Microservice<br>
- QR Extract Data<br>

### Introduction
In this Section we are working on installation of RV Microservice. This service will be installed generally in server and will act as a bridge between Database application and QR extract data tool.

RV Microservice is a independent Service, which is introduced to run below mentioned jobs in the background:<br>
- Scanning of qr codes from the pdf files<br>
- Verifying the qr codes<br>
- Generation of reverse verification report<br>

### Prerequisite 

-   **[JAVA 11](https://www.oracle.com/java/technologies/downloads/#java11)**<br>
-   **[Maven](https://maven.apache.org/index.html)**<br>
- Minimum Hardware Requirements:<br>
  >  Single Core server<br> 
  >  8 GB RAM<br> 
  >  25GB Harddisk<br>
- Mysql DB (Version 8.0.17)<br>
This application is a spring-boot application build with maven that
means this application will run as a microservice & as spring-boot
internally has tomcat server built with it we don�t need to provide
external server & server configuration for the same,

### Installation of mysql on server:<br> 
-   **For Mac:**<br><br>
	>###### [Installer](https://dev.mysql.com/doc/mysql-osx-excerpt/5.7/en/osx-installation-pkg.html)<br>
	>###### [Installation Guide](https://medium.com/employbl/how-to-install-mysql-on-mac-osx-5b266cfab3b6)<br>
	>###### [Download link](https://cdn.mysql.com//Downloads/MySQL-8.0/mysql-8.0.18-macos10.14-x86_64.dmg)<br>
	>###### [Workbench](https://cdn.mysql.com//Downloads/MySQLGUITools/mysql-workbench-community-8.0.18-macos-x86_64.dmg)<br>
-   **For Linux:**<br><br>
	>###### [Installer](https://dev.mysql.com/doc/refman/8.0/en/linux-installation.html)<br>
	>###### [Installation Guide](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-18-04)<br>
-   **For Windows:**<br><br>
	>###### [Installer](https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html)<br>
	>###### [Installation Guide](https://netbeans.org/kb/docs/ide/install-and-configure-mysql-server.html)<br>
<br>
### Steps for Bringing up RV MicroServices
---

-	#### Step 1: 
	Clone/Download the MicroService files from GIT `https://github.com/DIKSHA-NCTE/reverse_verification_api_microservice.git`

-	#### Step 2:<br> 
	Navigate to the cloned path in Server/Desktop. Configure the below mentioned files
	- ###### Setup of Environment Variables:<br>
		Update the database details in `set_env_var.sh` file :<br>
		Update the API details in `set_API_var.sh` file :<br>
    	Open GIT Bash/Terminal and run the below commands<br>
		`chmod a+x set_env_var.sh` <br>
		`source ./set_env_var.sh` <br><br>
		`chmod a+x set_API_var.sh` <br>
		`source ./set_API_var.sh` <br><br>
        run `echo $qr_db_user`, `echo $qr_db_pwd`, `echo $qr_db_schema` to check if the variables are set.
       
	- ###### Scripts for creating database and tables(Execute only if setting up a loacal DB for the first time, Do not run with prod server DB):<br>
		Run the create_db_tables.sh for creating specified database and tables :<br>
	`chmod a+x create_db_tables.sh`<br>
`./create_db_tables.sh`<br>

>**Issue you can Face here:** sh: command not found: mysql<br>
>###### Solution: Add Mysql path in the environmental variable(eg: run the following command in commandPrompt "export PATH=${PATH}:/usr/local/mysql/bin")<br>


-	#### Step 3:<br> 
	###### Verifying last step:
    Fire the below mentioned commands to check if the database and tables are created.
`show databases;` &
`use <schemaName mentioned in set_env_var.sh file>;`
`show tables;`
You should see 5 tables listed below:


| Table | Description |
| ------------- | ------------- |
| QR_PACKET  | This table contains QR packet details and their respective reverse verification details  |
| QR_PACKET_HISTORY  | Complete history of all QR Reverse Verifications for tenants |
| QR_TOKEN  | Tokens created for each tenant  |
| QR_API_LOG_BACKUP  | This table contains all the API requests fired and corresponding responses wrt to QR codes  |
| PACKETFILE  | This table has details for QR packet  |

<br>
#### Changes in pom.xml 
Open pom.xml, search for `mysql-connector-java`. Update the version with the Mysql version which was installed in `Step 1`. Version has to be correctly updated else will lead to
connection errors with mysql.


- ##### Running the tool:

	 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Go to the root folder of the project where code is cloned (EX:
/home/opts/reverse\_verification\_api\_microservice)
Check if any service is running in your desired port by command & get
the pid. The default port no is 8080<br>

	For Unix machine run the below commands<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;	` lsof -i :<port_no>` 	<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Kill the process - `Kill -9 <p_id>` <br>
     
     For Windows machine run the below commands<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;	`netstat -ano|findstr 8080` 	<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Kill the process - `taskkill /PID <typeyourPIDhere> /F` <br>
     
     
- #####  For starting the service we need to go to the root folder & run the command-

  - `mvn clean  install -Dmaven.test.skip=true` to install the
    dependencies & build the project.
   -   `nohup mvn spring-boot:run &` to run the application on that
    particular system & your application is ready & running.

-	#### Step 4:
	###### Creation of Tokens for Tenants:<br>
	Configure tokenService.ini file with server url, tokenurl and tenant fields.<br> Only one token is supported for tenant.<br>

	##### For Mac/Linux Systems:<br> 
	First step is done only for the first
time<br> `chmod a+x create_token.sh` <br> `./create_token.sh` <br> If the token is not
yet created new token will be available on command line. Else error will
be thrown.<br><br>

#### Note:
```sh
-	The Token generated in the above step need to be used while running Extract QR data tool.
-	This job will be running in the background, QR Extract data will call this service to Reverse verify the QR data.
-	As mentioned in the 'Tool Briefing', install QR Extract data tool from GIT : 'https://github.com/DIKSHA-NCTE/extractqrdata' in case not installed already
```

##### For Windows Systems:
Check if curl is installed. Else install
curl:<br>
https://develop.zendesk.com/hc/en-us/articles/360001068567-Installing-and-using-cURL\#install
cation API
