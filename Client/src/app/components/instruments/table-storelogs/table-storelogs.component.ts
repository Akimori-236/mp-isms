
import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, map, startWith } from 'rxjs';
import { Log } from 'src/app/models/log';

@Component({
  selector: 'app-table-storelogs',
  templateUrl: './table-storelogs.component.html',
  styleUrls: ['./table-storelogs.component.css']
})
export class TableStorelogsComponent {
  @Input()
  storeName!: string
  @Input()
  logs: Log[] = []
  logs$: Observable<Log[]>
  filter = new FormControl('', { nonNullable: true });

  constructor(
    public activeModal: NgbActiveModal) {
    this.logs$ = this.filter.valueChanges.pipe(
      startWith(''),
      map((text) => this.search(text)),
    );
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
