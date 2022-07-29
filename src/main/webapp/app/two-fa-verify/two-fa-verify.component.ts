import { Component, ViewChild, OnInit, AfterViewInit, ElementRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

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

  authenticationError = false;

  loginForm = this.fb.group({
    twofacode: [null, [Validators.required]],
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
    this.twoFaVerifyService.verify(this.loginForm.get('twofacode')!.value).subscribe({
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
