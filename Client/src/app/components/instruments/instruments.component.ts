import { AfterViewInit, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ModalDismissReasons, NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import { Instrument } from 'src/app/models/instrument';
import { User } from 'src/app/models/user';
import { StoreDataService } from 'src/app/services/store-data.service';
import { FormAddinstrumentComponent } from './form-addinstrument/form-addinstrument.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { InstrumentService } from 'src/app/services/instrument.service';
import { TableStorelogsComponent } from './table-storelogs/table-storelogs.component';
import { StoreSettingsComponent } from '../stores/store-settings/store-settings.component';
import { ToastsComponent } from '../toasts/toasts.component';
import { MessagingService } from 'src/app/services/messaging.service';
import { Toast } from 'src/app/models/toast';

@Component({
  selector: 'app-instruments',
  templateUrl: './instruments.component.html',
  styleUrls: ['./instruments.component.css']
})
export class InstrumentsComponent implements OnChanges, AfterViewInit {
  @Input()
  currentStoreID!: string
  @Input()
  currentStoreName!: string
  instrumentList!: Instrument[]
  managerList!: User[]
  isAdding: boolean = false
  addManagerForm!: FormGroup
  logList = []


  constructor(
    private storeSvc: StoreDataService,
    private instruSvc: InstrumentService,
    private modalService: NgbModal,
    private modalConfig: NgbModalConfig,
    private fb: FormBuilder,
    private msgSvc: MessagingService) {
    this.modalConfig.centered = true
  }

  ngOnChanges(changes: SimpleChanges): void {
    // console.log(changes)
    // call api for data
    this.getStoreDetails()
  }

  ngAfterViewInit(): void {
    this.addManagerForm = this.fb.group({
      managerEmail: this.fb.control<string>('', [Validators.required, Validators.email]),
    })
  }

  getStoreDetails() {
    this.storeSvc.getStoreDetails(this.currentStoreID).then(
      response => {
        this.instrumentList = response['instruments']
        console.debug(this.instrumentList)
        this.managerList = response['managers']
        console.debug(this.managerList)
      }
    )
  }

  openPopupAddInstrument() {
    const modalRef = this.modalService.open(FormAddinstrumentComponent);
    modalRef.componentInstance.modalTitle = "Add Instrument"
    modalRef.result
      .then((result) => {
        // access formgroup in FormAddinstrumentComponent
        const newInstrument = modalRef.componentInstance.addInstrumentForm.value as Instrument
        console.log(newInstrument)
        newInstrument.store_id = this.currentStoreID
        // call SB
        this.instruSvc.addNewInstrument(this.currentStoreID, newInstrument)
          .then(response => {
            this.getStoreDetails()
            console.log(response)
            window.location.reload() // just in case
          })
          .catch(error => {
            this.getStoreDetails()
            console.error(error)
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

  openPopupAddManager(content: any) {
    const modalRef = this.modalService.open(content, { ariaLabelledBy: 'modal-add-manager' });
    modalRef.result
      .then((result) => {
        // Handle modal close event
        console.log(`Closed with: ${result}`)

        // Perform API request and handle the response
        const managerEmail = this.addManagerForm.value['managerEmail']
        console.info("Sending invite to: " + managerEmail)
        this.storeSvc.sendInviteManager(this.currentStoreID, managerEmail)
          .then(response => {
            console.log("sent invite for manager: ", response)
            let successToast: Toast = { title: "Invite Sent", body: "Successfully added user as manager. \nAn email has been sent to the user." }
            this.msgSvc.showToast(successToast)
          })
          .catch(err => {
            console.warn(err)
            if (err.status == 400) {
              let errorToast: Toast = { title: "Unregistered Email", body: "This email is not registered, please get user to register before adding as manager." }
              this.msgSvc.showToast(errorToast)
            }
          });
      })
      .catch((reason) => {
        // Handle modal dismiss event
        console.log(`Dismissed ${this.getDismissReason(reason)}`)
      });
  }

  openPopupStoreLogs() {
    const modalRef = this.modalService.open(TableStorelogsComponent);
    modalRef.componentInstance.storeName = this.currentStoreName
    this.storeSvc.getStoreLogs(this.currentStoreID).then(
      (response) => {
        console.debug(response)
        modalRef.componentInstance.logs = response
      }
    )
    modalRef.result
      .then((result) => {
        // Handle modal close event
        console.log(`Closed with: ${result}`)
      })
      .catch((reason) => {
        // Handle modal dismiss event
        console.log(`Dismissed ${this.getDismissReason(reason)}`)
      });
  }

  openStoreSettings() {
    const modalRef = this.modalService.open(StoreSettingsComponent);
    modalRef.componentInstance.storeID = this.currentStoreID
    modalRef.componentInstance.storeName = this.currentStoreName
  }

}