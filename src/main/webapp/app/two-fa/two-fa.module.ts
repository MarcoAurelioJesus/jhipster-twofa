import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TwoFaComponent } from './two-fa.component';
import { TWOFA_ROUTE } from './two-fa.route';

@NgModule({
  imports: [RouterModule.forChild([TWOFA_ROUTE])],
  declarations: [TwoFaComponent],
})
export class TwoFaModule {}
