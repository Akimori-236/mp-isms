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
import { TableStorelogsComponent } from './components/instruments/table-storelogs/table-storelogs.component';
import { AngularFireAuthModule } from '@angular/fire/compat/auth';
import { AngularFireMessagingModule } from '@angular/fire/compat/messaging';
import { AngularFireModule } from '@angular/fire/compat';
import { environment } from 'src/environments/environment';
import { MessagingService } from './services/messaging.service';
import { AsyncPipe } from '@angular/common';
import { ToastsComponent } from './components/toasts/toasts.component';
import { GoogleMapsModule } from '@angular/google-maps';
import { MapComponent } from './components/map/map.component';
import { StoreSettingsComponent } from './components/stores/store-settings/store-settings.component';

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
    BorrowComponent,
    TableStorelogsComponent,
    ToastsComponent,
    MapComponent,
    StoreSettingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    ReactiveFormsModule,
    HttpClientModule,
    AngularFireAuthModule,
    AngularFireMessagingModule,
    AngularFireModule.initializeApp(environment.firebase),
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
    }),
    GoogleMapsModule
  ],
  providers: [
    JwtHelperService,
    MessagingService,
    AsyncPipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
