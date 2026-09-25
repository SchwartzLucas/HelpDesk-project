import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../../services/users/user-service';
import {DEMAND_STATUS} from '../../../constants/demand-status';

export interface Demand {
  public_id: string;
  public_code: string;
  title: string;
  description: string;
  user_id: string | null;
  user_name: string | null;
  status: string | null;
  create_time: string;
}

export interface Status {
  name: string,
  value: number
}

export interface User {
  user_id: string;
  user_name: string;
}

@Component({
  imports: [
    FormsModule
  ],
  selector: 'app-demand-edit-layout',
  styleUrl: './demand-edit-layout.scss',
  templateUrl: './demand-edit-layout.html',
})
export class DemandEditLayout implements OnInit {

  constructor(
    private userService: UserService
  ) {
  }

  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  statuses: Status[] = DEMAND_STATUS;

  demand: Demand | null = null;
  users: User[] = [];

  carregandoUsuarios = false;

  carregandoStatus = false;

  ngOnInit(): void {
    const publicId = this.route.snapshot.paramMap.get('publicId');

    console.log('1 - PUBLIC ID:', publicId);

    if (!publicId) {
      return;
    }

    this.http
      .get<Demand>(`http://localhost:8080/demand/${publicId}`)
      .subscribe({
        next: (demanda) => {
          console.log('2 - RESPOSTA API:', demanda);

          this.demand = demanda;

          console.log('3 - DEMAND:', this.demand);
        },
        error: (err) => {
          console.error('ERRO API:', err);
        }
      });
  }

  carregarUsuarios(): void {
    this.carregandoUsuarios = true;

    this.userService.carregarUsuarios().subscribe({
      next: (dados) => {
        this.users = dados;
        this.carregandoUsuarios = false;
      },
      error: (err) => {
        console.error('Erro ao carregar usuários:', err);
        this.carregandoUsuarios = false;
      }
    });
  }

  statusDisponiveis: Status[] = [];

  carregarDemandStatus(): void {
    this.carregandoStatus = true;

    if (!this.demand?.status) {
      this.statusDisponiveis = this.statuses;
    } else {
      const statusAtual = Number(this.demand.status);

      this.statusDisponiveis = this.statuses.filter(
        status => status.value !== statusAtual
      );
    }

    this.carregandoStatus = false;
  }


}
