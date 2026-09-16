import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { Client } from '../../core/models';
import { ClientService } from '../../core/services/client.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule],
  selector: 'app-clients',
  templateUrl: './clients.html',
  styleUrl: './clients.scss',
})
export class ClientsPage implements OnInit {
  private clientService = inject(ClientService);
  private auth = inject(AuthService);

  clients = signal<Client[]>([]);
  loading = signal(false);
  showForm = signal(false);
  saving = signal(false);
  error = signal('');

  name = '';
  email = '';

  readonly isAdmin = this.auth.isAdmin;

  async ngOnInit() {
    await this.reload();
  }

  async reload() {
    this.loading.set(true);
    try {
      this.clients.set(await firstValueFrom(this.clientService.list()));
    } finally {
      this.loading.set(false);
    }
  }

  openForm() {
    this.showForm.set(true);
    this.error.set('');
    this.name = '';
    this.email = '';
  }

  cancelForm() {
    this.showForm.set(false);
  }

  async save() {
    if (!this.name.trim() || !this.email.trim()) {
      this.error.set('Informe nome e email do cliente.');
      return;
    }
    if (!/^\S+@\S+\.\S+$/.test(this.email.trim())) {
      this.error.set('Email inválido.');
      return;
    }
    this.saving.set(true);
    this.error.set('');
    try {
      await firstValueFrom(this.clientService.create(this.name.trim(), this.email.trim()));
      this.showForm.set(false);
      await this.reload();
    } catch {
      this.error.set('Não foi possível criar o cliente (email duplicado?).');
    } finally {
      this.saving.set(false);
    }
  }
}