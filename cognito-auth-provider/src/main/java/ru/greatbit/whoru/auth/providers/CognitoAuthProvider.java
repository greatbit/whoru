package ru.greatbit.whoru.auth.providers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.greatbit.whoru.auth.Person;
import ru.greatbit.whoru.auth.RedirectResponse;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import ru.greatbit.whoru.auth.utils.HttpUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.greatbit.utils.string.StringUtils.emptyIfNull;
import static ru.greatbit.whoru.auth.utils.HttpUtils.isTokenAccessRequest;

@Service
public class CognitoAuthProvider extends BaseAuthProvider {

    @Value("${cognito.login.url}")
    private String cognitoLoginUrl;

    @Value("${aws.cognito.access.key}")
    private String awsCognitoAccessKey;

    @Value("${aws.cognito.secret.key}")
    private String awsCognitoSecretKey;

    @Value("${aws.cognito.region}")
    private String awsCognitoRegion;

    @Value("${aws.cognito.oauth.endpoint}")
    private String cognitoOauthEndpoint;

    @Value("${aws.cognito.client.id}")
    private String cognitoClientId;

    @Value("${aws.cognito.redirect.url}")
    private String cognitoRedirectUrl;

    private final long OAUTH_API_TIMEOUT = 30000;

    private final String GRANT_TYPE = "authorization_code";

    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @PostConstruct
    private void postConstruct(){
        if (isEmpty(awsCognitoAccessKey) || isEmpty(awsCognitoSecretKey)){
            logger.warn("Access and secret key are not set for Cognito Auth Provider. " +
                    "In case you are using a different provider - this warning can be ignored");
            return;
        }
        AwsCredentials awsCredentials = AwsBasicCredentials.create(awsCognitoAccessKey, awsCognitoSecretKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsCognitoRegion))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
        if (isTokenAccessRequest(request)) {
            return authByToken(request, response);
        }

        try {
            Cookie sid = HttpUtils.findCookie(request, HttpUtils.SESSION_ID);
            if (sid == null || !sessionProvider.sessionExists(sid.getValue())
                    || !sessionProvider.getSessionById(sid.getValue()).getPerson().getLogin().equals(request.getParameter(PARAM_LOGIN))) {
                logger.info("No session found. Auth by login/password ip={}", HttpUtils.getRemoteAddr(request, IP_HEADER));
                return authWithJwtToken(request, response);
            } else {
                logger.info("Updating session for user with ip={}", HttpUtils.getRemoteAddr(request, IP_HEADER));
                response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, sid.getValue(), authDomain, sessionTtl));
            }
            sendRedirect(request, response);
            return sessionProvider.getSessionById(sid.getValue());
        } catch (UnauthorizedException e){
            throw e;
        } catch (Exception e){
            logger.error("Can't authenticate user", e);
            throw new UnauthorizedException(e);
        }
    }

    private Session authByToken(HttpServletRequest request, HttpServletResponse response) {
        throw new UnauthorizedException("Authorisation by token is not supporter by cognito auth provider");
    }


    public Session authWithJwtToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationCode = request.getParameter("code");

        CognitoTokensResponse tokenResponse = getOauthClient().getOauthTokens(GRANT_TYPE, cognitoClientId, authorizationCode,
                cognitoRedirectUrl).execute().body();
        if (tokenResponse == null){
            throw new UnauthorizedException("Unable to retrieve tokens by authorization code");
        }

        String idToken = tokenResponse.getId_token();
        String accessToken = tokenResponse.getAccess_token();

        if (isEmpty(idToken) || isEmpty(accessToken)){
            try {
                response.sendRedirect(getLoginUrl(request));
            } catch (IOException e) {
                logger.warn("Unable to send redirect to Cognito login page", e);
                return null;
            }
        }

        GetUserRequest getUserRequest = GetUserRequest.builder().accessToken(accessToken).build();
        GetUserResponse userResponse = cognitoIdentityProviderClient.getUser(getUserRequest);
        if (userResponse == null){
            try {
                response.sendRedirect(getLoginUrl(request));
            } catch (IOException e) {
                logger.warn("Unable to send redirect to Cognito login page", e);
                return null;
            }
        }

        DecodedJWT jwt = JWT.decode(idToken);
        jwt.getClaims();

        Session session = (Session) new Session()
                .withId(jwt.getClaims().get("jti").asString())
                .withName(getName(jwt))
                .withLogin(jwt.getClaims().get("email").asString())
                .withTimeout(jwt.getClaims().get("exp").asLong())
                .withPerson(
                        new Person()
                                .withLogin(jwt.getClaims().get("email").asString())
                                .withFirstName(jwt.getClaims().get("given_name").asString())
                                .withLastName(jwt.getClaims().get("family_name").asString())
                );
        sessionProvider.addSession(session);
        response.addCookie(HttpUtils.createCookie(HttpUtils.SESSION_ID, session.getId(), authDomain, sessionTtl));
        return session;
    }

    private String getName(DecodedJWT jwt){
        String name = emptyIfNull(jwt.getClaims().get("given_name").asString()) + " " + emptyIfNull(jwt.getClaims().get("family_name").asString());
        return name.trim();
    }

    private String getAccessToken(String authorizationCode) {
        return null;
    }

    @Override
    public RedirectResponse redirectNotAuthTo(HttpServletRequest request) {
        return new RedirectResponse(getLoginUrl(request), "retpath", true);
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        return getCognitoUser(request) != null;
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Cognito doesn't allow one-time tokens authentication");
    }

    @Override
    public Set<String> suggestGroups(HttpServletRequest request, String literal) {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAllUsers(HttpServletRequest request) {
        return Collections.emptySet();
    }

    @Override
    public Set<String> suggestUser(HttpServletRequest request, String literal) {
        return Collections.emptySet();
    }

    private Object getCognitoUser(HttpServletRequest request) {
        return false;
    }

    private String getLoginUrl(HttpServletRequest request) {
        return cognitoLoginUrl;
    }

    private CognitoOauthClient getOauthClient(){
        return  HttpClientBuilder.builder(cognitoOauthEndpoint, OAUTH_API_TIMEOUT).build().
                create(CognitoOauthClient.class);
    }
}
