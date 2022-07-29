import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { Observable } from 'rxjs';
import { TwoFaService } from './two-fa.service';

@Component({
  selector: 'jhi-two-fa',
  templateUrl: './two-fa.component.html',
  styleUrls: ['./two-fa.component.scss'],
})
export class TwoFaComponent implements OnInit {
  @ViewChild('twofa', { static: false })
  twofa!: ElementRef;
  account!: Account;
  test_image!: string;
  qrCodeImage = '';
  isUsing2FA = false;
  authenticationError = false;

  checkoutForm = this.fb.group({
    lastName: '',
  });

  loginForm = this.fb.group({
    username: [null, [Validators.required]],
    password: [null, [Validators.required]],
    rememberMe: [false],
  });

  settingsForm = this.fb.group({
    firstName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    email: [undefined, [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    langKey: [undefined],
  });

  constructor(
    private accountService: AccountService,
    private twoFaService: TwoFaService,
    private loginService: LoginService,
    private router: Router,
    private fb: FormBuilder,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        this.settingsForm.patchValue({
          firstName: account.firstName,
          lastName: account.lastName,
          email: account.email,
          langKey: account.langKey,
        });
        this.account = account;
      }
    });
  }

  createQRCode(): void {
    this.test_image = `${this.account.login}_QRCode.png`;
    this.elRef.nativeElement.style.setProperty('--test_image', `url('${this.test_image.toString()}')`);
    console.warn('this.test_image======= ', this.test_image);
  }

  twoFaAutenticate(): void {
    this.account.firstName = this.settingsForm.get('firstName')!.value;
    this.account.lastName = this.settingsForm.get('lastName')!.value;
    this.account.email = this.settingsForm.get('email')!.value;
    this.account.langKey = this.settingsForm.get('langKey')!.value;
    this.twoFaService.createtwofa(this.account).subscribe(
      data => {
        this.authenticationError = false;
        if (data.isImageQRCode) {
          this.isUsing2FA = true;
          this.qrCodeImage = data.imageUrl;
        }
        if (!this.router.getCurrentNavigation()) {
          // There were no routing during login (eg from navigationToStoredUrl)
          this.router.navigate(['/twofa']);
        }
      },
      err => {
        this.authenticationError = true;
      }
    );
    this.createQRCode();
  }

  returnTwoFaAutenticate(): void {
    if (!this.router.getCurrentNavigation()) {
      console.warn('Register!');
      // There were no routing during login (eg from navigationToStoredUrl)
      this.router.navigate(['/verify']);
    }
  }
  onSubmit(): void {
    console.warn('Your order has been submitted', this.checkoutForm.value);
    this.checkoutForm.reset();
  }
}
