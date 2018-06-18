package lee.jaehun.qantas.profile.test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import lee.jaehun.qantas.profile.Application;
import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.Register;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiTest.class);

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testRegister() {
        Register register = getRegister();
        Profile profile = createProfileFirst(register);

        createProfileSecond(register);

        deleteProfile(profile.getId(), obtainAccessToken(register));
    }

    @Test
    public void testGetProfile() {
        Register register = getRegister();
        Profile profile = createProfileFirst(register);
        String accessToken = obtainAccessToken(register);

        testGetProfile(profile.getId(), accessToken);

        testGetProfileWrongID(profile.getId() + 10, accessToken);

        deleteProfile(profile.getId(), accessToken);
    }

    @Test
    public void testUpdateProfile() {
        Register register = getRegister();
        Profile profile = createProfileFirst(register);
        Profile profile2 = createProfileFirst(getRegister2());
        String accessToken = obtainAccessToken(register);

        try {
            ResponseEntity<Profile> response = updateProfile(profile, accessToken);
            assertThat(response.getStatusCode(), is(HttpStatus.RESET_CONTENT));
            LOG.info(String.format("Updating a profile is successful: %s %s", profile.getId(), profile.getFirstname()));
        } catch (Exception e) {

        }

        try {
            ResponseEntity<Profile>  response = updateProfile(profile2, accessToken);
        } catch (Exception e) {
            assertThat(e.getClass().getName(), is(ResourceAccessException.class.getName()));
            LOG.info(String.format("Updating a profile is failed: %s %s", profile.getId(), profile.getFirstname()));
        }

        deleteProfile(profile.getId(), accessToken);

        try {
            ResponseEntity<Profile>  response = updateProfile(profile, accessToken);
        } catch (Exception e) {
            assertThat(e.getClass().getName(), is(ResourceAccessException.class.getName()));
            LOG.info(String.format("Updating a profile is failed: %s %s", profile.getId(), profile.getFirstname()));
        }

        deleteProfile(profile2.getId(), obtainAccessToken(getRegister2()));
    }

    @Test
    public void testDeleteProfile() {
        Register register = getRegister();
        Profile profile = createProfileFirst(register);
        Profile profile2 = createProfileFirst(getRegister2());
        String accessToken = obtainAccessToken(register);

        ResponseEntity<Void> response = deleteProfile(profile2.getId(), accessToken);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        LOG.info(String.format("Deleting a profile is failed: %s", profile.getId()));

        response = deleteProfile(profile.getId(), accessToken);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        LOG.info(String.format("Deleting a profile is successful: %s", profile.getId()));

        response = deleteProfile(profile.getId(), accessToken);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        LOG.info(String.format("Deleting a profile is failed: %s", profile.getId()));

        deleteProfile(profile2.getId(), obtainAccessToken(getRegister2()));
    }

    private ResponseEntity<Profile> updateProfile(Profile profile, String accessToken) throws Exception {
        String profileUri = "/qantas/profile/";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", String.format("Bearer %s", accessToken));
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Profile> request = new HttpEntity<>(profile, headers);
        return restTemplate.exchange(createURLWithPort(profileUri), HttpMethod.PUT, request, Profile.class);
    }

    private void testGetProfile(Long id, String accessToken) {
        ResponseEntity<Profile> response = getProfile(id, accessToken);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        Profile getProfile = response.getBody();
        assertThat(getProfile, notNullValue());
        LOG.info(String.format("Geting a profile is successful: %s %s", id, getProfile.getFirstname()));
    }

    private void testGetProfileWrongID(Long id, String accessToken) {
        ResponseEntity<Profile> response = getProfile(id, accessToken);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        Profile getProfile = response.getBody();
        assertThat(getProfile, nullValue());
        LOG.info(String.format("Geting a profile is failed: %s", id));
    }

    private ResponseEntity<Profile> createProfile(Register register) {
        String registerUrl = "/qantas/register";
        HttpEntity<Register> request = new HttpEntity<>(register, new HttpHeaders());
        return restTemplate.exchange(createURLWithPort(registerUrl), HttpMethod.POST, request, Profile.class);
    }

    private Profile createProfileFirst(Register register) {
        ResponseEntity<Profile> response = createProfile(register);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        Profile profile = response.getBody();
        assertThat(profile, notNullValue());
        assertThat(profile.getFirstname(), is(register.getFirstname()));
        assertThat(profile.getLastname(), is(register.getLastname()));
        LOG.info(String.format("Creating a user is successful: %s", register.getUsername()));

        return profile;
    }

    private void createProfileSecond(Register register) {
        ResponseEntity<Profile> response = createProfile(register);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        Profile profile = response.getBody();
        assertThat(profile, nullValue());
        LOG.info(String.format("Creating a user is failed: %s", register.getUsername()));
    }

    private ResponseEntity<Void> deleteProfile(Long id, String accessToken) {
        String profileUri = "/qantas/profile/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", String.format("Bearer %s", accessToken));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(createURLWithPort(profileUri), HttpMethod.DELETE, request, Void.class);
    }

    private ResponseEntity<Profile> getProfile(Long id, String accessToken) {
        String profileUri = "/qantas/profile/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", String.format("Bearer %s", accessToken));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(createURLWithPort(profileUri), HttpMethod.GET, request, Profile.class);
    }

    private String obtainAccessToken(Register register) {
        String oauthTokenUri = "/qantas/oauth/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("username", register.getUsername());
        form.add("password", register.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getHttpBasicRequest("qantas","mySecret"));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.exchange(createURLWithPort(oauthTokenUri), HttpMethod.POST, request, Map.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), notNullValue());
        String accessToken = (String)response.getBody().get("access_token");
        assertThat(accessToken, notNullValue());
        LOG.info("Created Access Token:" + accessToken);
        return accessToken;
    }

    private String getHttpBasicRequest(String username, String password){
        String headerValue;
        byte[] toEncode;
        try {
            toEncode = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException var5) {
            throw new RuntimeException(var5);
        }
        return "Basic " + new String(Base64.encode(toEncode));
    }

    private Register getRegister() {
        Register register = new Register();
        register.setUsername("jaehlee11@gmail.com");
        register.setPassword("password");
        register.setFirstname("Jaehun");
        register.setLastname("Lee");
        register.setAddress(new ArrayList<>());
        return register;
    }

    private Register getRegister2() {
        Register register = new Register();
        register.setUsername("jerry060@gmail.com");
        register.setPassword("password");
        register.setFirstname("Jerry");
        register.setLastname("Lee");
        register.setAddress(new ArrayList<>());
        return register;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
