import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { TwoFaVerifyComponent } from './two-fa-verify.component';
import { TWO_FA_VERIFY_ROUTE } from './two-fa-verify.route';

@NgModule({
  imports: [SharedModule, CommonModule, RouterModule.forChild([TWO_FA_VERIFY_ROUTE])],
  declarations: [TwoFaVerifyComponent],
})
export class TwoFaVerifyModule {}
