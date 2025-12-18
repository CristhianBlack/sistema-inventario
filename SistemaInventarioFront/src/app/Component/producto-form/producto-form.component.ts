import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { Categoria } from 'src/app/Modelos/categoria';
import { Producto } from 'src/app/Modelos/producto';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { UnidadMedida } from 'src/app/Modelos/unidad-medida';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { ProductoService } from 'src/app/Servicios/producto.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';
import { UnidadMedidaService } from 'src/app/Servicios/unidad-medida.service';

@Component({
  selector: 'app-producto-form',
  templateUrl: './producto-form.component.html',
  styleUrls: ['./producto-form.component.css']
})
export class ProductoFormComponent {

  @Input() producto: Producto | null = null;
  @Output() formGuardado = new EventEmitter<void>();
  
    formModel: Producto = new Producto();
  
    categorias: Categoria[] = [];
    unidadMedida: UnidadMedida[] = [];
    proveedores: Proveedor[] = [];
  
  
    constructor(
      private productoService: ProductoService,
      private toastr: ToastrService,
      private categoriaService: CategoriaService,
      private unidadMedidaService: UnidadMedidaService,
      private proveedorService: ProveedorService
    ) {}
  
    ngOnInit(): void {
      this.cargarListas();
    }
  
   
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['producto'] && this.producto) {
      this.asignarPersonaAlFormulario(); // ✔ Solo asignar datos
    }
  
    if (changes['producto'] && !this.producto) {
      this.formModel = new Producto(); // ✔ para modo crear
    }
  }
  
  
  
    private cargarListas(): void {
      forkJoin({
        categorias: this.categoriaService.obtenerListaCategoria(),
        unidadMedida: this.unidadMedidaService.obtenerListaUnidadMedida(),
        proveedores: this.proveedorService.listarProveedorPersona(),
      }).subscribe({
        next: data => {
          this.categorias = data.categorias;
          this.unidadMedida = data.unidadMedida;
          this.proveedores = data.proveedores;
          
  
          if (this.producto) {
            this.asignarPersonaAlFormulario();
          }
        }
      });
    }
  
    private asignarPersonaAlFormulario(): void {
    if (!this.producto) return;
  
    this.formModel = {
      ...this.producto,
  
      idCategoria: this.producto.idCategoria ?? null,
      idUnidadMedida: this.producto.idUnidadMedida ?? null,
      idProveedor: this.producto.idProveedor ?? null,
    };
    console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
    console.log("LISTAS:", {
      categorias: this.categorias,
      unidadMedida: this.unidadMedida,
      proveedores: this.proveedores
    });
  }
  
  
    /*private asignarPersonaAlFormulario(): void {
      this.formModel = {
        ...this.persona!,
  
        idTipoDocumento: this.persona?.tipoDocumento?.idTipoDocumento ?? null,
        idCiudad: this.persona?.ciudad?.idCiudad ?? null,
        idTipoPersona: this.persona?.tipoPersona?.idTipoPersona ?? null,
  
        // roles
        idsRoles: this.persona?.roles?.map(r => Number(r.idRolPersona)) ?? []
      };
    }*/
  
    private mapPersonaToRequest(): any {
    return {
      idproducto: this.formModel.idProducto ?? null,
      codigoProducto: this.formModel.codigoProducto,
      nombreProducto: this.formModel.nombreProducto,
      precioCompra : this.formModel.precioCompra,
      precioVenta : this.formModel.precioVenta,
      descripcion : this.formModel.descripcion,

    // NOMBRES CORRECTOS que espera tu DTO
    idCategoria : Number(this.formModel.idCategoria),
    idUnidadMedida : Number(this.formModel.idUnidadMedida),
    idProveedor :  Number(this.formModel.idProveedor) 
      
    };
  }
  
  
  
    onSubmit(formProducto: NgForm): void {
      if (formProducto.invalid) {
        this.toastr.warning('Complete todos los campos requeridos');
        return;
      }
      const request = this.mapPersonaToRequest();
      console.log("JSON que envío:", JSON.stringify(request, null, 2));
  
      // EDITAR
      if (this.formModel.idProducto) {
        this.productoService.actualizarProducto(this.formModel.idProducto, request)
          .subscribe(() => {
            console.log("Estoy validando donde estoy entrando ");
            this.toastr.success('Producto actualizado');
            this.formGuardado.emit();
            this.limpiarFormulario();
          });
        return;
      }else{
        console.log("Entro al else de crear");
        // CREAR
        this.productoService.guardarProducto(request)
        .subscribe({
          next: () => {
            console.log("Entro al subscribe y al next ");
            this.toastr.success('Producto agregado correctamente', 'Éxito');
            this.formGuardado.emit();
            this.limpiarFormulario(formProducto);
            console.log("Enviando REAL al backend:", JSON.stringify(request, null, 2));
          console.log("JSON FINAL:", request);
          this.toastr.success('Persona creada');
          },
          error: (err) => {
            console.error("ERROR COMPLETO DEL SERVIDOR:", err);
            this.toastr.error(err.message || 'Error al crear persona', 'Error');
          }
      });
      }   
    }
  
    limpiarFormulario(formProducto?: NgForm): void {
      this.formModel = new Producto();
      formProducto?.resetForm();
        this.producto = null;
    this.formModel = new Producto();
  
    this.formModel.idCategoria = null;
    this.formModel.idUnidadMedida = null;
    this.formModel.idProveedor = null;
  
    }
  
    

}
