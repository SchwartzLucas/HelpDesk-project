import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';

export interface Demand {
  public_id: string;
  public_code: string;
  title: string;
  description: string;
  user_id: string | null;
  user_name: string | null;
  demand_status_code: string | null;
  create_time: string;
}


@Component({
  imports: [
    FormsModule
  ],
  selector: 'app-demand-edit-layout',
  styleUrl: './demand-edit-layout.scss',
  templateUrl: './demand-edit-layout.html',
})
export class DemandEditLayout {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  demand: Demand | null = null;

  ngOnInit() {
    const publicId = this.route.snapshot.params['publicId'];
    if (!publicId) {
      return;
    }

    this.carregarDemanda(publicId);
  }

  carregarDemanda(publicId: string): void {
    this.http
      .get<Demand>(`http://localhost:8080/demand/${publicId}`)
      .subscribe({
        next: (demanda) => {
          console.log('Demanda:', demanda);

          this.demand = demanda;
        },
        error: (err) => {
          console.error('Erro ao carregar demanda:', err);
        }
      });
  }
}
