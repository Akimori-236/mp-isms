// import { Injectable } from '@angular/core';
// import {
//   HttpRequest,
//   HttpHandler,
//   HttpEvent,
//   HttpInterceptor
// } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { AuthService } from './services/auth.service';

// @Injectable()
// export class AuthInterceptor implements HttpInterceptor {

//   constructor(private authSvc: AuthService) { }

//   // IDONTTHINKTHISISWORKING
//   intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
//     const token = this.authSvc.JWT
//     if (token) {
//       request = request.clone({
//         setHeaders: { Authorization: `Bearer ${token}` }
//       });
//     }
//     return next.handle(request);
//   }
// }