import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {catchError, map, of} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {ArcService} from '../services/arc.service';

export const hasArcGuard: CanActivateFn = () => {
    const arcService = inject(ArcService);
    const router = inject(Router);

    return arcService.getAll().pipe(
        map(arcs => {
            const hasActiveArc = arcs.some(arc => arc.status === 'ACTIVE');
            return hasActiveArc ? true : router.createUrlTree(['/arc-creation']);
        }),
        catchError((error: HttpErrorResponse) => {
            if (error.status === 404) {
                return of(router.createUrlTree(['/arc-creation']));
            }
            return of(true); // 401 is handled by the interceptor
        })
    );
};