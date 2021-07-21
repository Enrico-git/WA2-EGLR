##GET wallet by ID

#401 Unauthorized
curl -i -v 172.20.176.1:8100/wallets/60f8070fa124b7631238f256

#200 OK [CUSTOMER]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.176.1:8100/wallets/60f8070fa124b7631238f256

#400 Bad Request - Not valid objectId
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.176.1:8100/wallets/pino1

#404 Not Found [Authorized customer]
curl -i -v -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 172.20.176.1:8100/wallets/60f8070fa124b7631238f254
