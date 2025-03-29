package com.agenda.agenda;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/contactos")
@CrossOrigin("*")
public class ContactoController {

    private List<Contacto> contactos = new CopyOnWriteArrayList<>();

    @GetMapping
    public ResponseEntity<List<Contacto>> getContactos() {
        return ResponseEntity.ok(contactos);
    }

    @PostMapping
    public ResponseEntity<Contacto> addContacto(@RequestBody Contacto contacto) {
        if (contacto.getNombre() == null || contacto.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Devuelve null en lugar de un mensaje de texto
        }

        for (Contacto c : contactos) {
            if (c.getNombre().equalsIgnoreCase(contacto.getNombre())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Devuelve null en lugar de un mensaje de texto
            }
        }

        contactos.add(contacto);
        return ResponseEntity.status(HttpStatus.CREATED).body(contacto); // Devuelve el contacto creado
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<String> deleteContacto(@PathVariable String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nombre inválido.");
        }

        boolean removed = contactos.removeIf(c -> c.getNombre().equalsIgnoreCase(nombre));

        if (removed) {
            return ResponseEntity.ok("Contacto eliminado: " + nombre);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/limpiar")
    public ResponseEntity<String> limpiarContactos() {
        contactos.clear();
        return ResponseEntity.ok("Todos los contactos han sido eliminados.");
    }

    // Método opcional para limpiar la lista entre pruebas
    public void limpiarListaContactos() {
        contactos.clear();
    }
}