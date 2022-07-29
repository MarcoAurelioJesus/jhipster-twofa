import { Route } from '@angular/router';
import { TwoFaVerifyComponent } from './two-fa-verify.component';

export const TWO_FA_VERIFY_ROUTE: Route = {
  path: '',
  component: TwoFaVerifyComponent,
  data: {
    pageTitle: 'twofaverify.title',
  },
};
