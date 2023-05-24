import { HttpErrorResponse } from '@angular/common/http';
import { Component, NgZone, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CredentialResponse } from 'google-one-tap';
import { timeout } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { MessagingService } from 'src/app/services/messaging.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup
  isLoggedIn: boolean = false

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authSvc: AuthService,
    private _ngZone: NgZone,
    private activatedRoute: ActivatedRoute,
    private fcm: MessagingService) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      givenname: this.fb.control<string>('', [Validators.required, Validators.pattern("^[a-zA-Z]{2,}$")]),
      familyname: this.fb.control<string>(''),
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8)]),
    })

    this.isLoggedIn = this.authSvc.isLoggedIn
    if (this.authSvc.isLoggedIn) {
      timeout(3000)
      this.router.navigate(['/'])
    }
    // @ts-ignore
    google.accounts.id.initialize({
      client_id: '869245493728-jcr4ussoue4u3eu7e020s37gvee8kp05.apps.googleusercontent.com',
      context: "signin",
      // can only have either ballback or login_uri NOT BOTH
      callback: this.handleCredentialResponse.bind(this),
      auto_select: false, // autoselects first google account of user to login
      cancel_on_tap_outside: true, // cancel if user clicks outside of popup
      // log_level: "debug"
    })
    // @ts-ignore
    google.accounts.id.renderButton(
      // @ts-ignore
      document.getElementById("googleBtn"),
      { theme: "filled_blue", text: "signup_with", shape: "rectangular" }
    )
    // @ts-ignore
    google.accounts.id.prompt((notification: PromptMomentNotification) => { })
  }

  handleCredentialResponse(response: CredentialResponse) {
    this.authSvc.googleRegister(response.credential)
      .then(response => {
        console.log(response)
        localStorage.setItem("jwt", response['jwt'])
        this._ngZone.run(() => {
          const origPath = this.activatedRoute.snapshot.queryParams['fullPath'];
          if (origPath) {
            const pathArray = origPath.split(',');
            this.router.navigate(pathArray);
          } else {
            this.router.navigate(['/']);
          }
        })
      })
      .catch(error => {
        if (error.status === 409) {
          window.alert("This email is already registered. Please log in instead.")
          this._ngZone.run(() => {
            this.router.navigate(['/login'])
          })
        }
      })
  }

  register() {
    const givenname = this.registerForm.value['givenname']
    const familyname = this.registerForm.value['familyname']
    const email = this.registerForm.value['email']
    const password = this.registerForm.value['password']
    this.authSvc.register(givenname, familyname, email, password)
      .then(response => {
        console.log(response)
        localStorage.setItem("jwt", response['jwt'])
        this.fcm.sendFCMToken()
        const origPath = this.activatedRoute.snapshot.queryParams['fullPath'];
        if (origPath) {
          const pathArray = origPath.split(',');
          this.router.navigate(pathArray);
        } else {
          this.router.navigate(['/']);
        }
      })
      .catch((error: HttpErrorResponse) => {
        console.error(error)
        if (error.status === 409) {
          window.alert("This email is already registered. Please log in instead.")
          this._ngZone.run(() => {
            this.router.navigate(['/login'])
          })
        } else {
          console.warn(error)
          window.alert(error['error'])
        }
      })
  }
  // TODO: grey out or show loading circle when loading
}
