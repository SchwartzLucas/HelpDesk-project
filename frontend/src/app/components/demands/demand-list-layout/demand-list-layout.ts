import {Component, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DatePipe} from '@angular/common';
import {ChangeDetectorRef} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

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
  selector: 'app-demand-list-layout',
  standalone: true,
  imports: [DatePipe, FormsModule],
  styleUrl: './demand-list-layout.scss',
  templateUrl: './demand-list-layout.html',
})
export class DemandListLayoutComponent {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef)
  private router = inject(Router)
  demands: Demand[] = [];
  constructor() {
    this.carregarDemandas();
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

  abrirJanelaCriar(): void {
    this.router.navigate(["/demands/create"]);
  }

  editarDemanda(publicId: string): void {
    this.router.navigate(["/demands", publicId])
  }

}
