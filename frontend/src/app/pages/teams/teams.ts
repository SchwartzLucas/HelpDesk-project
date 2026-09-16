import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { Team, User } from '../../core/models';
import { TeamService } from '../../core/services/team.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-teams',
  templateUrl: './teams.html',
  styleUrl: './teams.scss',
})
export class TeamsPage implements OnInit {
  private teamService = inject(TeamService);
  private userService = inject(UserService);
  private auth = inject(AuthService);

  teams = signal<Team[]>([]);
  users = signal<User[]>([]);
  loading = signal(false);
  showForm = signal(false);
  saving = signal(false);
  error = signal('');

  name = '';
  description = '';
  managerId: number | null = null;

  readonly isAdmin = this.auth.isAdmin;

  async ngOnInit() {
    await this.reload();
  }

  async reload() {
    this.loading.set(true);
    try {
      this.teams.set(await firstValueFrom(this.teamService.list()));
      this.users.set(await firstValueFrom(this.userService.list()));
    } catch {
      // opcional
    } finally {
      this.loading.set(false);
    }
  }

  openForm() {
    this.showForm.set(true);
    this.error.set('');
  }

  cancelForm() {
    this.showForm.set(false);
    this.name = '';
    this.description = '';
    this.managerId = null;
  }

  async save() {
    if (!this.name.trim() || this.managerId == null) {
      this.error.set('Informe nome e um gestor para o time.');
      return;
    }
    this.saving.set(true);
    this.error.set('');
    try {
      await firstValueFrom(this.teamService.create(this.name.trim(), this.description.trim(), this.managerId));
      this.cancelForm();
      await this.reload();
    } catch {
      this.error.set('Não foi possível criar o time.');
    } finally {
      this.saving.set(false);
    }
  }
}