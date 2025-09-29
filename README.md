1. Download the project and open in eclipse IDE.
2. Run RepairShopApplication as a java application.
3. I recommend downloading postman to send api requests.



Vadybininkas:
1. POST | http://localhost:8080/api/customers | body: {"code": "CD56T96", "name": "Jane doe"}

2. GET 	| http://localhost:8080/api/customers/{code}
3. GET 	| http://localhost:8080/api/repairs/by-customer/{code}

4. GET 	| http://localhost:8080/api/repairs/{id}

5. POST | http://localhost:8080/api/repairs
body:
Example 1:
{
  "customer": {
    "customer_code": "TYH865",
    "customer_name": "steve smith"
  },
  "product_name": "phone",
  "description": "phone died"
}

Example 2:
{
  "customer": {
    "customer_code": "69",
    "customer_name": "bob ross"
  },
  "product_name": "tablet",
  "parts": [
    {
      "part_name": "screen",
      "part_price": 50
    },
    {
      "part_name": "keyboard",
      "part_price": 30
    }
  ],
  "description": "fix my screen pls",
  "status": "Pending"
}

6. POST | http://localhost:8080/api/repairs/finish/{id}

7. PUT 	| http://localhost:8080/api/repairs/{id} body: {json to update part or all of the data.} example {"finalPrice": 52}



Meistras:

1. POST | http://localhost:8080/api/repairs/start/{id}

2. POST | http://localhost:8080/api/repairs/fixed/{id}

3. POST | http://localhost:8080/api/repairs/{id}/add-parts | body: [ { "part_name": "screen", "part_price": 50 }, { "part_name": "battery", "part_price": 80 }]


Buhalteris:
1. GET 	| http://localhost:8080/api/customers/top-profit/{count}
2. GET 	| http://localhost:8080/api/products/top-repaired/{count}
3. GET  | http://localhost:8080/api/repairs/by-date-range?start=2025-09-01&end=2025-09-30


Klientas:
1. GET 	| http://localhost:8080/api/repairs/{id}
