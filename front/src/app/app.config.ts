import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {provideToastr} from 'ngx-toastr';
import {provideNativeDateAdapter} from '@angular/material/core';
import {provideCalendar, DateAdapter} from 'angular-calendar';
import {adapterFactory} from 'angular-calendar/date-adapters/date-fns';

export const appConfig: ApplicationConfig = {
  providers: [
      provideBrowserGlobalErrorListeners(),
      provideRouter(routes),
      provideHttpClient(withInterceptors([authInterceptor])),
      provideToastr({ positionClass: 'toast-top-center' }),
      provideNativeDateAdapter(),
      provideCalendar({provide: DateAdapter, useFactory: adapterFactory}),
  ],
};
