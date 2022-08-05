import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { TwoFaComponent } from './two-fa.component';
import { TWOFA_ROUTE } from './two-fa.route';

@NgModule({
  imports: [CommonModule, SharedModule, RouterModule.forChild([TWOFA_ROUTE])],
  declarations: [TwoFaComponent],
})
export class TwoFaModule {}
