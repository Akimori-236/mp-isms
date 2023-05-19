import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Instrument } from '../models/instrument';
import { Store } from '../models/store';
import { AuthService } from './auth.service';
import { Log } from '../models/log';

@Injectable({
    providedIn: 'root'
})
export class StoreDataService {
    private STORE_URL: string = "/api/store"

    constructor(private http: HttpClient, private authSvc: AuthService) { }

    createStore(storeName: string): Promise<string> {
        const headers = this.authSvc.JWTHeaders
        headers.set("Content-type", "application/json")
        let params = new HttpParams().set("storename", storeName)
        return firstValueFrom(
            this.http.post<string>(`${this.STORE_URL}/create`, {}, { headers, params })
        )
    }

    getManagedStores(): Promise<Store[]> {
        const headers = this.authSvc.JWTHeaders
        headers.set("Content-type", "application/json")
        return firstValueFrom(
            this.http.get<Store[]>(this.STORE_URL, { headers })
        )
    }

    getStoreDetails(storeID: string): Promise<any> {
        // console.log(storeID)
        const headers = this.authSvc.JWTHeaders
        headers.set("Content-type", "application/json")
        return firstValueFrom(
            this.http.get<any>(`${this.STORE_URL}/${storeID}`, { headers })
        )
    }

    sendInviteManager(storeID: string, managerEmail: string): Promise<string> {
        const headers = this.authSvc.JWTHeaders.set('Content-Type', 'application/json')
        let params = new HttpParams().set("managerEmail", managerEmail)
        return firstValueFrom(
            this.http.post<string>(`${this.STORE_URL}/invite/${storeID}`, { headers, params })
        )
    }

    getStoreLogs(storeID: string): Promise<Log[]> {
        const headers = this.authSvc.JWTHeaders.set('Content-Type', 'application/json')
        return firstValueFrom(
            this.http.post<Log[]>(`${this.STORE_URL}/logs/${storeID}`, { headers })
        )
    }
}
