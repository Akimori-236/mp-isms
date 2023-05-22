import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { maps } from 'google-one-tap';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {
  map!: google.maps.Map;
  lat = 1.3596865
  lng = 103.818
  mapOptions: google.maps.MapOptions = {
    center: { lat: this.lat, lng: this.lng },
    zoom: 11,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }

  constructor(public activeModal: NgbActiveModal) { }

}
