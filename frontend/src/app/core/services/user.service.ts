import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  me() {
    return this.http.get<User>('/api/users/me');
  }

  list() {
    return this.http.get<User[]>('/api/users/list');
  }
}