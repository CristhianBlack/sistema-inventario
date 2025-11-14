package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Repositorio.RolPersonaRepository;
import com.cristhian.SistemaInventario.Service.IRolPersonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolPersonaImpl implements IRolPersonaService {

    private final RolPersonaRepository rolPersonaRepository;

    public RolPersonaImpl(RolPersonaRepository rolPersonaRepository) {
        this.rolPersonaRepository = rolPersonaRepository;
    }

    @Override
    public List<RolPersona> listarRolPersonaActivo() {
        return rolPersonaRepository.findByActivoTrue();
    }

    @Override
    public Optional<RolPersona> buscarRolPersonaId(int id) {
        return rolPersonaRepository.findById(id);
    }

    @Override
    public RolPersona guardarRolPersona(RolPersona rolPersona) {
        return rolPersonaRepository.save(rolPersona);
    }

    @Override
    public void eliminarRolPersona(int id) {
        RolPersona rolPersona = rolPersonaRepository.findById(id).orElse(null);
        if (rolPersona != null){
            rolPersona.setActivo(false);
            rolPersonaRepository.save(rolPersona);
        }
    }

    @Override
    public Optional<RolPersona> findByNombreRolIgnoreCase(String nombreRol){
        return rolPersonaRepository.findByNombreRolIgnoreCase(nombreRol);
    }
}
