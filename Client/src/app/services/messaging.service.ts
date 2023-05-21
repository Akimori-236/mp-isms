import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private FCM_KEEP_URL: string = "/api/fcm/keep";
  currentMessage = new BehaviorSubject<any>(null)

  constructor(
    private angularFireMessaging: AngularFireMessaging,
    private http: HttpClient,
    private authSvc: AuthService) { }

  requestPermission() {
    this.angularFireMessaging.requestToken.subscribe(
      (token) => {
        console.log(token)
        if (token != null) {
          sessionStorage.setItem("fcmToken", token)
          this.sendFCMToken()
        }
      },
      (err) => {
        console.warn("Unable to get permission for push notification:", err)
      }
    )
  }

  receiveMessaging() {
    this.angularFireMessaging.messages.subscribe(
      (payload) => {
        console.log("New message received", payload)
        this.currentMessage.next(payload)
      }
    )
  }

  sendFCMToken() {
    let token = sessionStorage.getItem("fcmToken")
    if (this.authSvc.isLoggedIn) {
      const headers = this.authSvc.JWTHeaders;
      firstValueFrom(
        this.http.post(this.FCM_KEEP_URL, { body: token }, { headers })
      )
    }
  }
}
