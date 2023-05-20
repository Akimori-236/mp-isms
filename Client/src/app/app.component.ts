import { Component, OnInit } from '@angular/core';
import { MessagingService } from './services/messaging.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Instrument Store Management System';
  message: any

  constructor(private messagingSvc: MessagingService) { }

  ngOnInit(): void {
    this.messagingSvc.requestPermission()
    this.messagingSvc.receiveMessaging()
    this.message = this.messagingSvc.currentMessage
  }

  

}
