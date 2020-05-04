# API Docs

# Show Data
## Blockchain
Show all blockchain content

url: `/blockchain`

header:
```
Conten-Type application/json
```

method: `GET`

response:
```json
[
  {
      "index": 0,
      "nonce": 0,
      "timestamp": "0",
      "data": [],
      "hash": "F3B5498406092FDA136C36E5CED693A8F95A0CB7C9AD7EE6DF2BFEC55DBE97A59C14A60C8556BE092D65D95E0A69DE9F4EC2C7C986794C836D2A8BF5AD29A5E6",
      "prevHash": ""
  },
  {
      "index": 1,
      "nonce": 4357,
      "timestamp": "1588521218770",
      "data": [
          {
            "data": "Content 1",
            "timestamp": "1588521209166"
          },
          {
            "data": "Content 2",
            "timestamp": "1588521214145"
          },
          {
            "data": "Content 3",
            "timestamp": "1588521218753"
          }
      ],
      "hash": "00038D2E2370210491BA305B0A7952BC09EC37AFFC9C130E15C1E7900B50DD4A52B51D22ABAFA374C60728647D7F7D9050FBB41AF5FCD00CBF3308DB6A24FBA4",
      "prevHash": "F3B5498406092FDA136C36E5CED693A8F95A0CB7C9AD7EE6DF2BFEC55DBE97A59C14A60C8556BE092D65D95E0A69DE9F4EC2C7C986794C836D2A8BF5AD29A5E6"
  }
]
```

## Data Pool
Show all data pool content

url: `/pool`

header:
```
Conten-Type application/json
```

method: `GET`

response:
```json
[
  {
    "data": "Content1",
    "timestamp": "1588551785529"
  },
 {
    "data": "Content2",
    "timestamp": "1588551785666"
  }
]
```

## Last Block
Show last block

url: `/last_block`

header:
```
Conten-Type application/json
```

method: `GET`

response:
```json
{
    "index": 1,
    "nonce": 4357,
    "timestamp": "1588521218770",
    "data": [
        {
          "data": "Content 1",
          "timestamp": "1588521209166"
        },
        {
          "data": "Content 2",
          "timestamp": "1588521214145"
        },
        {
          "data": "Content 3",
          "timestamp": "1588521218753"
        }
    ],
    "hash": "00038D2E2370210491BA305B0A7952BC09EC37AFFC9C130E15C1E7900B50DD4A52B51D22ABAFA374C60728647D7F7D9050FBB41AF5FCD00CBF3308DB6A24FBA4",
    "prevHash": "F3B5498406092FDA136C36E5CED693A8F95A0CB7C9AD7EE6DF2BFEC55DBE97A59C14A60C8556BE092D65D95E0A69DE9F4EC2C7C986794C836D2A8BF5AD29A5E6"
}
```

# Add Data
## Add to Data Pool
url: `/add`

header:
```
Conten-Type application/json
```

method: `POST`

body:
```json
{
    "data": "Content"
}
```

response:
```json
{
    "OK": true
}
```


# Peer

## Get All Peer
url: `/peer`

header:
```
Conten-Type application/json
```

method: `GET`

response:
```json
{
    "hosts": ["http://host:port", "http://host2:port"]
}
```

## Add Peer
url: `/peer/add`

header:
```
Conten-Type application/json
```

method: `POST`

body:
```json
{
    "host": "http://host:port"
}
```

response:
```json
{
    "hosts": ["http://host:port", "http://host2:port"]
}
```

## Clear Peer
url: `/peer/clear`

header:
```
Conten-Type application/json
```

method: `POST`

body:
```json
{}
```

response:
```json
{
    "hosts": ["http://host:port", "http://host2:port"]
}
```
