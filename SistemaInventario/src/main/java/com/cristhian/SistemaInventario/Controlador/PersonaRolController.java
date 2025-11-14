package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Service.IPersonaRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaRolController {

    @Autowired
    private IPersonaRolService personaRolService;

    // 🔹 Obtener roles de una persona
    @GetMapping("/Personas/PersonaRol/{idPersona}")
    public ResponseEntity<List<RolPersona>> obtenerRolesPorPersona(@PathVariable Integer idPersona) {
        List<RolPersona> roles = personaRolService.obtenerRolesPorPersona(idPersona);
        return ResponseEntity.ok(roles);
    }

    // 🔹 Asignar rol a una persona
    @PostMapping("/asignar/{idPersona}/{idRol}")
    public ResponseEntity<PersonaRol> asignarRol(@PathVariable Integer idPersona, @PathVariable Integer idRol) {
        PersonaRol personaRol = personaRolService.asignarRol(idPersona, idRol);
        return ResponseEntity.ok(personaRol);
    }

    // 🔹 Eliminar un rol asignado
    @DeleteMapping("/{idPersonaRol}")
    public ResponseEntity<Void> eliminarRolPersona(@PathVariable Integer idPersonaRol) {
        personaRolService.eliminarRolPersona(idPersonaRol);
        return ResponseEntity.noContent().build();
    }
}

