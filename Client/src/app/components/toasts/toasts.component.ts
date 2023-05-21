import { Component } from '@angular/core';
import { MessagingService } from 'src/app/services/messaging.service';

@Component({
  selector: 'app-toasts',
  templateUrl: './toasts.component.html',
  styleUrls: ['./toasts.component.css']
})
export class ToastsComponent {
  constructor(public msgSvc: MessagingService) { }
}
