package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Repositorio.CiudadRepository;
import com.cristhian.SistemaInventario.Service.ICiudadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CiudadServiceImpl implements ICiudadService {

    @Autowired
    private CiudadRepository ciudadRepository;

    @Override
    public List<Ciudad> listarCiudadesActivas(){
        return  ciudadRepository.findByActivoTrue();
    }

    @Override
    public Optional<Ciudad> buscarCiudadId(int id){
        return ciudadRepository.findById(id);
    }

    @Override
    public Ciudad guardarCiudad(Ciudad ciudad){
        return ciudadRepository.save(ciudad);
    }

    @Override
    public void eliminarCiudad(int id){
        Ciudad ciudad = ciudadRepository.findById(id).orElse(null);
        if (ciudad != null) {
            ciudad.setActivo(false); // solo la marcamos como inactiva
            ciudadRepository.save(ciudad);
        }
    }

    @Override
    public boolean existByCiudad(String ciudad){
        boolean existe = ciudadRepository.existsByCiudadIgnoreCase(ciudad);
        System.out.println("🔎 Buscando ciudad: " + ciudad + " → existe=" + existe);
        return existe;
    }

    @Override
    public Optional<Ciudad> findByCiudadIgnoreCase(String ciudad){
        return ciudadRepository.findByCiudadIgnoreCase(ciudad);
    }
}
