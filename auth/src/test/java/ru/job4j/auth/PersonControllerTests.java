package ru.job4j.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTests {

    private Person person;
    private String json;
    private RestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    private  String baseUrl;

    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void initializePerson() throws JsonProcessingException {
        person = new Person();
        person.setId(1);
        person.setLogin("jay");
        person.setPassword("pass");

        json = objectMapper.writeValueAsString(person);

        baseUrl = "http://localhost:" + randomServerPort + "/person/";

        restTemplate = new RestTemplate();
    }

    @Test
    public void whenGetPersonThenReturnJsonObject() throws Exception {
        when(personRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(person));

        URI uri = new URI(baseUrl + "1");
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(json, result.getBody());

    }

    @Test
    public void whenGetAllPersonThenReturnJsonArray() throws Exception {
        var personList = List.of(person);

        when(personRepository.findAll()).thenReturn(personList);

        URI uri = new URI(baseUrl);
        ResponseEntity<List<Person>>  result = restTemplate.exchange(
                uri, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(person, result.getBody().get(0));
    }

    @Test
    public void whenSavePersonThenReturnTooJsonObject() throws Exception {
        given(personRepository.save(person)).willReturn(person);

        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Person> request = new HttpEntity<>(person, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(json, result.getBody());
    }

    @Test
    public void whenUpdatePersonThenReturnOk() throws Exception {
        given(personRepository.save(person)).willReturn(person);

        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Person> request = new HttpEntity<>(person, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void whenDeletePersonThenReturnTooOk() throws Exception {
        doNothing().when(personRepository).delete(person);

        URI uri = new URI(baseUrl + "1");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Person> request = new HttpEntity<>(person, headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
