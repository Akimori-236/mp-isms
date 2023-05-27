import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Instrument } from 'src/app/models/instrument';
import { AuthService } from 'src/app/services/auth.service';
import { InstrumentService } from 'src/app/services/instrument.service';
import { MapComponent } from '../map/map.component';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse } from '@angular/common/http';

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
    private router: Router,
    private modalService: NgbModal,) { }

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
      }).catch((err: HttpErrorResponse) => {
        console.warn("Error getting borrowed list: " + err)
      })
  }

  showMap() {
    const modalRef = this.modalService.open(MapComponent);
    modalRef.result
      .then((result) => {
        // console.log(result)
      },
        (reason) => {
          // console.log(`Dismissed ${this.getDismissReason(reason)}`)
        })
      .catch(error => console.warn("Error showing map" + error))
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
}
