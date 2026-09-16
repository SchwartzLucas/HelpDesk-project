import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-login',
  styleUrl: './login.scss',
  templateUrl: './login.html',
})
export class Login {
  login = '';
  password = '';
  loading = signal(false);
  error = signal('');

  private auth = inject(AuthService);
  private router = inject(Router);

  async submit() {
    if (!this.login || !this.password) {
      this.error.set('Informe login e senha.');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    try {
      await firstValueFrom(this.auth.login(this.login, this.password));
      this.router.navigate(['/dashboard']);
    } catch {
      this.error.set('Login ou senha inválidos.');
      this.loading.set(false);
    }
  }

  register() {
    // registro de novos usuários fica restrito ao admin (página Usuários)
    this.error.set('Cadastro de usuário é feito pelo administrador.');
  }
}