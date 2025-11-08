import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriaListaComponent } from './Component/categoria-lista/categoria-lista.component';
import { CategoriaFormComponent } from './Component/categoria-form/categoria-form.component';
import { CiudadListaComponent } from './Component/ciudad-lista/ciudad-lista.component';
import { CiudadFormComponent } from './Component/ciudad-form/ciudad-form.component';


const routes: Routes = [
  {path:'Categorias', component: CategoriaListaComponent},
  {path: 'Categoria', component: CategoriaFormComponent},
  {path: 'Categoria/:id', component: CategoriaFormComponent},
  {path: 'Ciudades', component: CiudadListaComponent},
  {path: 'Ciudad', component: CiudadFormComponent},
  {path: 'Ciudad/:id', component: CiudadFormComponent},
  {path:'**', redirectTo:'Categorias', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
