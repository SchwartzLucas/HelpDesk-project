import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Component, computed } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { TimerWidget } from '../components/timer-widget/timer-widget';

@Component({
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TimerWidget],
  selector: 'app-layout',
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.scss',
})
export class AppLayout {
  readonly userName = computed(() => this.auth.user()?.name ?? '');
  readonly roleLabel = computed(() => {
    const role = this.auth.user()?.role;
    return role === 'ADMIN_USER' ? 'Admin' : role === 'SUPPORT_USER' ? 'Suporte' : 'Usuário';
  });
  readonly isAdmin = computed(() => this.auth.isAdmin());

  constructor(public auth: AuthService) {}

  logout() {
    this.auth.logout();
    window.location.href = '/login';
  }
}