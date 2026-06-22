import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AsyncPipe } from '@angular/common';
import { authActions } from '../../../core/store/auth/auth.actions';
import { selectError, selectLoading } from '../../../core/store/auth/auth.selectors';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, AsyncPipe],
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <h2>Iniciar sesión</h2>
      <input formControlName="username" placeholder="Usuario" autocomplete="username" />
      <input formControlName="password" type="password" placeholder="Contraseña" autocomplete="current-password" />
      <button type="submit" [disabled]="form.invalid || (loading$ | async)">Iniciar sesión</button>
      @if (error$ | async; as error) {
        <p class="error">{{ error }}</p>
      }
      <a routerLink="/auth/register">¿No tienes cuenta? Regístrate</a>
    </form>
  `,
  styles: `
    form { display: flex; flex-direction: column; gap: 1rem; width: 300px; }
    .error { color: red; }
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private store = inject(Store);

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  loading$ = this.store.select(selectLoading);
  error$ = this.store.select(selectError);

  onSubmit() {
    if (this.form.invalid) return;
    this.store.dispatch(authActions.login({ credentials: this.form.getRawValue() }));
  }
}
