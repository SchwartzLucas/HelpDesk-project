import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/users/user-service';
import { DEMAND_STATUS } from '../../../constants/demand-status';

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
  name: string;
  value: number;
}

export interface User {
  user_id: string;
  user_name: string;
}

@Component({
  imports: [FormsModule],
  selector: 'app-demand-edit-layout',
  styleUrl: './demand-edit-layout.scss',
  templateUrl: './demand-edit-layout.html',
})
export class DemandEditLayout implements OnInit {

  constructor(private userService: UserService) {}

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  statuses: Status[] = DEMAND_STATUS;

  demand = signal<Demand | null>(null);
  users = signal<User[]>([]);
  carregandoUsuarios = signal(false);
  carregandoStatus = signal(false);
  statusDisponiveis = signal<Status[]>([]);
  publicId: string | null = null;
  ngOnInit(): void {
    this.publicId = this.route.snapshot.paramMap.get('publicId');

    if (!this.publicId) {
      return;
    }

    this.http
      .get<Demand>(`http://localhost:8080/demand/${this.publicId}`)
      .subscribe({
        next: (demanda) => {
          this.demand.set(demanda);
          this.carregarDemandStatus();
        },
        error: (err) => {
          console.error('ERRO API:', err);
        }
      });
  }

  carregarUsuarios(): void {
    this.carregandoUsuarios.set(true);

    this.userService.carregarUsuarios().subscribe({
      next: (dados) => {
        this.users.set(dados);
        this.carregandoUsuarios.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar usuários:', err);
        this.carregandoUsuarios.set(false);
      }
    });
  }

  carregarDemandStatus(): void {
    this.carregandoStatus.set(true);

    const atual = this.demand()?.status;

    if (!atual) {
      this.statusDisponiveis.set(this.statuses);
    } else {
      const statusAtual = Number(atual);
      this.statusDisponiveis.set(
        this.statuses.filter(status => status.value !== statusAtual)
      );
    }

    this.carregandoStatus.set(false);
  }


  fecharJanelaEditar(): void {
    this.router.navigate(['/demands']);
  }

  salvarDemanda(): void {
    const atual = this.demand();

    if (!atual || !this.publicId) {
      return;
    }

    const editedValues = {
      title: atual.title,
      description: atual.description,
      status: atual.status,
      user_id: atual.user_id
    };

    this.http
      .post(`http://localhost:8080/demand/update/${this.publicId}`, editedValues)
      .subscribe({
        next: () => {
          this.router.navigate(['/demands']);
        },
        error: (err) => {
          console.error('Erro ao salvar demanda:', err);
        }
      });
  }
}
