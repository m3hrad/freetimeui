# Freetime UI

## API endpoint:

### Title: Show friends

- URL: /friends/:id

- Method: Get

- Success Reply:

{
"friends": [{
	"id": 101,
	"firstname": "Mahyar",
	"lastname": "Mohammadi"
	}]
}

- Error Reply:

{
  "error": {
    "code": 404,
    "message": "ID not found"
  }
}

# Signed Apk
- key store path: C:\Mahyar\projects\apk\signKey.jks
- Alias: Bros
- password: Bros123456
- Organizational Unit: Android App
- Organization: Bros Company
