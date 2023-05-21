import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Instrument } from 'src/app/models/instrument';
import { AuthService } from 'src/app/services/auth.service';
import { InstrumentService } from 'src/app/services/instrument.service';

@Component({
  selector: 'app-borrowed',
  templateUrl: './borrowed.component.html',
  styleUrls: ['./borrowed.component.css']
})
export class BorrowedComponent implements OnInit {
  isLoggedIn!: boolean
  instrumentList!: Instrument[]

  constructor(
    private instruSvc: InstrumentService,
    private authSvc: AuthService,
    private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authSvc.isLoggedIn
    if (!this.isLoggedIn) {
      this.router.navigate(['/login'])
    }
    const token = localStorage.getItem('jwt')
    // load borrowed items
    this.instruSvc.getBorrowed()
      .then(response => {
        console.log(response)
        this.instrumentList = response
      }).catch((err) => {
        console.error(err)
      })
  }

  returnInstrument(id: string) {
    console.debug(id)
    // call server for qr-url for accepter to scan

    // redirect? closable popup for qr image?
  }
}
