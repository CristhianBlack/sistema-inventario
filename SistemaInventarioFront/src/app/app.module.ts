import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';


import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms'

//librerias externas
import  {  BrowserAnimationsModule  }  from  '@angular/platform-browser/animations' ;
import  {  ToastrModule  }  from  'ngx-toastr';
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
import { RolPersonaFormComponent } from './Component/rol-persona-form/rol-persona-form.component';




@NgModule({
  declarations: [
    AppComponent,
    CategoriaListaComponent,
    CategoriaFormComponent,
    CiudadListaComponent,
    CiudadFormComponent,
    TipoDocumentoListaComponent,
    TipoDocumentoFormComponent,
    TipoPersonaListaComponent,
    TipoPersonaFormComponent,
    PersonaListaComponent,
    PersonaFormComponent,
    RolPersonaListaComponent,
    RolPersonaFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule , //módulo de animaciones requerido 
    ToastrModule.forRoot ( ),
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
