#!/bin/bash
#Including .ini file
. tokenService.ini
#Test
data=`curl ${server}${tokenurl}${tenant}`
#echo $data
# {"errmsg":"Tenant already exists","status":"Failure","token":null}
echo "$data" | sed -E 's/.*"token":"?([^,"]*)"?.*/\1/'
#echo $token