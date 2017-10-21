Verify ID tokens:
after a successful sign-in, send the user's ID token to your server using HTTPS.
Then, on the server, verify the integrity and authenticity of the ID token and retrieve the uid from it.
Adding Firebase Admin SDK to server:
Add Firebase to your app:
Navigate to the Service Accounts tab in your project's settings page.
Click the Generate New Private Key button at the bottom of the Firebase Admin SDK section
A JSON file downloaded with below content:
{
  "type": "service_account",
  "project_id": "free-time-c6774",
  "private_key_id": "d8072b7c956ecb10264b0459720d2741532d546c",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDZ2ufx6wv0PODi\nsf53mwm7KqjpX1o6E9hpCjlVk39mN4qAw1h7ulQj5FwFxKI2IyRz2L9FG27Dvnl/\nPHz5xHroIOk67X6NUEK6KZSbV6Hjk9naN8YlQB1e9L9E5Xl6pMuK6uz6DaRPuakq\nE3/UCALr+0qgcHvhrGusNmgiK6076dupQF8E6MFiu6YqlH5I19sSuzBPVIuYHWZl\njyXQ8Fwu3dUTknPZ+GxPI1tUmLlnnDi4n7sS0DvDMYMuLrt69S9qMTjDHmdLnjIs\naf63llPweszjX6r7K/M9zu/vJ8wFbYb8GQY1erGs/N4NxXINT9rGKjGhiqrlda8N\neI5XROHTAgMBAAECggEABQufbY6unMrZwBMa1c6x/5l+ufdRK233boKKTpbdtd9T\nBnJmI5w7UKCvfoHng9UPUt0ez00evpfNtop1fOj1zVeqlYiXv4WvvFU0MawEF5vL\nL6IB2JQ1We3VjchTjOlkiv4WQoauwU3tqTs0YOCJBZZ9sinvJIxBHiCDpUiMeL1O\noN5sYhAhG/3nEH6oRZ3XcmSo8rHZoD45tQXdngH3SW0BJ789XMTjtr9xiA3wqvnq\n+lONcubmWSuFjLMh+rQ+MmpyasOpEnNPnLGWHnT5hOOtqGCr61ziK+TcgdHLRS+g\nqPwgnYIiBvTGw6vKrBNZshKo4gI8bKiz+g0SZgoSGQKBgQD5NHIWSheJXA1JVv/3\n5ayXudj6wlN5IiMtkO3JVW4/QL2ZTa0SKpAg/Ob+ACAQ9IIoAoHZEsF8njMY5Dvh\nril2wRnPIkxoP5k9CorE156M7btjCyJ5B/WM9VfTBOlZsXUt2fZK3qWhZ3DMvxRC\nMVQhM+G5gwECCva0uu4bexoVJQKBgQDfy6A60TJxLuOM1Dv7OeTz5mjQW/2LJFW2\n4f5BWUWlwLSyHd9kcybFSyi39ECsnOZCU0HHaM6t3IYAdvflvDqm+XhJ0OkY5sfs\nh5PL/8Moa8JSy6smALSJ12ucG9B+2KjMQxhO6knByym/zOUT82PluVp9eKcAcTGW\nlOKvmC71lwKBgQC6uNo260Mhb7Cg4CggxZ2BfgYfNZW38KUtEiepMHLPwCGQCYyB\nTRT7MqOdc9iS+7G+asqCLLR/PEYLA4/+tDFrQpqlAg0AG48lFo4NmYW6ELLULlah\nGx6YhIhOm6KAyx+CwIrzZEn0Wv5A23dBdZaU3jvmdUs/DZn2pw2b16weZQKBgQCe\nwdnRv7OT3RppPemrB1/msa3rGUbcnRL9IIZ4nzasJe9JZ62gmZuIXA/vpyL2EHu7\n00oXhr8PUR/yVnL3EzdcWh695qhlFNoAbhEnpzEvjHoWuAqac+ee3SWmYYjfYx0j\ngflNiiB2jIP6XwSmyYM1ZO7PkXaO9r2hv86rmD6/0wKBgBIx6avufCzNx0Ip+CLI\nGlC/Lz8yVGA8oD7Yv6S7Uk9Lu59gCi2PH92aXep1G7NTKk9cqQM265t5SpQUaoGD\nS3sF3h021Fk/4UnZL1NRJ5Jkr0sMalnTjB8FxR1AoW5q9E+24CvPfwGz8tlIutV/\nqN3kna3xkjl5Ab45yTdboLCW\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-9t8a0@free-time-c6774.iam.gserviceaccount.com",
  "client_id": "104790894469224603294",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-9t8a0%40free-time-c6774.iam.gserviceaccount.com"
}
Add the SDK:
$ npm install firebase-admin --save
