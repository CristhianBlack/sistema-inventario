import { Observable } from "rxjs";
import { Categoria } from "../Modelos/categoria";

export interface ICategoriaService {

    // Obtener todas las categorías
    obtenerListaCategoria(): Observable<Categoria[]>;
    
    // Obtener categoría por ID
    obtenerCategoriaPorId(id: number): Observable<Categoria>;
    
    // Agregar nueva categoría
    agregarCategoria(categoria: Categoria): Observable<any>;
    
    // Editar categoría existente
    editarCategoria(id: number, categoria: Categoria): Observable<any>;
    
    // Eliminar categoría
    eliminarCategoria(id: number): Observable<any>;
}
