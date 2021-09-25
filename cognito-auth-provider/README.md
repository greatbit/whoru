Cognito auth provider allows using AWS Cognito to authenticate users

In order to use Cognito, enable the following parameters in your Spring Application:

Set Cognito as default AUTH provider
whoru.auth.provider=ru.greatbit.whoru.auth.providers.CognitoAuthProvider

Set required properties:

UI login form provider by AWS
cognito.login.url=https://YOUR_DOMAIN.auth.AWS_REGION.amazoncognito.com/login?client_id=CLIENT_ID&response_type=code&scope=aws.cognito.signin.user.admin+email+openid+phone+profile&redirect_uri=https://YOUR_SERVICE/idpauth
aws.cognito.access.key=AWS_ACCESS_KEY
aws.cognito.secret.key=AWS_SECRET_KEY
aws.cognito.region=AWS_REGION
aws.cognito.oauth.endpoint=https://YOUR_DOMAIN.auth.AWS_REGION.amazoncognito.com/
aws.cognito.client.id=CLIENT_ID
aws.cognito.redirect.url=https://YOUR_SERVICE/idpauth

After login user will be re-directed to the url specified above with an authorisation code as a query parameter: https://YOUR_SERVICE/idpauth
Just re-send it as is to one of your server endpoints and call doAuth on the provider:
 
```authProvider.doAuth(request, response);```

Provider will get a user profile from Cognito and set a session to your session provider


