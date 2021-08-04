#######################################################
## GET /wallets/{walletID}  -> wallet by ID
#######################################################

#401 Unauthorized
curl -i -v 172.20.176.1:8100/wallets/610a7a7315a05f3ac4875552

#200 OK [CUSTOMER]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552"

#400 Bad Request - Not valid objectId
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/pino1"

#404 Wallet Not Found [Authorized customer]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/60f8070fa124b7631238f254"


#######################################################
## POST /wallets/ -> create wallet [ADMIN ONLY]
#######################################################

 #200 OK - ADMIN
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 "172.20.208.1:8100/wallets/"

#400 Bad request: Denied - CUSTOMER
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 172.20.208.1:8100/wallets/

########################################################################
## POST /wallets/{walletID}/transactions -> CreateTransaction [ADMIN]
########################################################################

 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
  -H "Content-Type: application/json" \
  -d '{"amount": 150, "orderID": "60f66fd598f6d22dc0301234"}' \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions"

 #enable at in WSL2
 sudo service atd start

 #Test many concurrent requests (@Version)
 echo "curl -i -v -X POST  \
  -H \"Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg\" \
  -H \"Content-Type: application/json\" \
  -d '{\"amount\": 150, \"orderID\": \"60f66fd598f6d22dc0301234\"}' \
 \"172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions\"" | at 15:39

################################################################################
## GET /wallets/{walletID}/transactions -> get all transactions (pageable)
################################################################################

 #200 OK [CUSTOMER] - no window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions"

 #200 OK [CUSTOMER] - no window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions?page=0&size=1"

#200 OK [CUSTOMER] - window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions?from=1627019378000&to=1627020638000"

 #200 OK [CUSTOMER] - window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions?from=123123&to=456456&page=0&size=1"



################################################################################
## GET /wallets/{walletID}/transactions/{transactionID} -> getTransactionDetails
################################################################################

#200 OK [CUSTOMER] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions/610a7b978ef83541ab0b1d3a"

#200 OK [ADMIN] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions/610a7b978ef83541ab0b1d3a"

#404 Not found - Wallet was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/60faeed2c3e740711b059afb/transactions/610a7b978ef83541ab0b1d3a"

#404 Not found - Transaction was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "172.20.208.1:8100/wallets/610a7a7315a05f3ac4875552/transactions/60fa74dbc00ce9721dde8ced"

################################################################################
## GET /wallets/ -> MOCK for KAFKA "payment_request" or "abort_payment_request
################################################################################

curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
 "172.20.208.1:8100/wallets/"

#list all topics in kafka
WSL2:$ docker exec kafka kafka-topics --list --zookeeper zookeeper:2181

#delete topics in kafka
WSL2:$ docker exec kafka kafka-topics --delete --zookeeper zookeeper:2181 --topic <topic_name>
