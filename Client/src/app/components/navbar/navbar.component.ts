import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { MessagingService } from 'src/app/services/messaging.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {

  isCollapsed: boolean = true
  isLoggedIn: boolean = false
  givenname!: string
  profilePic!: string

  constructor(
    private router: Router,
    private authSvc: AuthService) { }

  ngOnInit(): void {
    this.router.events.subscribe((event) => {
      // console.log(event)
      // close the nav on url change
      this.isCollapsed = true
      this.isLoggedIn = this.authSvc.isLoggedIn
      this.givenname = this.authSvc.givenname
      this.profilePic = this.authSvc.profilePic
    })
  }

  logout() {
    this.authSvc.logout()
  }

}
