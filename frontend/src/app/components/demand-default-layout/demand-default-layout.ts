import {Component, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DatePipe} from '@angular/common';
import {ChangeDetectorRef} from '@angular/core';
import {FormsModule} from '@angular/forms';

export interface Demand {
  create_time: string;
  description: string;
  finish_time: string | null;
  public_code: string;
  public_id: string;
  started_time: string | null;
  stopped_time: string | null;
  title: string;
  user_name: string | null;
  time_wasted: string;
  user_id?: string;
  user_demand_id?: number;
  demand_status_code?: number;
}

export interface User {
  user_id: string;
  user_name: string;
}

@Component({
  selector: 'app-demand-default-layout',
  standalone: true,
  imports: [DatePipe, FormsModule],
  styleUrl: './demand-default-layout.scss',
  templateUrl: './demand-default-layout.html',
})
export class DemandDefaultLayoutComponent {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef)
  demands: Demand[] = [];
  users: User[] = [];
  carregandoUsuarios = false;
  usuariosCarregados = false;

  showCreateWindow: boolean = false;
  newDemand = {
    title: '', description: '', user_id: null as string | null, demand_status_code: ''
  };

  constructor() {
    console.log('🔥 DemandDefaultLayout CRIADO');

    this.carregarDemandas();
  }

  abrirJanelaCriar(): void {
    this.newDemand = {
      title: '', description: '', user_id: '', demand_status_code: ''
    };

    this.showCreateWindow = true;
  }

  fecharJanelaCriar(): void {
    this.showCreateWindow = false;
  }

  criarDemanda(): void {

    if (!this.newDemand.title.trim()) {
      return;
    }

    const demanda = {
      title: this.newDemand.title, description: this.newDemand.description, user_id: this.newDemand.user_id,
      demand_status_code: this.newDemand.demand_status_code
    };

    this.http
      .post('http://localhost:8080/demand/create', demanda)
      .subscribe({
        next: (response) => {
          console.log('Demanda criada:', response);

          this.fecharJanelaCriar();
          this.carregarDemandas();
        }, error: (err) => {
          console.error('Erro ao criar demanda:', err);
        }
      });
  }

  carregarDemandas(): void {
    this.http
      .get<Demand[]>('http://localhost:8080/demand/list')
      .subscribe({
        next: (dados) => {
          console.log('Demandas recebidas:', dados);
          console.log('Quantidade:', dados.length);

          this.demands = [...dados];

          this.cdr.detectChanges();
          console.log('Demandas no componente:', this.demands);
        }, error: (err) => {
          console.error('Erro ao carregar demandas:', err);
        },
      });
  }

  carregarUsuarios(): void {
    if (this.usuariosCarregados || this.carregandoUsuarios) {
      return;
    }
    this.carregandoUsuarios = true;
    this.http
      .post<User[]>('http://localhost:8080/user/list', {
        is_active: 1
      })
      .subscribe({
        next: (dados) => {
          console.log('Usuários recebidos:', dados);

          this.users = dados;
          this.usuariosCarregados = true;
          this.carregandoUsuarios = false;

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erro ao carregar usuários:', err);

          this.carregandoUsuarios = false;
        }
      });
  }

  calcularTempoGasto(d: Demand): string {
    const started = d.started_time;
    const ended = d.finish_time ?? d.stopped_time;

    if (!started) {
      return '-';
    }

    const start = new Date(started);
    const end = ended ? new Date(ended) : new Date();

    const diffMs = end.getTime() - start.getTime();

    if (diffMs < 0) {
      return '-';
    }

    const totalSeconds = Math.floor(diffMs / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    const parts: string[] = [];

    if (hours > 0) parts.push(`${hours}h`);
    if (minutes > 0 || hours > 0) parts.push(`${minutes}m`);

    parts.push(`${seconds}s`);

    return parts.join(' ');
  }

}
