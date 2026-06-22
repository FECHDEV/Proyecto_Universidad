import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-paginator',
  standalone: true,
  template: '<div>Pagina {{page()}} de {{totalPages()}}</div>',
  styles: ''
})
export class PaginatorComponent {
  page = input(0);
  totalPages = input(0);
  pageChange = output<number>();
}
