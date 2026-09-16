import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client } from '../models';

@Injectable({ providedIn: 'root' })
export class ClientService {
  constructor(private http: HttpClient) {}

  list() {
    return this.http.get<Client[]>('/api/clients/list');
  }

  create(name: string, email: string) {
    return this.http.post<Client>('/api/clients/create', { name, email });
  }
}