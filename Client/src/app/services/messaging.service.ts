import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { AngularFireMessaging } from '@angular/fire/compat/messaging';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Toast } from '../models/toast';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private FCM_KEEP_URL: string = "/api/fcm/keep";

  currentMessage = new BehaviorSubject<any>(null)
  toastList: Toast[] = []

  constructor(
    private angularFireMessaging: AngularFireMessaging,
    private http: HttpClient,
    private authSvc: AuthService) { }

  requestPermission() {
    this.angularFireMessaging.requestToken.subscribe(
      (token) => {
        // console.debug(token)
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
        console.debug(payload)
        console.info("New message received", payload['notification'])
        this.currentMessage.next(payload['notification'])
        if (payload['notification'] != undefined) {
          this.showToast({
            title: payload['notification']['title'],
            body: payload['notification']['body']
          })
        }
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

  showToast(toast: Toast) {
    this.toastList.push(toast)
  }

  removeToast(toast: Toast) {
    this.toastList = this.toastList.filter(t => t != toast);
  }
}
