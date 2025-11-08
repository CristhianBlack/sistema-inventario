import { Observable } from "rxjs";
import { Ciudad } from "../Modelos/ciudad";

export interface ICiudadService {

    //Obtiene todas las ciudades.
       obtenerListaciudades(): Observable<Ciudad[]>
    
      //Obtenenos la ciudad por el id
       obetnerCiudadesPorId(id : number): Observable<Ciudad>;
    
      // Agregamos nueva ciudad 
       agregarCiudad(Ciudad : Ciudad): Observable<any>;
    
      //Editar ciudad existente
       editarCiudad(id : number, ciudad : Ciudad): Observable<any>;
    
      //Eliminar Ciudad
       eliminarciudad(id : number): Observable<any>;
    
}
