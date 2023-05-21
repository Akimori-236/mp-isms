import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
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
  // currentMsg$!: Subscription


  constructor(
    private router: Router,
    private authSvc: AuthService,
    private msgSvc: MessagingService) { }

  ngOnInit(): void {
    this.router.events.subscribe((event) => {
      // console.log(event)
      // close the nav on url change
      this.isCollapsed = true
      this.isLoggedIn = this.authSvc.isLoggedIn
      this.givenname = this.authSvc.givenname
    })
    // this.msgSvc.currentMessage.subscribe((message) => {
    //   console.log(message['notification'])
    //   // TODO: migrate to toast service

    // })
  }

  logout() {
    this.authSvc.logout()
  }



}
