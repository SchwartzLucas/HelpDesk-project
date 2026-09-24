import {Component, inject, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  imports: [FormsModule],
  selector: 'app-defult-login-layout',
  styleUrl: './defult-login-layout.scss',
  templateUrl: './defult-login-layout.html',
})
export class DefultLoginLayout {
  @Input() title: string = 'Login';
  @Input() primaryBtnText: string = 'Sign in';
  @Input() secondaryBtnText: string = 'Create account';

  private http = inject(HttpClient);
  private router = inject(Router);

  loginData = {
    login: '',
    password: ''
  };

  loading = false;
  errorMessage = '';


  onLogin() {
    this.loading = true;
    this.errorMessage = '';

    this.http.post('http://localhost:8080/auth/login', this.loginData)
      .subscribe({
        next: (response: any) => {
          localStorage.setItem('token', response.token);
          console.log('Login realizado com sucesso!');
          this.router.navigate(["/demands"])
        },
        error: (error) => {
          this.errorMessage = 'Login ou senha inválidos';
          console.error('Erro no login:', error);
        },
        complete: () => {
          this.loading = false;
        }
      });
  }
}
