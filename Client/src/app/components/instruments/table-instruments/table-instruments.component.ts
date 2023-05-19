import { Component, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Observable, Subject } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Instrument } from 'src/app/models/instrument';
import { QrService } from 'src/app/services/qr.service';
import { StoreDataService } from 'src/app/services/store-data.service';
import { PopupQrComponent } from '../popup-qr/popup-qr.component';
import { FormAddinstrumentComponent } from '../form-addinstrument/form-addinstrument.component';
import { InstrumentService } from 'src/app/services/instrument.service';



@Component({
  selector: 'app-table-instruments',
  templateUrl: './table-instruments.component.html',
  styleUrls: ['./table-instruments.component.css']
})
export class TableInstrumentsComponent {
  @Input()
  instrumentList!: Instrument[]
  instruments$: Observable<Instrument[]>
  @Input()
  storeID!: string
  filter = new FormControl('', { nonNullable: true });
  @Output()
  onUpdate = new Subject()

  constructor(private modalService: NgbModal, private instruSvc: InstrumentService) {
    this.instruments$ = this.filter.valueChanges.pipe(
      startWith(''),
      map((text) => this.search(text)),
    );
  }

  search(text: string): Instrument[] {
    return this.instrumentList.filter((instrument) => {
      const term = text.toLowerCase();
      return (
        instrument.instrument_type.toLowerCase().includes(term) ||
        instrument.brand.toLowerCase().includes(term) ||
        instrument.model.toLowerCase().includes(term) ||
        instrument.serial_number.toLowerCase().includes(term) ||
        instrument.remarks.toLowerCase().includes(term)
        // pipe.transform(instrument.area).includes(term) ||
        // pipe.transform(instrument.population).includes(term)
      );
    });
  }

  onNewList(event: Instrument[]) {
    this.instrumentList = event
  }

  getQR(instrument_id: string) {
    const modalRef = this.modalService.open(PopupQrComponent)
    modalRef.componentInstance.instrument_id = instrument_id
    modalRef.componentInstance.storeID = this.storeID
  }

  openPopupUpdateInstrument(instrument: Instrument) {
    const modalRef = this.modalService.open(FormAddinstrumentComponent);
    modalRef.componentInstance.modalTitle = "Edit Instrument"
    modalRef.componentInstance.currentInstrument = instrument
    modalRef.result
      .then((result) => {
        // access formgroup in FormAddinstrumentComponent
        const updatedInstrument = modalRef.componentInstance.addInstrumentForm.value as Instrument
        console.log(updatedInstrument)
        // call SB
        this.instruSvc.updateInstrument(updatedInstrument)
          .then(response => {
            console.log(response)
            this.onUpdate.next(true)
            // this.getStoreDetails()
          })
          .catch(error => {
            console.error(error)
            this.onUpdate.next(true)
            // this.getStoreDetails()
          })
      },
        (reason) => {
          console.log(`Dismissed ${this.getDismissReason(reason)}`)
        })
      .catch(error => console.error(error))
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
