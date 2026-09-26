import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {UserService} from '../../../services/users/user-service';

export interface User {
  user_id: string;
  user_name: string;
}


@Component({
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  selector: 'app-demand-create-layout',
  styleUrl: './demand-create-layout.scss',
  templateUrl: './demand-create-layout.html',
})
export class DemandCreateLayout {

  constructor(
    private userService: UserService
  ) {
  }
  private http = inject(HttpClient);
  private router = inject(Router);
  users: User[] = [];
  carregandoUsuarios = false;
  usuariosCarregados = false;
  newDemand = {
    title: '', description: '', user_id: null as string | null, demand_status_code: ''
  };

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
          this.router.navigate(["/demands"])
        }, error: (err) => {
          console.error('Erro ao criar demanda:', err);
        }
      });
  }

  fecharJanelaCriar(): void {
    this.router.navigate(["/demands"])
  }

  carregarUsuarios(): void {
    this.userService.carregarUsuarios().subscribe({
      next: (dados) => {
        this.users = dados;
      }
    });
  }

}
