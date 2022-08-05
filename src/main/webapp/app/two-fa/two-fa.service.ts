import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Login } from 'app/login/login.model';
import { Observable } from 'rxjs';
@Injectable({ providedIn: 'root' })
export class TwoFaService {
  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  createtwofa(credentials: Login): Observable<any> {
    return this.http.post(this.applicationConfigService.getEndpointFor('api/twofa'), credentials);
  }
}
