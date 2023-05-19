import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { User } from '../models/user';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private PROFILE_URL: string = "/api/profile/"

  constructor(private http: HttpClient, private authSvc: AuthService) { }

  getProfile(): Promise<User> {
    const headers = this.authSvc.JWTHeaders
    headers.set("Content-Type", "application/json")
    return firstValueFrom(
      this.http.get<User>(`${this.PROFILE_URL}`, { headers })
    )
  }

}
