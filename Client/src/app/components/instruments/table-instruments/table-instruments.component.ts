import { Component, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ModalDismissReasons, NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';

import { Observable, Subject } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Instrument } from 'src/app/models/instrument';
import { PopupQrComponent } from '../popup-qr/popup-qr.component';
import { FormAddinstrumentComponent } from '../form-addinstrument/form-addinstrument.component';
import { InstrumentService } from 'src/app/services/instrument.service';
import { StoreDataService } from 'src/app/services/store-data.service';



@Component({
  selector: 'app-table-instruments',
  templateUrl: './table-instruments.component.html',
  styleUrls: ['./table-instruments.component.css']
})
export class TableInstrumentsComponent implements OnInit {
  @Input()
  instrumentList!: Instrument[]
  instruments$: Observable<Instrument[]>
  @Input()
  storeID!: string
  filter = new FormControl('', { nonNullable: true });
  @Output()
  onUpdate = new Subject()

  constructor(
    private modalService: NgbModal,
    private modalConfig: NgbModalConfig,
    private instruSvc: InstrumentService) {

    this.modalConfig.centered = true
    this.instruments$ = this.filter.valueChanges.pipe(
      startWith(''),
      map((text) => this.search(text)),
    );
  }

  ngOnInit(): void {
  }

  search(text: string): Instrument[] {
    return this.instrumentList.filter((instrument) => {
      const term = text.toLowerCase();
      return (
        instrument.instrument_type.toLowerCase().includes(term) ||
        instrument.brand.toLowerCase().includes(term) ||
        instrument.model.toLowerCase().includes(term) ||
        instrument.serial_number.toLowerCase().includes(term)
        // pipe.transform(instrument.area).includes(term) ||
        // pipe.transform(instrument.population).includes(term)
      );
    });
  }

  getQR(instrument_id: string) {
    const modalRef = this.modalService.open(PopupQrComponent)
    modalRef.componentInstance.instrument_id = instrument_id
    modalRef.componentInstance.storeID = this.storeID
  }

  returnInstrument(instrument_id: string, instrument_type: string, serial_number: string) {
    let confirmText: string = `Confirm returning of ${instrument_type} (S/N: ${serial_number})`
    if (window.confirm(confirmText) == true) {
      this.instruSvc.returnInstrument(this.storeID, instrument_id)
        .then(isReturned => {
          // window.alert(isReturned)
          this.onUpdate.next(isReturned)
        })
        .catch(error => window.alert(error))
    } else { window.alert("Transaction cancelled") }
  }

  openPopupUpdateInstrument(instrument: Instrument) {
    const modalRef = this.modalService.open(FormAddinstrumentComponent);
    modalRef.componentInstance.modalTitle = "Edit Instrument"
    modalRef.componentInstance.currentInstrument = instrument
    modalRef.componentInstance.currentStoreID = this.storeID
    modalRef.result
      .then((result) => {
        // access formgroup in FormAddinstrumentComponent
        const updatedInstrument = modalRef.componentInstance.addInstrumentForm.value as Instrument
        // console.info("Updating: ", updatedInstrument)
        // call SB on parent
        this.onUpdate.next(updatedInstrument)
      },
        (reason) => {
          // console.log(`Dismissed ${this.getDismissReason(reason)}`)
        })
      .catch(error => console.warn(error))
  }

  public delete(instrument_id: string) {
    this.instruSvc.deleteInstrument(instrument_id)
      .then(

    )
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
