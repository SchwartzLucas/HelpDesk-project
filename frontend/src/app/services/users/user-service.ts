import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../../components/demands/demand-edit-layout/demand-edit-layout';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private http = inject(HttpClient);

  carregarUsuarios(): Observable<User[]> {
    return this.http.post<User[]>('http://localhost:8080/user/list', {
      is_active: 1
    });
  }
}
