import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Team, User } from '../models';

@Injectable({ providedIn: 'root' })
export class TeamService {
  constructor(private http: HttpClient) {}

  list() {
    return this.http.get<Team[]>('/api/teams/list');
  }

  create(name: string, description: string, managerId: number) {
    return this.http.post<Team>('/api/teams/create', { name, description, managerId });
  }

  members(teamId: number) {
    return this.http.get<User[]>(`/api/teams/${teamId}/members`);
  }
}