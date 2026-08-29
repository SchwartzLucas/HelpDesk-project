import { Component } from '@angular/core';
import {DefultLoginLayout} from '../../components/defult-login-layout/defult-login-layout';


@Component({
  imports: [
    DefultLoginLayout
  ],
  selector: 'app-login',
  styleUrl: './login.scss',
  templateUrl: './login.html',
})
export class Login {}
