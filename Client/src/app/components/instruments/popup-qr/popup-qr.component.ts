import { AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { QrService } from 'src/app/services/qr.service';

@Component({
  selector: 'app-popup-qr',
  templateUrl: './popup-qr.component.html',
  styleUrls: ['./popup-qr.component.css']
})
export class PopupQrComponent implements AfterViewInit, OnDestroy {
  @Input()
  instrument_id!: string
  @Input()
  storeID!: string
  qr: string | null = null;

  constructor(
    public activeModal: NgbActiveModal,
    private qrSvc: QrService) { }

  ngOnDestroy(): void {
    if (this.qr) {
      URL.revokeObjectURL(this.qr);
    }
  }

  ngAfterViewInit(): void {
    this.qrSvc.getLoanQR(this.instrument_id, this.storeID)
      .then(blob => {
        console.log(blob)
        this.qr = URL.createObjectURL(blob)
      })
      .catch(error => {
        console.error(error)
        // return error.error.text
      })
  }

}
