package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.cristhian.SistemaInventario.Repositorio.UnidadMedidaRepository;
import com.cristhian.SistemaInventario.Service.IUnidadMedidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnidadMedidaServiceImpl implements IUnidadMedidaService {

    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaServiceImpl.class);

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Override
    public List<UnidadMedida> listarUnidades(){
        return unidadMedidaRepository.findByActivoTrue();
    }

    @Override
    public Optional<UnidadMedida> buscarUnidadId(int id){
        return unidadMedidaRepository.findById(id);
    }

    @Override
    public UnidadMedida guardar(UnidadMedida unidadMedida){
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Override
    public void borrar(int id){
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(id).orElse(null);

        if (unidadMedida != null){
            unidadMedida.setActivo(false);
            unidadMedidaRepository.save(unidadMedida);
        }
    }

    @Override
    public boolean existsByNombreMedida(String nombreMedida){
        return unidadMedidaRepository.existsByNombreMedidaIgnoreCase(nombreMedida);
    }

    @Override
    public boolean existsOtherWithSameName(int id, String nombreMedida) {
        return unidadMedidaRepository.existsOtherWithSameName(id, nombreMedida);
    }

    @Override
    public boolean existeOtraUnidadConMismoNombre(int id, String nombreMedida) {
        String nombreNormalizado = nombreMedida.trim().toLowerCase();
        return unidadMedidaRepository.findAll().stream()
                .anyMatch(u -> u.getIdUnidadMedida() != id &&
                        u.getNombreMedida().trim().toLowerCase().equals(nombreNormalizado));
    }
}
