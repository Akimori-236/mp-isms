import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom, map } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class QrService {
  private QR_URL: string = "/api/qr/";

  constructor(private http: HttpClient, private authSvc: AuthService) { }

  getLoanQR(instrument_id: string, storeID: string): Promise<any> {
    console.info("Getting QR for loaning out: " + instrument_id)
    const headers = this.authSvc.JWTHeaders
    headers.set("Content-type", "application/json")
    headers.set('Accept', 'image/png')
    return firstValueFrom(
      this.http.get<any>(`${this.QR_URL}${storeID}/loanout/${instrument_id}`, { headers, responseType: 'blob' as 'json' })
    )
  }
}
