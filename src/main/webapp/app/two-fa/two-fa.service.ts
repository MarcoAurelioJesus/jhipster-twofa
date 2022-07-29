import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Account } from 'app/core/auth/account.model';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
@Injectable({ providedIn: 'root' })
export class TwoFaService {
  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  createtwofa(account: Account): Observable<any> {
    return this.http.post(this.applicationConfigService.getEndpointFor('api/twofa'), account);
  }
}
