import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {

  isCollapsed: boolean = true
  isLoggedIn: boolean = false
  givenname!: string

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authSvc: AuthService,) { }

  ngOnInit(): void {
    this.router.events.subscribe((event) => {
      // console.log(event)
      // close the nav on url change
      this.isCollapsed = true
      this.isLoggedIn = this.authSvc.isLoggedIn
      this.givenname = this.authSvc.givenname
    })
  }

  logout() {
    this.authSvc.logout()
  }

  search() { }
}
