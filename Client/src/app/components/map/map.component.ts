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
    zoom: 15,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  }
  markerOptions: google.maps.MarkerOptions = {
    position: { lat: this.lat, lng: this.lng },
    animation: google.maps.Animation.BOUNCE
  }

  constructor(public activeModal: NgbActiveModal) { }

}
