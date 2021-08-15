#Creo nuovo prodotto
# 6109031169d5116f05c9f446
 curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"name": "Coca Cola", "description": "A good drink", "pictureUrl": "CocaColaUrl", "category": "Drink", "price": 0.99 }' \
 "172.20.208.1:8200/products"

#Creo nuovo prodotto
# 6109033269d5116f05c9f447
  curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiNjBmODAzZjgwYjgxZDBmN2JhODgxZTczIiwiaWF0IjoxMDE2MjM5MDIyLCJleHAiOjYwMTYyMzkwMjJ9.CUEY8nJrjtZAxaPVj5KTelTAzTOaNFoIdvUfhstCUFNBPeRoEM2Gnyq6srWYiihYIrTqbtP4-9ZU-IJnoMBQkA" \
  -H "Content-Type: application/json" \
  -d '{"name": "Sprite", "description": "A good drink2", "pictureUrl": "SpriteURL", "category": "Drink", "price": 3 }' \
 "localhost:8081/products"

6117d21238e21856d4b9fe8e
6117d23a38e21856d4b9fe8f
6117d24c38e21856d4b9fe90

#Creo nuovo warehouse
# 6109036d69d5116f05c9f448, 6109037069d5116f05c9f449
curl -i -v -X POST  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
 "172.20.208.1:8200/warehouses"

#correlo il prodotto al warehouse
curl -i -v -X PATCH  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
  -d '{"products" : [{"id": "6109031169d5116f05c9f446", "alarm": 24, "quantity": 30}, {"id": "6109033269d5116f05c9f447", "alarm": 44, "quantity": 30}]}' \
 "172.20.208.1:8200/warehouses/6109036d69d5116f05c9f448"

#get WH
 curl -i -v \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
 "172.20.208.1:8200/warehouses/6109036d69d5116f05c9f448"

#delete products both from WHs and from products
curl -i -v -X DELETE  \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkFETUlOIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCIsImlhdCI6MTAxNjIzOTAyMiwiZXhwIjo2MDE2MjM5MDIyfQ.UgdtjYTDBh7jt5z-lA3pyVLwdS1fzFwJQqRnaHw8q6yctUkgLzHHfIDRMJYoO5qHq3DISeClH09oRKM92RLVpw" \
  -H "Content-Type: application/json" \
 "172.20.208.1:8200/products/6109033269d5116f05c9f447"
