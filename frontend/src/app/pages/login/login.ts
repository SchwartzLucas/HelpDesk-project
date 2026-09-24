import { Component } from '@angular/core';
import {DefultLoginLayout} from '../../components/login/defult-login-layout/defult-login-layout';


@Component({
  standalone: true,
  imports: [
    DefultLoginLayout
  ],
  selector: 'app-login',
  styleUrl: './login.scss',
  templateUrl: './login.html',
})
export class Login {}
