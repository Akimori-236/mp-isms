import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  mapWidth = 375
  mapHeight = 375
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
    this.center = { lat, lng }
    this.markerOptions = {
      position: { lat, lng },
      animation: google.maps.Animation.BOUNCE,
    }
  }

}
