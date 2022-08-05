import { Component, ViewChild, OnInit, AfterViewInit, ElementRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from 'app/core/auth/account.model';

import { AccountService } from 'app/core/auth/account.service';
import { TwoFaVerifyService } from './two-fa-verify.service';

@Component({
  selector: 'jhi-two-fa-verify',
  templateUrl: './two-fa-verify.component.html',
  styleUrls: ['./two-fa-verify.component.scss'],
})
export class TwoFaVerifyComponent implements OnInit, AfterViewInit {
  @ViewChild('username', { static: false })
  username!: ElementRef;
  twofacode!: ElementRef;
  account!: Account;

  authenticationError = false;

  loginForm = this.fb.group({
    twofacode: [null, [Validators.required]],
  });

  settingsForm = this.fb.group({
    firstName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    email: [undefined, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    langKey: [undefined],
  });

  constructor(
    private accountService: AccountService,
    private twoFaVerifyService: TwoFaVerifyService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // if already authenticated then navigate to home page
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['/verify']);
      }
    });
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.settingsForm.patchValue({
          firstName: account.firstName,
          lastName: account.lastName,
          email: account.email,
          langKey: account.langKey,
          twofacode: account.twoFACode,
        });
        this.account = account;
      }
    });
  }

  ngAfterViewInit(): void {
    this.username.nativeElement.focus();
  }

  register(): void {
    if (!this.router.getCurrentNavigation()) {
      console.warn('Register!');
      // There were no routing during login (eg from navigationToStoredUrl)
      this.router.navigate(['/twofa']);
    }
  }

  verify(): void {
    this.account.firstName = this.settingsForm.get('firstName')!.value;
    this.account.lastName = this.settingsForm.get('lastName')!.value;
    this.account.email = this.settingsForm.get('email')!.value;
    this.account.langKey = this.settingsForm.get('langKey')!.value;
    this.account.twoFACode = this.loginForm.get('twofacode')!.value;
    console.warn(this.loginForm.get('twofacode')!.value, "== this.settingsForm.get('twofacode')!.value;---");
    this.twoFaVerifyService.verify(this.account).subscribe({
      next: () => {
        if (!this.router.getCurrentNavigation()) {
          console.warn('Autenticate true');
          // There were no routing during login (eg from navigationToStoredUrl)
          this.router.navigate(['']);
        }
      },
      error: () => (this.authenticationError = true),
    });
  }
}
