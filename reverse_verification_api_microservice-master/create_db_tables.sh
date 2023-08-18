qr_paket_table=$(cat ./db_scripts/QR_PACKET_TableCreationScript.sql)
qr_paket_history=$(cat ./db_scripts/QR_PACKET_HISTORY_TableCreationScript.sql)
qr_token=$(cat ./db_scripts/QR_TOKEN_TableCreationScripts.sql)
qr_api_backlog=$(cat ./db_scripts/QR_API_LOG_BACKUP_TableCreationScript.sql)
qr_packet_file=$(cat ./db_scripts/QR_PACKET_FILE_TableCreationScript.sql)

-u "root" -p"root123" -e "CREATE DATABASE "qr_db_schema"; USE "qr_db_schema"; qr_paket_table; qr_paket_history; qr_token; qr_api_backlog; qr_packet_file"

