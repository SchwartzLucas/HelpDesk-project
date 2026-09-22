import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {DatePipe} from '@angular/common';

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

@Component({
  selector: 'app-demand-default-layout',
  standalone: true,
  imports: [
    DatePipe
  ],
  styleUrl: './demand-default-layout.scss',
  templateUrl: './demand-default-layout.html',
})
export class DemandDefaultLayoutComponent {
  private http = inject(HttpClient);

  demands: Demand[] = [];

  constructor() {
    this.carregarDemandas();
  }

  carregarDemandas() {
    this.http
      .get<Demand[]>('http://localhost:8080/demand/list')
      .subscribe({
        next: (dados) => {
          console.log('Tipo de dados:', typeof dados);
          console.log('É array?', Array.isArray(dados));
          console.log('Tamanho:', dados?.length);
          console.log('Demandas recebidas:', dados);
          this.demands = dados;
        },
        error: (err) => {
          console.error('Erro ao carregar demandas:', err);
        },
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
