import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  template: '<nav><a routerLink="/books">Biblioteca</a></nav>',
  styles: ''
})
export class NavbarComponent {}
