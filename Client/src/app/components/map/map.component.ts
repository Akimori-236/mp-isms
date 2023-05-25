import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { maps } from 'google-one-tap';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  mapWidth = 465
  mapHeight = 465
  zoom = 15
  center!: google.maps.LatLngLiteral
  mapOptions: google.maps.MapOptions = {
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    zoomControl: true,
    scrollwheel: true,
    disableDoubleClickZoom: true,
    maxZoom: 20,
    minZoom: 8
  }
  markerOptions!: google.maps.MarkerOptions

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    let lat = 1.3596865
    let lng = 103.818
    // get center
    this.center = { lat, lng }
    this.markerOptions = {
      position: { lat, lng },
      animation: google.maps.Animation.BOUNCE,
    }
  }

}
