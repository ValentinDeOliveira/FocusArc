import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, switchMap, throwError} from 'rxjs';
import {AuthService} from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const authService = inject(AuthService);

    return next(req.clone({withCredentials: true})).pipe(
        catchError((error: HttpErrorResponse) => {
            const isAuthEndpoint = req.url.includes('/auth/');

            if (error.status === 401 && !isAuthEndpoint) {
                return authService.refresh().pipe(
                    switchMap(() => next(req.clone({withCredentials: true}))),
                    catchError(() => {
                        void router.navigate(['/login']);
                        return throwError(() => error);
                    })
                );
            }

            // if refresh return 401, redirect to login
            if (error.status === 401 && isAuthEndpoint) {
                void router.navigate(['/login']);
            }

            return throwError(() => error);
        })
    );
};
