import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriaListaComponent } from './Component/categoria-lista/categoria-lista.component';
import { CategoriaFormComponent } from './Component/categoria-form/categoria-form.component';
import { CiudadListaComponent } from './Component/ciudad-lista/ciudad-lista.component';
import { CiudadFormComponent } from './Component/ciudad-form/ciudad-form.component';
import { TipoDocumentoListaComponent } from './Component/tipo-documento-lista/tipo-documento-lista.component';
import { TipoDocumentoFormComponent } from './Component/tipo-documento-form/tipo-documento-form.component';
import { TipoPersonaListaComponent } from './Component/tipo-persona-lista/tipo-persona-lista.component';
import { TipoPersonaFormComponent } from './Component/tipo-persona-form/tipo-persona-form.component';
import { PersonaListaComponent } from './Component/persona-lista/persona-lista.component';
import { PersonaFormComponent } from './Component/persona-form/persona-form.component';
import { RolPersonaListaComponent } from './Component/rol-persona-lista/rol-persona-lista.component';
import { UnidadMedidaListaComponent } from './Component/unidad-medida-lista/unidad-medida-lista.component';
import { UnidadMedidaFormComponent } from './Component/unidad-medida-form/unidad-medida-form.component';
import { ProveedorListaComponent } from './Component/proveedor-lista/proveedor-lista.component';
import { ProveedorFormComponent } from './Component/proveedor-form/proveedor-form.component';
import { ProductoListaComponent } from './Component/producto-lista/producto-lista.component';
import { ProductoFormComponent } from './Component/producto-form/producto-form.component';
import { CompraListComponent } from './Component/compra-list/compra-list.component';
import { CompraFormComponent } from './Component/compra-form/compra-form.component';
import { KardexListaComponent } from './Component/kardex-lista/kardex-lista.component';
import { MovimientoInventarioListComponent } from './Component/movimiento-inventario-list/movimiento-inventario-list.component';
import { FormaPagoListaComponent } from './Component/forma-pago-lista/forma-pago-lista.component';
import { FormaPagoFormComponent } from './Component/forma-pago-form/forma-pago-form.component';
import { ImpuestoFormComponent } from './Component/impuesto-form/impuesto-form.component';
import { ImpuestoListaComponent } from './Component/impuesto-lista/impuesto-lista.component';
import { VentaListaComponent } from './Component/venta-lista/venta-lista.component';
import { VentaFormComponent } from './Component/venta-form/venta-form.component';
import { VentaPagoFormComponent } from './Component/venta-pago-form/venta-pago-form.component';
import { CompraPagoComponent } from './Component/compra-pago/compra-pago.component';


const routes: Routes = [
  {path:'Categorias', component: CategoriaListaComponent},
  {path: 'Categoria', component: CategoriaFormComponent},
  {path: 'Categoria/:id', component: CategoriaFormComponent},
  {path: 'Ciudades', component: CiudadListaComponent},
  {path: 'Ciudad', component: CiudadFormComponent},
  {path: 'Ciudad/:id', component: CiudadFormComponent},
  {path: 'TipoDocumentos', component: TipoDocumentoListaComponent},
  {path: 'TipoDocumento', component:TipoDocumentoFormComponent},
  {path: 'TipoDocumento/:id', component:TipoDocumentoFormComponent},
  {path: 'TipoPersonas', component: TipoPersonaListaComponent},
  {path: 'TipoPersona', component:TipoPersonaFormComponent},
  {path: 'TipoPersona/:id', component:TipoPersonaFormComponent},
  {path: 'Personas', component: PersonaListaComponent},
  {path: 'Persona', component: PersonaFormComponent},
  {path: 'Persona/:id', component: PersonaFormComponent},
  {path: 'Roles', component: RolPersonaListaComponent},
  {path: 'Unidades', component: UnidadMedidaListaComponent},
  {path: 'Unidad', component: UnidadMedidaFormComponent},
  {path: 'Unidad/:id', component: UnidadMedidaFormComponent},
  {path: 'Proveedores', component: ProveedorListaComponent},
  {path: 'Proveedor', component: ProveedorFormComponent},
  {path: 'Proveedor/:id', component: ProveedorFormComponent},
  {path: 'Productos', component: ProductoListaComponent},
  {path: 'Producto', component: ProductoFormComponent},
  {path: 'Producto/:id', component: ProductoFormComponent},
  {path: 'Compras', component: CompraListComponent},
  {path: 'Compra/:id', component: CompraFormComponent},
  {path: 'Compra/:id', component: CompraFormComponent},
  {path: 'Kardex/:id', component: KardexListaComponent},
  {path: 'MovimientoInventario', component: MovimientoInventarioListComponent},
  {path: 'FormaPago', component: FormaPagoListaComponent},
  {path: 'FormaPago/:id', component: FormaPagoFormComponent},
  {path: 'Impuesto', component: ImpuestoListaComponent},
  {path: 'Impuesto/:id', component: ImpuestoFormComponent},
  {path: 'Ventas', component: VentaListaComponent},
  {path: 'Impuesta/:id', component: VentaFormComponent},
  {path: 'Ventapago/:id', component: VentaPagoFormComponent},
  {path: 'CompraPago/:id', component: CompraPagoComponent},
  {path:'**', redirectTo:'Categorias', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
