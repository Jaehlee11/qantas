package lee.jaehun.qantas.profile.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import lee.jaehun.qantas.profile.Application;
import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.Register;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiTest {

    @LocalServerPort
    private int port;

    private MockMvc mockMvc;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();


    @Test
    public void test() {
        String registerUrl = "/qantas/register";
        Register register = getRegister();

        HttpEntity<Register> request = new HttpEntity<>(register, new HttpHeaders());
        ResponseEntity<Profile> response = restTemplate.exchange(createURLWithPort(registerUrl), HttpMethod.POST, request, Profile.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        Profile profile = response.getBody();
        assertThat(profile, notNullValue());
        assertThat(profile.getFirstname(), is(register.getFirstname()));

//        String accessToken = obtainAccessToken(register.getUsername(), register.getPassword());
//        getProfile(profile.getId(), accessToken);
//        deleteProfile(profile.getId(), accessToken);
    }

    private void deleteProfile(Long id, String accessToken) {
        String profileUri = "/qantas/profile/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", String.format("Bearer %s", accessToken));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(profileUri), HttpMethod.DELETE, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    private void getProfile(Long id, String accessToken) {
        String profileUri = "/qantas/profile/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", String.format("Bearer %s", accessToken));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Profile> response = restTemplate.exchange(createURLWithPort(profileUri), HttpMethod.GET, request, Profile.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        Profile profile = response.getBody();
        assertThat(profile, notNullValue());
    }

    private String obtainAccessToken(String username, String password) {

        String oauthTokenUri = "/oauth/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("username", username);
        form.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", getHttpBasicRequest("qantas","mySecret"));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.exchange(createURLWithPort(oauthTokenUri), HttpMethod.POST, request, Map.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), notNullValue());
        String token = (String)response.getBody().get("access_token");
        assertThat(token, notNullValue());
        return token;
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

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
