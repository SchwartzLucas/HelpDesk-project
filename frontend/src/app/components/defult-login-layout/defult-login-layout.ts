import {Component, Input} from '@angular/core';

@Component({
  imports: [],
  selector: 'app-defult-login-layout',
  styleUrl: './defult-login-layout.scss',
  templateUrl: './defult-login-layout.html',
})
export class DefultLoginLayout {
  @Input() title: string = "";
  @Input() primaryBtnText: string = "";
  @Input() secondaryBtnText: string = "";
}
