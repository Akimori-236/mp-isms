import { AfterContentChecked, Component, Input, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Instrument } from 'src/app/models/instrument';

@Component({
  selector: 'app-form-addinstrument',
  templateUrl: './form-addinstrument.component.html',
  styleUrls: ['./form-addinstrument.component.css']
})
export class FormAddinstrumentComponent implements OnInit {

  addInstrumentForm!: FormGroup
  @Input()
  currentStoreID!: string
  @Input()
  currentStoreName!: string
  @Input()
  currentInstrument: Instrument | null = null
  @Input()
  modalTitle: string = ""

  constructor(
    private fb: FormBuilder,
    public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    this.addInstrumentForm = this.fb.group({
      instrument_id: this.fb.control<string>(''),
      store_id: this.fb.control<string>(this.currentStoreID),
      
      instrument_type: this.fb.control<string>('', [Validators.required]),
      brand: this.fb.control<string>('', [Validators.required]),
      model: this.fb.control<string>('', [Validators.required]),
      serial_number: this.fb.control<string>('', [Validators.required]),
      isRepairing: this.fb.control<boolean>(false, [Validators.required]),
      remarks: this.fb.control<string>('')
    })
    if (this.currentInstrument) {
      this.addInstrumentForm.patchValue(this.currentInstrument);
    }
  }
}
