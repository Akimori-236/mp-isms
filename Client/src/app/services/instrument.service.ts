import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { firstValueFrom } from 'rxjs';
import { Instrument } from '../models/instrument';

@Injectable({
  providedIn: 'root'
})
export class InstrumentService {

  private INSTRUMENT_URL: string = "/api/instrument"

  constructor(private http: HttpClient, private authSvc: AuthService) { }

  getBorrowed(): Promise<Instrument[]> {
    const headers = this.authSvc.JWTHeaders
    return firstValueFrom(
      this.http.get<Instrument[]>(`${this.INSTRUMENT_URL}/borrowed`, { headers })
    )
  }

  getInstrument(id: string): Promise<Instrument> {
    const headers = this.authSvc.JWTHeaders
    return firstValueFrom(
      this.http.get<Instrument>(`${this.INSTRUMENT_URL}/${id}`, { headers })
    )
  }

  addNewInstrument(storeID: string, body: Instrument): Promise<null> {
    // console.debug(body)
    const headers = this.authSvc.JWTHeaders.set('Content-Type', 'application/json')
    return firstValueFrom(
      this.http.post<null>(`${this.INSTRUMENT_URL}/add/${storeID}`, { body }, { headers })
    )
  }

  updateInstrument(body: Instrument): Promise<boolean> {
    // console.debug(body)
    const headers = this.authSvc.JWTHeaders.set('Content-Type', 'application/json')
    return firstValueFrom(
      this.http.put<boolean>(`${this.INSTRUMENT_URL}/update`, { body }, { headers })
    )
  }

  borrowInstrument(instrument_id: string): Promise<boolean> {
    const headers = this.authSvc.JWTHeaders
    return firstValueFrom(
      this.http.put<boolean>(`${this.INSTRUMENT_URL}/borrow/${instrument_id}`, { headers })
    )
  }

  returnInstrument(store_id: string, instrument_id: string): Promise<boolean> {
    const headers = this.authSvc.JWTHeaders
    return firstValueFrom(
      this.http.put<boolean>(`${this.INSTRUMENT_URL}/return/${store_id}/${instrument_id}`, { headers })
    )
  }

}
