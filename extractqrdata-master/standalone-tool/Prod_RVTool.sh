#Maximum number of books which can be reverse verified
max_books=1000
# Thread Pool workers
pool_workers=4

export LIB_PATH=/d/diksha_rv_tool/extractqrdata-master/standalone-tool/lib
​
export CLASSPATH=.:${LIB_PATH}/apiguardian-api-1.1.2.jar:${LIB_PATH}/commons-codec-1.11.jar:${LIB_PATH}/commons-collections4-4.4.jar:${LIB_PATH}/commons-compress-1.19.jar:${LIB_PATH}/commons-csv-1.9.0.jar:${LIB_PATH}/commons-logging-1.2.jar:${LIB_PATH}/commons-math3-3.6.1.jar:${LIB_PATH}/core-3.4.1.jar:${LIB_PATH}/curvesapi-1.06.jar:${LIB_PATH}/fontbox-2.0.25.jar:${LIB_PATH}/hamcrest-core-1.3.jar:${LIB_PATH}/httpclient-4.5.13.jar:${LIB_PATH}/httpcore-4.4.13.jar:${LIB_PATH}/jackson-annotations-2.13.0.jar:${LIB_PATH}/jackson-core-2.13.0.jar:${LIB_PATH}/jackson-databind-2.13.0.jar:${LIB_PATH}/jai-imageio-core-1.4.0.jar:${LIB_PATH}/jai-imageio-jpeg2000-1.3.0.jar:${LIB_PATH}/jakarta.annotation-api-1.3.5.jar:${LIB_PATH}/javase-3.4.1.jar:${LIB_PATH}/javax.annotation-api-1.3.2.jar:${LIB_PATH}/jcommander-1.78.jar:${LIB_PATH}/jul-to-slf4j-1.7.32.jar:${LIB_PATH}/junit-4.13.2.jar:${LIB_PATH}/junit-jupiter-api-5.8.2.jar:${LIB_PATH}/junit-platform-commons-1.8.2.jar:${LIB_PATH}/log4j-api-2.16.0.jar:${LIB_PATH}/log4j-to-slf4j-2.16.0.jar:${LIB_PATH}/logback-classic-1.3.0-alpha10.jar:${LIB_PATH}/logback-core-1.3.0-alpha10.jar:${LIB_PATH}/opentest4j-1.2.0.jar:${LIB_PATH}/pdfbox-2.0.25.jar:${LIB_PATH}/poi-4.1.2.jar:${LIB_PATH}/poi-ooxml-4.1.2.jar:${LIB_PATH}/poi-ooxml-schemas-4.1.2.jar:${LIB_PATH}/reverseverification-0.0.1-SNAPSHOT.jar:${LIB_PATH}/slf4j-api-2.0.0-alpha4.jar:${LIB_PATH}/snakeyaml-1.29.jar:${LIB_PATH}/SparseBitSet-1.2.jar:${LIB_PATH}/spring-beans-5.3.9.jar:${LIB_PATH}/spring-boot-2.6.1.jar:${LIB_PATH}/spring-boot-autoconfigure-2.6.1.jar:${LIB_PATH}/spring-boot-starter-2.6.1.jar:${LIB_PATH}/spring-boot-starter-logging-2.6.1.jar:${LIB_PATH}/spring-core-5.3.13.jar:${LIB_PATH}/spring-jcl-5.3.13.jar:${LIB_PATH}/spring-web-5.3.9.jar:${LIB_PATH}/xmlbeans-3.1.0.jar

java -Xms1024m -Xmx4096m -cp $CLASSPATH reverseverification.service.ReverseVerificationMain $max_books $pool_workers https://diksha.gov.in/dial http://localhost:8080/api/rv/verify true
