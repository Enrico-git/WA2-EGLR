# GET all orders
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
localhost:8080/orders -v

#GET order by ID
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxODE2MjM5MDIyfQ.V_ePfXDIFymWiXDs_-599XvNYYwYFMZvsAbAT77UoAIfs9uczLMJLKBXZ-7zVuK0MCJfF8aS7hawYG3vao3yqx" \
 localhost:8080/orders/60f41a3b1edbdee9c3755c41 -v

#UPDATE order by ID (only ADMIN can)
curl -X PATCH -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
-H "Content-Type: application/json" \
-d '{"status": "ISSUED"}' \
localhost:8080/orders/60f69c0e53534ae5f83a172b -v

#DELETE order by ID (only if buyer and status == ISSUED)
curl -X DELETE -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.gXPLLzL8o7OTOzZJHmtwjT9s-y6HSjOj7QLohCWHoIXsqywSPCDlpu1cw7E23g5QjlwnGL6kMbnNwTwhPCbBBA" \
localhost:8080/orders/60f6b4ed53534ae5f83a1739 -v
