import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { RegisterComponent } from './components/register/register.component';
import { BorrowedComponent } from './components/borrowed/borrowed.component';
import { MainComponent } from './components/main/main.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { AboutComponent } from './components/about/about.component';
import { StoresComponent } from './components/stores/stores.component';
import { ProfileComponent } from './components/profile/profile.component';
import { InstrumentsComponent } from './components/instruments/instruments.component';
import { TableInstrumentsComponent } from './components/instruments/table-instruments/table-instruments.component';
import { FormAddinstrumentComponent } from './components/instruments/form-addinstrument/form-addinstrument.component';
import { JwtHelperService, JwtModule } from '@auth0/angular-jwt';
import { PopupQrComponent } from './components/instruments/popup-qr/popup-qr.component';
import { BorrowComponent } from './components/borrow/borrow.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    BorrowedComponent,
    MainComponent,
    NavbarComponent,
    AboutComponent,
    StoresComponent,
    ProfileComponent,
    InstrumentsComponent,
    TableInstrumentsComponent,
    FormAddinstrumentComponent,
    PopupQrComponent,
    BorrowComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    HttpClientModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    JwtModule.forRoot({
      config: {
        tokenGetter: () => {
          return localStorage.getItem("jwt")
        }
      }
    })
  ],
  providers: [
    JwtHelperService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
