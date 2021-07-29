#######################################################
## GET /wallets/{walletID}  -> wallet by ID
#######################################################

#401 Unauthorized
curl -i -v 172.20.176.1:8100/wallets/610285a7b5daaa4cc35b7caa

#200 OK [CUSTOMER]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa

#400 Bad Request - Not valid objectId
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.208.1:8100/wallets/pino1

#404 Not Found [Authorized customer]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.208.1:8100/wallets/60f8070fa124b7631238f254


#######################################################
## POST /wallets/ -> create wallet [ADMIN ONLY]
#######################################################

 #200 OK - ADMIN
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 "172.20.208.1:8100/wallets/"

#400 Bad request: Denied - CUSTOMER
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
  -H "Content-Type: application/json" \
  -d '{"userID": "60f66fd598f6d22dc03092d4"}' \
 172.20.208.1:8100/wallets/

########################################################################
## POST /wallets/{walletID}/transactions -> CreateTransaction [ADMIN]
########################################################################

 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"amount": 350.33, "orderID": "60f66fd598f6d22dc0301234"}' \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions"

################################################################################
## GET /wallets/{walletID}/transactions -> get all transactions (pageable)
################################################################################

 #200 OK [CUSTOMER] - no window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions"

 #200 OK [CUSTOMER] - no window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions?page=0&size=1"

#200 OK [CUSTOMER] - window
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions?from=1627019378000&to=1627020638000"

 #200 OK [CUSTOMER] - window + page
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions?from=123123&to=456456&page=0&size=1"



################################################################################
## GET /wallets/{walletID}/transactions/{transactionID} -> getTransactionDetails
################################################################################

#200 OK [CUSTOMER] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions/60fa74dbc00ce9721dde8cec"

#200 OK [ADMIN] - get information
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions/60fa74dbc00ce9721dde8cec"

#404 Not found - Wallet was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/60faeed2c3e740711b059afb/transactions/60fa74dbc00ce9721dde8cec"

#404 Not found - Transaction was not found
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 "172.20.208.1:8100/wallets/610285a7b5daaa4cc35b7caa/transactions/60fa74dbc00ce9721dde8ced"

################################################################################
## GET /wallets/ -> MOCK for KAFKA "payment_request" or "abort_payment_request
################################################################################

curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.VOu9FelxRLawUkJIkpJQAH2ESbAacWVQo1JYjyCYoqZPuYEuWhXzLA_g_qxrUeq-e9MIsvVyc6nBeXDXHqS4uw" \
 "172.20.208.1:8100/wallets/"

#list all topics in kafka
WSL2:$ docker exec kafka kafka-topics --list --zookeeper zookeeper:2181

#delete topics in kafka
WSL2:$ docker exec kafka kafka-topics --delete --zookeeper zookeeper:2181 --topic <topic_name>
