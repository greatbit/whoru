package ru.greatbit.whoru.auth.providers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import ru.greatbit.whoru.auth.Person;
import ru.greatbit.whoru.auth.RedirectResponse;
import ru.greatbit.whoru.auth.Session;
import ru.greatbit.whoru.auth.error.UnauthorizedException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.greatbit.utils.string.StringUtils.emptyIfNull;


public class CognitoAuthProvider extends BaseAuthProvider {

    @Value("${cognito.login.url}")
    private String cognitoLoginUrl;

    @Value("${aws.cognito.access.key}")
    private String awsCognitoAccessKey;

    @Value("${aws.cognito.secret.key}")
    private String awsCognitoSecretKey;

    @Value("${aws.cognito.region}")
    private String awsCognitoRegion;

//    private AmazonCognitoIdentity cognitoIdentityClient;

    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @PostConstruct
    private void postConstruct(){
//        BasicAWSCredentials credentials = new BasicAWSCredentials(awsCognitoAccessKey, awsCognitoSecretKey);
//        cognitoIdentityClient = AmazonCognitoIdentityClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(awsCognitoRegion)
//                .build();


        AwsCredentials awsCredentials = AwsBasicCredentials.create(awsCognitoAccessKey, awsCognitoSecretKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsCognitoRegion))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Override
    public Session authImpl(HttpServletRequest request, HttpServletResponse response) {
//        String authorizationCode = request.getParameter("code");
//        String accessToken = getAccessToken(authorizationCode);

//        DescribeIdentityRequest describeIdentityRequest = new DescribeIdentityRequest();
//        describeIdentityRequest.setIdentityId();
//        cognitoIdentityClient.describeIdentity()



        String idToken = request.getParameter("id_token");
        String accessToken = request.getParameter("access_token");


        if (isEmpty(idToken) || isEmpty(accessToken)){
            try {
                response.sendRedirect(getLoginUrl(request));
            } catch (IOException e) {
                logger.warn("Unable to send redirect to Cognito login page", e);
                return null;
            }
        }

        GetUserRequest getUserRequest = GetUserRequest.builder().accessToken(idToken).build();
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


        return (Session) new Session()
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
        return new RedirectResponse(getLoginUrl(request), "retpath");
    }

    @Override
    public boolean isAuthenticated(HttpServletRequest request) throws UnauthorizedException {
        return getCognitoUser(request) != null;
    }

    @Override
    public Session getSession(HttpServletRequest request) throws UnauthorizedException {
        // ToDo: Get session id cookie then
        // ToDo - get session from session provider or cognito
        return null;
    }

    @Override
    public void doAuthByOnetimeToken(String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Cognito doesn't allow one-time tokens authentication");
    }

    @Override
    public Set<String> suggestGroups(HttpServletRequest request, String literal) {
        // ToDO: implement
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAllUsers(HttpServletRequest request) {
        // ToDO: implement
        return Collections.emptySet();
    }

    @Override
    public Set<String> suggestUser(HttpServletRequest request, String literal) {
        // ToDO: implement
        return Collections.emptySet();
    }


    private Object getCognitoUser(HttpServletRequest request) {
        return false;
    }

    private String getLoginUrl(HttpServletRequest request) {
        return cognitoLoginUrl;
    }

}
