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
