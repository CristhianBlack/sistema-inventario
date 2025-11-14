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
  {path:'**', redirectTo:'Categorias', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
