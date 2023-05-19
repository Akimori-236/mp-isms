import { DecimalPipe } from '@angular/common';
import { Component, Input, OnInit, PipeTransform } from '@angular/core';
import { FormControl } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, map, startWith } from 'rxjs';
import { Log } from 'src/app/models/log';
import { StoreDataService } from 'src/app/services/store-data.service';

@Component({
  selector: 'app-table-storelogs',
  templateUrl: './table-storelogs.component.html',
  styleUrls: ['./table-storelogs.component.css']
})
export class TableStorelogsComponent implements OnInit {
  @Input()
  storeID!: string
  @Input()
  storeName!: string
  logs: Log[] = []
  logs$: Observable<Log[]>
  filter = new FormControl('', { nonNullable: true });

  constructor(
    public activeModal: NgbActiveModal,
    private storeSvc: StoreDataService,
    pipe: DecimalPipe) {
    this.logs$ = this.filter.valueChanges.pipe(
      startWith(''),
      map((text) => this.search(text)),
    );
  }

  ngOnInit(): void {
    this.storeSvc.getStoreLogs(this.storeID).then(
      (response) => {
        this.logs = response
      }
    )
  }

  search(text: string): Log[] {
    return this.logs.filter((log) => {
      const term = text.toLowerCase();
      return (
        log.activity.toLowerCase().includes(term) ||
        log.email.toLowerCase().includes(term) ||
        log.instrument_type.toLowerCase().includes(term) ||
        log.serial_number.toLowerCase().includes(term) ||
        log.isRepairing.toString().toLowerCase().includes(term) ||
        log.remarks?.toLowerCase().includes(term)
        // pipe.transform(log.area).includes(term) ||
        // pipe.transform(log.population).includes(term)
      );
    });
  }
}
