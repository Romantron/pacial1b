package com.agenda.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureWebTestClient
public class ContactoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContactoController contactoController;

    @BeforeEach
    void setUp() {
        contactoController.limpiarListaContactos(); // Limpiar la lista antes de cada prueba
    }

    @Test
    public void testAgregarContacto() {
        Map<String, Object> contacto = new HashMap<>();
        contacto.put("nombre", "Juan Pérez");
        contacto.put("telefono", "3001234567");
        contacto.put("email", "juan.perez@email.com");

        webTestClient.post().uri("/api/contactos")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(contacto).exchange().expectStatus().isCreated()
                .expectBody().jsonPath("$.nombre").isEqualTo("Juan Pérez").jsonPath("$.telefono")
                .isEqualTo("3001234567").jsonPath("$.email").isEqualTo("juan.perez@email.com");
    }

    
    @Test
    public void testAgregarContactoDuplicado() {
        Map<String, Object> contacto = new HashMap<>();
        contacto.put("nombre", "Juan Pérez");
        contacto.put("telefono", "3001234567");
        contacto.put("email", "juan.perez@email.com");

        webTestClient.post().uri("/api/contactos")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(contacto).exchange().expectStatus().isCreated();

        webTestClient.post().uri("/api/contactos")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(contacto).exchange().expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody().isEmpty(); // Cambiado para verificar que el cuerpo esté vacío (null)
    }

    @Test
    public void testObtenerContactos() {
        Map<String, Object> contacto1 = new HashMap<>();
        contacto1.put("nombre", "Juan Pérez");
        contacto1.put("telefono", "3001234567");
        contacto1.put("email", "juan.perez@email.com");

        Map<String, Object> contacto2 = new HashMap<>();
        contacto2.put("nombre", "María Gómez");
        contacto2.put("telefono", "3019876543");
        contacto2.put("email", "maria.gomez@email.com");

        webTestClient.post().uri("/api/contactos")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(contacto1).exchange().expectStatus().isCreated();

        webTestClient.post().uri("/api/contactos")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(contacto2).exchange().expectStatus().isCreated();

        webTestClient.get().uri("/api/contactos")
                .exchange().expectStatus().isOk().expectBody().jsonPath("$[0].nombre").isEqualTo("Juan Pérez")
                .jsonPath("$[1].nombre").isEqualTo("María Gómez");
    }

    @Test
    public void testEliminarContacto() {
        Map<String, Object> contacto = new HashMap<>();
        contacto.put("nombre", "Juan Pérez");
        contacto.put("telefono", "3001234567");
        contacto.put("email", "juan.perez@email.com");

        webTestClient.post().uri("/api/contactos").contentType(MediaType.APPLICATION_JSON).bodyValue(contacto)
                .exchange().expectStatus().isCreated();

        webTestClient.delete().uri("/api/contactos/Juan Pérez")
                .exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Contacto eliminado: Juan Pérez");

        webTestClient.get().uri("/api/contactos").exchange().expectStatus().isOk().expectBody()
                .jsonPath("$[?(@.nombre == 'Juan Pérez')]").doesNotExist();
    }

    @Test
    public void testLimpiarContactos() {
        webTestClient.delete().uri("/api/contactos/limpiar").exchange().expectStatus().isOk().expectBody(String.class)
                .isEqualTo("Todos los contactos han sido eliminados.");

        webTestClient.get().uri("/api/contactos").exchange().expectStatus().isOk().expectBody().jsonPath("$").isEmpty();
    }
}