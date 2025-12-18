package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.FormaPagoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.FormaPago;
import com.cristhian.SistemaInventario.Service.IFormaPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class FormaPagoController {

    private final IFormaPagoService formaPagoService;

    public FormaPagoController(IFormaPagoService formaPagoService) {
        this.formaPagoService = formaPagoService;
    }

    @GetMapping("/FormaPago")
    public ResponseEntity<List<FormaPagoDTO>> listarFormPagoActivo(){
        List<FormaPagoDTO> response = formaPagoService.listarFormaPagoActivo().stream()
                .map(FormaPagoDTO::new)// mapeo entidad → DTO
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/FormaPago/{id}")
    public ResponseEntity<?> obtenerFormaPagoPorId(@PathVariable int id){
        Optional<FormaPago> data = formaPagoService.buscarFormaPagoByID(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new FormaPagoDTO(data.get()));
    }

    @PutMapping("/FormaPago/{id}")
    public ResponseEntity<?> actualizarCiudad(@PathVariable int id, @RequestBody FormaPagoDTO formaPagoDTO){
        try{
            var actualizado = formaPagoService.actualizarFormaPago(id, formaPagoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new FormaPagoDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }

    }

    @DeleteMapping("/FormaPago/{id}")
    public ResponseEntity<?> eliminarCiudad(@PathVariable int id){
        try{
            formaPagoService.eliminarFormaPago(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Forma de pago eliminada con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

}
