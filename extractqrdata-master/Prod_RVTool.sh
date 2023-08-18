#Maximum number of books which can be reverse verified
max_books=200
# Thread Pool workers
pool_workers=4
java -Xms1024m -Xmx4096m -jar RVTool-3.0.0.jar $max_books $pool_workers https://diksha.gov.in/dial https://supporttool.diksha.gov.in/api/rv/verify true
