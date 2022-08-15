## Rabobank Assignment for Authorizations Area

## The task at hand

Implement the following business requirement

- Users must be able to create write or read access for payments and savings accounts
- Users need to be able to retrieve a list of accounts they have read or write access for

## Details and Assumptions for implementation:


The application is secured using an OAuth Bearer token (JWT), basic validation of the token is done using a stubbed JWKS store.
It is assumed further validation of the JWT is performed by an API gateway.

#### Notes on implementation:
* AccountNumber is globally unique accross all accoutns (IBAN)
* Account type is irrelevant to POA grants but to demonstrate different accounts  have differing fields in the resulting responses
* Added NONE as an auth type to preserve audit log and allow removal of Authorization
* Account list ONLY includes accounts wit PoA, not accounts owned by self
* PoA can only be granted by account holder, not someone with PoA on that account

#### Limitations
* Test coverage is not excellent: manual integration test UI provided instead
* Code can be cleaned up in some places (replace new ?? with builders in places)
* Mongo queries could be faster and fewer if native join queries on accounts are used
* Did not provide a Dockerfile to create image

## Build

Standard maven build and test
``
mvn clean package
``

You can also load the project in to IntelliJ IDEA, build and run tests there.

## How to Test

Swagger UI is available at: http://localhost:8080/docs/swagger-ui

This assumes you have a JWKS service to validate your bearer token, this is stubbed within the app can be changed via application.yaml, standard spring config parameters.

### Step 1: Authorization Tokens for granter and grantee

* Copy the RSA private key contents from the repo `scripts/credentials/credentials.pem`
  * *n.b. run create_rsa_keys.sh to make new ones, keys should not normally be in source control!*
* Go to https://jwt.io/, change the Encryption type to RS256 and replace the PRIVATE KEY in the box with the one above.
* Generate the GRANTER JWT: set the 'sub' claim to 'MrGranter' copy and paste the encoded JWT somewhere handy
* Generate the GRANTEE JWT: set the 'sub' claim to 'MsGrantee' copy and paste the encoded JWT somewhere handy

## Step 2: Create an account to grant Power of Attorney to

* Use the account API endpoint to create an account for "MrGranter".
* Go to http://localhost:8080/docs/swagger-ui
* In "account-controller" use POST
  * Set an accountNumber **REMEMBER THIS**
  * Choose accountType 'savings' or 'payment'
  * Paste in the GRANTER JWT as saved in Step 1

Check it worked - use GET to list all accounts (use the same GRANTER JWT)

## Step 3: Grant Power of Attorney to grantee

* In swagger UI go to "power-of-attorney" POST
* Post with GRANTER JWT, ACCONT NUMBER and grantee: "MsGrantee" in the Authroization type READ / WRITE (or NONE)

## Step 4: Check Power of Attorney to grantee

* In swagger UI go to "power-of-attorney" GET - Enter *GRANTEE JWT*.
* Check the appropriate accounts is listed

### Step 5: Repeat with other Accounts/Authorizations


