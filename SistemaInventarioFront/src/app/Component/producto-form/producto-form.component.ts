import {
  Component,
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
} from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { Categoria } from 'src/app/Modelos/categoria';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { Producto } from 'src/app/Modelos/producto';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { UnidadMedida } from 'src/app/Modelos/unidad-medida';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';
import { ProductoService } from 'src/app/Servicios/producto.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';
import { UnidadMedidaService } from 'src/app/Servicios/unidad-medida.service';

@Component({
  selector: 'app-producto-form',
  templateUrl: './producto-form.component.html',
  styleUrls: ['./producto-form.component.css'],
})
export class ProductoFormComponent {
  /**
   * Producto recibido desde el componente padre.
   * Si viene con datos → modo edición
   * Si es null → modo creación
   */
  @Input() producto: Producto | null = null;
  /**
   * Evento que se emite cuando el formulario se guarda correctamente
   */
  @Output() formGuardado = new EventEmitter<void>();
  /**
   * Modelo principal del formulario (template-driven)
   */
  formModel: Producto = new Producto();

  /**
   * Listas para selects
   */
  categorias: Categoria[] = [];
  unidadMedida: UnidadMedida[] = [];
  proveedores: Proveedor[] = [];
  impuestos: Impuesto[] = [];

  /**
   * Controla si el producto tiene código automático o manual
   */
  tieneCodigoProducto: boolean = true;
  /**
   * Bandera que indica si el formulario está en modo edición
   */
  esEdicion = false;
  /**
   * Indica si el usuario logueado es administrador del sistema
   */
  esAdminSistema = false;

  constructor(
    private productoService: ProductoService,
    private toastr: ToastrService,
    private categoriaService: CategoriaService,
    private unidadMedidaService: UnidadMedidaService,
    private proveedorService: ProveedorService,
    private impuestoService: ImpuestoService,
  ) {}

  /**
   * Inicialización del componente
   * - Carga listas necesarias
   * - Obtiene el rol del usuario desde localStorage
   */
  ngOnInit(): void {
    this.cargarListas();
    const rol = localStorage.getItem('rol');
    this.esAdminSistema = rol === 'ADMIN_SISTEMA';
  }

  /**
   * Detecta cambios en el @Input producto
   * Permite distinguir entre crear y editar
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['producto'] && this.producto) {
      // Modo edición
      this.esEdicion = true;
      this.asignarPersonaAlFormulario(); // ✔ Solo asignar datos
      this.tieneCodigoProducto = this.formModel.codigoProducto === '';
    }

    if (changes['producto'] && !this.producto) {
      // Modo creación
      this.esEdicion = false;
      this.formModel = new Producto(); // ✔ para modo crear
    }
  }

  /**
   * Carga todas las listas necesarias para el formulario
   * Se ejecutan en paralelo con forkJoin
   */
  private cargarListas(): void {
    forkJoin({
      categorias: this.categoriaService.obtenerListaCategoria(),
      unidadMedida: this.unidadMedidaService.obtenerListaUnidadMedida(),
      proveedores: this.proveedorService.listarProveedorPersona(),
      impuestos: this.impuestoService.listarImpuestos(),
    }).subscribe({
      next: (data) => {
        this.categorias = data.categorias;
        this.unidadMedida = data.unidadMedida;
        this.proveedores = data.proveedores;
        this.impuestos = data.impuestos;

        // Si estamos editando, se asignan los valores al formulario
        if (this.producto) {
          this.asignarPersonaAlFormulario();
        }
      },
    });
  }

  /**
   * Asigna los valores del producto recibido
   * al modelo del formulario
   */
  private asignarPersonaAlFormulario(): void {
    if (!this.producto) return;

    this.formModel = {
      ...this.producto,

      idCategoria: this.producto.idCategoria ?? null,
      idUnidadMedida: this.producto.idUnidadMedida ?? null,
      idProveedor: this.producto.idProveedor ?? null,
      idImpuesto: this.producto.idImpuesto ?? null,
    };
    console.log('FORM MODEL AL ABRIR EDITAR:', this.formModel);
    console.log('LISTAS:', {
      categorias: this.categorias,
      unidadMedida: this.unidadMedida,
      proveedores: this.proveedores,
      impuestos: this.impuestos,
    });
  }

  /**
   * Mapea el modelo del formulario
   * al objeto que espera el backend (DTO)
   */
  private mapProductoToRequest(): any {
    return {
      idproducto: this.formModel.idProducto ?? null,
      codigoProducto: this.formModel.codigoProducto,
      nombreProducto: this.formModel.nombreProducto,
      stockMinimo: this.formModel.stockMinimo,
      precioVenta: this.formModel.precioVenta,
      descripcion: this.formModel.descripcion,

      // NOMBRES CORRECTOS que espera tu DTO
      idCategoria: Number(this.formModel.idCategoria),
      idUnidadMedida: Number(this.formModel.idUnidadMedida),
      idProveedor: Number(this.formModel.idProveedor),
      idImpuesto: Number(this.formModel.idImpuesto),
    };
  }
  /**
   * Determina si el campo stock mínimo debe estar deshabilitado
   * - Solo se deshabilita cuando se está editando
   * - y el usuario NO es administrador
   */
  get deshabilitarStockMinimo(): boolean {
    return this.esEdicion && !this.esAdminSistema;
  }

  /**
   * Maneja el envío del formulario
   * Valida, construye el request y decide
   * si crea o edita un producto
   */
  onSubmit(formProducto: NgForm): void {
    if (formProducto.invalid) {
      this.toastr.warning('Complete todos los campos requeridos');
      return;
    }
    const request = this.mapProductoToRequest();
    console.log('JSON que envío:', JSON.stringify(request, null, 2));

    // EDITAR
    if (this.formModel.idProducto) {
      this.productoService
        .actualizarProducto(this.formModel.idProducto, request)
        .subscribe(() => {
          console.log('Estoy validando donde estoy entrando ');
          this.toastr.success('Producto actualizado');
          this.formGuardado.emit();
          this.limpiarFormulario();
        });
      return;
    } else {
      console.log('Entro al else de crear');
      // CREAR
      this.productoService.guardarProducto(request).subscribe({
        next: () => {
          console.log('Entro al subscribe y al next ');
          this.toastr.success('Producto agregado correctamente', 'Éxito');
          this.formGuardado.emit();
          this.limpiarFormulario(formProducto);
          console.log(
            'Enviando REAL al backend:',
            JSON.stringify(request, null, 2),
          );
          console.log('JSON FINAL:', request);
        },
        error: (err) => {
          console.error('ERROR COMPLETO DEL SERVIDOR:', err);
          this.toastr.error(err.message || 'Error al crear persona', 'Error');
        },
      });
    }
  }

  /**
   * Limpia el formulario y reinicia el modelo
   */
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
