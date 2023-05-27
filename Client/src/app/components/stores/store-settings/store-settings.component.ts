import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StoreDataService } from 'src/app/services/store-data.service';

@Component({
  selector: 'app-store-settings',
  templateUrl: './store-settings.component.html',
  styleUrls: ['./store-settings.component.css']
})
export class StoreSettingsComponent implements OnInit {
  @Input()
  storeID!: string
  @Input()
  storeName!: string
  @Input()
  managerList!: User[]
  storeDetailsForm!: FormGroup

  constructor(
    public activeModal: NgbActiveModal,
    private storeSvc: StoreDataService,
    private fb: FormBuilder) { }

  ngOnInit(): void {
    this.storeDetailsForm = this.fb.group({
      email: this.fb.control<string>('', [Validators.required]),
    })
    this.storeSvc.getStoreDetails(this.storeID)
      .then()
      .catch()
  }
}
