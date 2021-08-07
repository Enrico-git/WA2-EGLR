#######################################################
## ENDPOINTS FOR ORDER SERVICE
#######################################################
# GET all orders
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
localhost:8081/orders -v

#GET order by ID
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 localhost:8081/orders/60f41a3b1edbdee9c3755c41 -v

#UPDATE order by ID (only ADMIN can)
curl -X PATCH -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
-H "Content-Type: application/json" \
-d '{"status": "ISSUED"}' \
localhost:8081/orders/60f69c0e53534ae5f83a172b -v

#DELETE order by ID (only if buyer and status == ISSUED)
curl -X DELETE -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.gXPLLzL8o7OTOzZJHmtwjT9s-y6HSjOj7QLohCWHoIXsqywSPCDlpu1cw7E23g5QjlwnGL6kMbnNwTwhPCbBBA" \
localhost:8081/orders/60f6b4ed53534ae5f83a1739 -v

#######################################################
## ENDPOINTS FOR WALLET SERVICE
#######################################################
#######################################################
## GET /wallets/{walletID}  -> wallet by ID
#######################################################

#401 Unauthorized
curl -i -v localhost:8081/wallets/610a7a7315a05f3ac4875552

#200 OK [CUSTOMER]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552"

#400 Bad Request - Not valid objectId
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/pino1"

#404 Wallet Not Found [Authorized customer]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/60f8070fa124b7631238f254"

#######################################################
## POST /wallets/ -> create wallet [ADMIN ONLY]
#######################################################

 #200 OK - ADMIN
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 "localhost:8081/wallets"

#400 Bad request: Denied - CUSTOMER
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 localhost:8081/wallets

########################################################################
## POST /wallets/{walletID}/transactions -> CreateTransaction [ADMIN]
########################################################################

 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
  -H "Content-Type: application/json" \
  -d '{"amount": 150, "orderID": "60f66fd598f6d22dc0301234"}' \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions"

 #enable at in WSL2
 sudo service atd start

 #Test many concurrent requests (@Version)
 echo "curl -i -v -X POST  \
  -H \"Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg\" \
  -H \"Content-Type: application/json\" \
  -d '{\"amount\": 150, \"orderID\": \"60f66fd598f6d22dc0301234\"}' \
 \"localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions\"" | at 15:39

################################################################################
## GET /wallets/{walletID}/transactions -> get all transactions (pageable)
################################################################################

 #200 OK [CUSTOMER] - no window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions"

 #200 OK [CUSTOMER] - no window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions?page=0&size=1"

#200 OK [CUSTOMER] - window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions?from=1627019378000&to=1627020638000"

 #200 OK [CUSTOMER] - window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions?from=123123&to=456456&page=0&size=1"

################################################################################
## GET /wallets/{walletID}/transactions/{transactionID} -> getTransactionDetails
################################################################################

#200 OK [CUSTOMER] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions/610a7b978ef83541ab0b1d3a"

#200 OK [ADMIN] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.T9yAM3lJa0i7vYl1THWUH0_Xp7bbNt-FhqoV1Nx1M9aoGH_hlAPI2FOTKoH-MzA_BWfcLfA8JUdr0Xe_ftscvg" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions/610a7b978ef83541ab0b1d3a"

#404 Not found - Wallet was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/60faeed2c3e740711b059afb/transactions/610a7b978ef83541ab0b1d3a"

#404 Not found - Transaction was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiNjBmNjZmZDU5OGY2ZDIyZGMwMzA5MmQ0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE4MTYyMzkwMjJ9.WoDCPnmcLXLw4_pCPll82QSnnbwSA5QBuQlaX-cz42X-ZBem3KfYLi1EjDYdyVMxhd_nUFkIodtpCP1mPHF_kw" \
 "localhost:8081/wallets/610a7a7315a05f3ac4875552/transactions/60fa74dbc00ce9721dde8ced"

#######################################################
## ENDPOINTS FOR WAREHOUSE SERVICE
#######################################################
#Creo nuovo prodotto
# 6109031169d5116f05c9f446
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"name": "Coca Cola", "description": "A good drink", "pictureUrl": "CocaColaUrl", "category": "Drink", "price": 0.99 }' \
 "localhost:8081/products"

#Creo nuovo prodotto
# 6109033269d5116f05c9f447
  curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"name": "Sprite", "description": "A good drink2", "pictureUrl": "SpriteURL", "category": "Drink", "price": 3 }' \
 "localhost:8081/products"

#Creo nuovo warehouse
# 6109036d69d5116f05c9f448, 6109037069d5116f05c9f449
curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
 "localhost:8081/warehouses"

#correlo il prodotto al warehouse
curl -i -v -X PATCH  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"products" : [{"id": "6109031169d5116f05c9f446", "alarm": 24, "quantity": 30}, {"id": "6109033269d5116f05c9f447", "alarm": 44, "quantity": 30}]}' \
 "localhost:8081/warehouses/6109036d69d5116f05c9f448"

#get WH
 curl -i -v \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
 "localhost:8081/warehouses/6109036d69d5116f05c9f448"

#delete products both from WHs and from products
curl -i -v -X DELETE  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
 "localhost:8081/products/6109033269d5116f05c9f447"
