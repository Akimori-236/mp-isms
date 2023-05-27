import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Instrument } from 'src/app/models/instrument';
import { AuthService } from 'src/app/services/auth.service';
import { InstrumentService } from 'src/app/services/instrument.service';

@Component({
  selector: 'app-borrow',
  templateUrl: './borrow.component.html',
  styleUrls: ['./borrow.component.css']
})
export class BorrowComponent implements OnInit {
  instrumentID!: string
  instrument!: Instrument

  constructor(
    private activatedRoute: ActivatedRoute,
    private instruSvc: InstrumentService,
    private authSvc: AuthService,
    private router: Router) { }

  ngOnInit(): void {
    this.instrumentID = this.activatedRoute.snapshot.params['instrumentid']
    const fullPath = this.activatedRoute.snapshot.url.toString();
    // console.log(fullPath)
    if (this.authSvc.isLoggedIn) {
      this.instruSvc.getInstrument(this.instrumentID).then(
        (response) => {
          this.instrument = response
          // console.debug(this.instrument)
        }
      )
    } else {
      let queryParams = { queryParams: { fullPath } }
      this.router.navigate(['/login'], queryParams)
    }
  }

  borrow() {
    this.instruSvc.borrow(this.instrumentID).then(response => this.router.navigate(['/borrowed']))
      .catch((error: HttpErrorResponse) => {
        if (error.status == 404) {
          console.warn("QR code might have expired")
        } else {
          console.warn("Error borrowing instrument: " + error)
        }
      })
  }

}
