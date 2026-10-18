import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const session = inject(AuthService).session();
  const secured = session ? request.clone({ setHeaders: { Authorization: `${session.type || 'Bearer'} ${session.token}` } }) : request;
  return next(secured).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401 && !request.url.endsWith('/auth/login')) { inject(AuthService).logout(); }
    return throwError(() => error);
  }));
};
