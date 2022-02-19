# MATUGR

## What is Matugr?
Matugr is an Android client-enabling implementation of OAuth 2. It is Kotlin-based and 
leverages modern tools to provide a simple and powerful API for consuming clients.
It supports the best practices of OAuth 2.0, which are represented in the internet draft of OAuth 2.1. 
By extension, Matugr could be considered a hybrid of OAuth 2.0 and OAuth 2.1 (see OAuth 2.1 section and 
OAuth 2.0 Documentation for more details).

Matugr is responsible for:
- Instantiating a redirect activity
- Making authorization requests 
- Processing an authorization response and providing the result to the client
- Making the token network request
- Processing token response and providing result to the client

The consuming client is responsible for:
- Specifying state or code verifier if custom behavior desired
- Specifying redirect url in manifest
- Providing required parameters to Matugr when instantiating the adapter

## Getting Started

### Required Property Setup (Sample App only)
The sample app uses a secrets plugin to pull local properties to be leveraged by the BuildConfig
and the AndroidManifest.

These properties are read from the local.properties file. 
If a property should be excluded, add a "". Below is an example of what to add to the
local.properties:
```
authUrl=https://example.com/authorization
tokenUrl=https://example.com/token
clientId=abcdefghijklmnopqrstuvwxyz
redirectUri=example://redirect
redirectUriScheme=example
scope="offline_access"
```

### Activity Redirect
IMPORTANT
Matugr handles instantiating the redirect activity and processing the authorization response on
behalf of the client. However, during setup, it is required the client specify the `AuthActivity`
in the app's AndroidManifest along with the redirectUriScheme. The sample app demonstrates this
configuration.

### Authorization Request
The authorization request is a coroutine. This means the application process needs to be alive to process this request. 
That being said, the authorization flow is NOT strictly coupled with the application UI, 
i.e. if the user returns to the client application, the authorization flow will remain as it is. 
In turn, the client app needs to cancel authorization if the flow is dead, i.e. the app is alive,
but the user is no longer logging in. See the sample app implementation a demonstration.

### Token Request
Similar to authorization, the token requests are made through coroutines. There is a single API
that allows a grant type of either an authorization code or a refresh token.

## More Information

### Networking Library
OkHttp is the networking library used by Matugr.
This selection aligns with the android platform and mitigates any compatibility issues
from a generalized interface.

### Best Practices/OAuth 2.1
PKCE is required for all OAuth clients using the authorization code flow

The Implicit grant (response_type=token) and the Resource Owner Password Credentials grant
are omitted from the 2.1 specification.

With respect to the above omissions, and considering typical mobile client practices, the only
supported authorization flow in matugr is the Authorization Code flow.

### Supported and Not Supported
Matugr supports authorization and tokens with respect to the standard OAuth 2 framework.

OpenID Connect is not planned to be supported, at least for the foreseeable future. This includes:
- no OpenId discovery
- no OpenId registration

### OAuth 2.0 Documentation
Obtaining Authorization
https://datatracker.ietf.org/doc/html/rfc6749#section-4

Authorization Request:
https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1

Authorization Response:
https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2

PKCE:
https://datatracker.ietf.org/doc/html/rfc7636#section-4

Response Type:
https://www.rfc-editor.org/rfc/rfc6749#section-3.1.1

Code Challenge:
https://datatracker.ietf.org/doc/html/rfc7636#section-4.2

Code Challenge Method:
https://datatracker.ietf.org/doc/html/rfc7636#section-4.2

State:
https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1
https://datatracker.ietf.org/doc/html/rfc6749#section-10.12

Token Request:
https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3

Token Response:
https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.4
https://datatracker.ietf.org/doc/html/rfc6749#section-5.1

Token Error:
https://datatracker.ietf.org/doc/html/rfc6749#section-5.2
