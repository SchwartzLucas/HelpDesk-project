import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { Client, Role, Team, User } from '../../core/models';
import { UserService } from '../../core/services/user.service';
import { ClientService } from '../../core/services/client.service';
import { TeamService } from '../../core/services/team.service';
import { AuthService } from '../../core/services/auth.service';
import { ROLE_LABELS } from '../../core/utils';

@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-users',
  templateUrl: './users.html',
  styleUrl: './users.scss',
})
export class UsersPage implements OnInit {
  private userService = inject(UserService);
  private clientService = inject(ClientService);
  private teamService = inject(TeamService);
  private auth = inject(AuthService);

  users = signal<User[]>([]);
  teams = signal<Team[]>([]);
  clients = signal<Client[]>([]);
  loading = signal(false);
  showForm = signal(false);
  saving = signal(false);
  error = signal('');

  name = '';
  login = '';
  password = '';
  role: Role = 'COMMON_USER';
  teamId: number | null = null;
  clientId: number | null = null;

  readonly isAdmin = this.auth.isAdmin;
  readonly roleLabels = ROLE_LABELS;
  readonly roleOptions: Role[] = ['ADMIN_USER', 'SUPPORT_USER', 'COMMON_USER'];

  async ngOnInit() {
    await this.reload();
  }

  async reload() {
    this.loading.set(true);
    try {
      this.users.set(await firstValueFrom(this.userService.list()));
      this.teams.set(await firstValueFrom(this.teamService.list()));
      this.clients.set(await firstValueFrom(this.clientService.list()));
    } catch {
      this.error.set('Sem permissão para listar usuários.');
    } finally {
      this.loading.set(false);
    }
  }

  openForm() {
    this.showForm.set(true);
    this.error.set('');
    this.name = '';
    this.login = '';
    this.password = '';
    this.role = 'COMMON_USER';
    this.teamId = null;
    this.clientId = null;
  }

  cancelForm() {
    this.showForm.set(false);
  }

  async save() {
    if (!this.name.trim() || !this.login.trim() || !this.password.trim()) {
      this.error.set('Informe nome, login e senha.');
      return;
    }
    if (this.password.length < 4) {
      this.error.set('A senha deve ter ao menos 4 caracteres.');
      return;
    }
    this.saving.set(true);
    this.error.set('');
    try {
      await firstValueFrom(
        this.auth.register({
          name: this.name.trim(),
          login: this.login.trim(),
          password: this.password,
          role: this.role,
          teamId: this.teamId,
          clientId: this.clientId,
        }),
      );
      this.showForm.set(false);
      this.error.set('');
      await this.reload();
    } catch {
      this.error.set('Não foi possível criar o usuário (login já existe?).');
    } finally {
      this.saving.set(false);
    }
  }
}