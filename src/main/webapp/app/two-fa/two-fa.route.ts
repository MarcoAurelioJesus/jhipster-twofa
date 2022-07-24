import { Route } from '@angular/router';
import { TwoFaComponent } from './two-fa.component';

export const TWOFA_ROUTE: Route = {
  path: '',
  component: TwoFaComponent,
  data: {
    pageTitle: 'two-fa.title',
  },
};
