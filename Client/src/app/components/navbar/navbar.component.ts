import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { MessagingService } from 'src/app/services/messaging.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit, OnDestroy {

  isCollapsed: boolean = true
  isLoggedIn: boolean = false
  givenname!: string
  profilePic!: string
  routerEvents$!: Subscription

  constructor(
    private router: Router,
    private authSvc: AuthService) { }
    
  ngOnDestroy(): void {
    this.routerEvents$.unsubscribe()
  }

  ngOnInit(): void {
    this.routerEvents$ = this.router.events.subscribe((event) => {
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
