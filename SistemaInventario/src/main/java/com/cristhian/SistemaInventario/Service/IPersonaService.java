package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    public List<Persona> listarPersonasActivas();
    public Optional<Persona> buscarPersonaID(int id);
    public Persona guardarPersona(Persona persona);
    public void eliminarPersona(int id);
    public boolean existsPersonaByDocumento(String documentoPersona);
    Optional<Persona> findByPersonaIgnoreCase(String nombre);



}
