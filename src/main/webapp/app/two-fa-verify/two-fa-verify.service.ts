import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

@Injectable({ providedIn: 'root' })
export class TwoFaVerifyService {
  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  verify(twofacode: string): Observable<any> {
    return this.http.post(this.applicationConfigService.getEndpointFor('api/verify'), twofacode, {
      headers: new HttpHeaders({ 'Content-Type': 'text/plain' }),
    });
  }
}
