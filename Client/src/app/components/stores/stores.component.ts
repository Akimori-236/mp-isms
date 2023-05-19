import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Store } from 'src/app/models/store';
import { AuthService } from 'src/app/services/auth.service';
import { StoreDataService } from 'src/app/services/store-data.service';

@Component({
  selector: 'app-stores',
  templateUrl: './stores.component.html',
  styleUrls: ['./stores.component.css']
})
export class StoresComponent implements OnInit {
  isLoggedIn!: boolean
  storeList!: Store[]
  createStoreForm!: FormGroup
  currentStoreID!: string
  currentStoreName!: string

  constructor(
    private authSvc: AuthService,
    private router: Router,
    private storeSvc: StoreDataService,
    private modalService: NgbModal,
    private fb: FormBuilder,) { }

  ngOnInit(): void {
    this.createStoreForm = this.fb.group({
      storeName: this.fb.control<string>('', [Validators.required]),
    })
    this.isLoggedIn = this.authSvc.isLoggedIn
    // go login if not logged in
    if (!this.isLoggedIn) {
      this.router.navigate(['/login'])
    } else {
      // load managed stores
      this.loadStores()
    }
  }

  loadStores() {
    this.storeSvc.getManagedStores()
      .then(response => { this.storeList = response })
      .catch((err) => {
        console.warn(err)
      })
  }

  openPopup(content: any) {
    const modalRef = this.modalService.open(content, { ariaLabelledBy: 'modal-create-store' });

    modalRef.result
      .then((result) => {
        // Handle modal close event
        console.log(`Closed with: ${result}`)

        // Perform API request and handle the response
        const storeName = this.createStoreForm.value['storeName']
        this.storeSvc.createStore(storeName)
          .then(response => {
            console.log("create store: ", response)
            // Reload component data
            this.loadStores()
          })
          .catch(err => {
            console.warn(err)
            // TODO: Open popup warning for failure
          });
      })
      .catch((reason) => {
        // Handle modal dismiss event
        console.log(`Dismissed ${this.getDismissReason(reason)}`)
      });
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

  getStoreDetails(storeid: string, storeName: string) {
    // console.debug("Loading Store: " + storeid)
    this.currentStoreID = storeid
    this.currentStoreName = storeName
  }
}
