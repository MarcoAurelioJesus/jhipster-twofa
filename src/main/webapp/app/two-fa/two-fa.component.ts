import { Component, ViewChild, OnInit, AfterViewInit, ElementRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { TwoFaService } from './two-fa.service';
@Component({
  selector: 'jhi-two-fa',
  templateUrl: './two-fa.component.html',
  styleUrls: ['./two-fa.component.scss'],
})
export class TwoFaComponent implements OnInit, AfterViewInit {
  @ViewChild('username', { static: false })
  username!: ElementRef;
  form: any = {};
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  currentUser: any;
  account!: Account;
  test_image!: string;
  qrCodeImage = '';
  isUsing2FA = false;

  authenticationError = false;

  loginFormTwoFa = this.fb.group({
    username: [null, [Validators.required]],
  });
  constructor(
    private accountService: AccountService,
    private twoFaService: TwoFaService,
    private router: Router,
    private fb: FormBuilder,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['/login']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username.nativeElement.focus();
  }

  createQRCode(): void {
    // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
    this.test_image = `${this.username}_QRCode.png`;
    this.elRef.nativeElement.style.setProperty('--test_image', `url('${this.test_image.toString()}')`);
  }

  twoFaAutenticate(): void {
    this.twoFaService
      .createtwofa({
        username: this.loginFormTwoFa.get('username')!.value,
        password: '',
        twofacode: '',
        rememberMe: false,
      })
      .subscribe(
        data => {
          this.authenticationError = false;
          if (data?.imageUrl) {
            this.isUsing2FA = true;
            this.qrCodeImage = data.imageUrl;
          }
          if (!this.router.getCurrentNavigation()) {
            // There were no routing during login (eg from navigationToStoredUrl)
            this.router.navigate(['/twofa']);
          }
        },
        () => {
          this.authenticationError = true;
        }
      );
    this.createQRCode();
  }

  returnTwoFaAutenticate(): void {
    if (!this.router.getCurrentNavigation()) {
      console.warn('Register!');
      // There were no routing during login (eg from navigationToStoredUrl)
      this.router.navigate(['/login']);
    }
  }
  onSubmit(): void {
    console.warn('Your order has been submitted', this.loginFormTwoFa.value);
    this.loginFormTwoFa.reset();
  }
}
