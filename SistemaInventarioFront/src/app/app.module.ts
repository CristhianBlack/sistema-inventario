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


@NgModule({
  declarations: [
    AppComponent,
    CategoriaListaComponent,
    CategoriaFormComponent,
    CiudadListaComponent,
    CiudadFormComponent
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
