#GET wallet by ID (no security)

curl -i -v 172.20.176.1:8100/wallets/60f6705998f6d22dc03092d7 #valid request

curl -i -v 172.20.176.1:8100/wallets/pino1 #Not valid objectId

curl -i -v 172.20.176.1:8100/wallets/60f6705998f6d22dc03092d2 #wallet not found
