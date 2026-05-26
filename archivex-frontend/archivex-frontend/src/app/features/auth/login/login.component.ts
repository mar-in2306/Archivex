import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AppError } from '../../../core/models/api.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="login">
      <div class="login__card">
        <div class="login__brand">
          <div class="login__logo">A</div>
          <span class="login__name">Archivex</span>
        </div>

        <h1 class="login__title">Inicia sesion</h1>
        <p class="login__subtitle">Accede al gestor documental de tu organizacion</p>

        @if (errorMessage(); as msg) {
          <div class="alert alert--danger" role="alert">{{ msg }}</div>
        }

        <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
          <div class="field">
            <label for="email">Correo electronico</label>
            <input id="email" type="email" formControlName="email" autocomplete="username" placeholder="nombre@correo.com" [class.input--error]="invalid('email')" />
            @if (invalid('email')) {
              <span class="field__error">Ingresa un correo valido.</span>
            }
          </div>

          <div class="field">
            <label for="password">Contrasena</label>
            <input id="password" type="password" formControlName="password" autocomplete="current-password" placeholder="********" [class.input--error]="invalid('password')" />
            @if (invalid('password')) {
              <span class="field__error">Ingresa tu contrasena.</span>
            }
          </div>

          <button type="submit" class="btn btn-primary login__submit" [disabled]="loading()">
            {{ loading() ? 'Ingresando...' : 'Ingresar' }}
          </button>
        </form>

        <p class="login__forgot">
          Si aun no tienes usuario, <a routerLink="/auth/register">registra una organizacion</a>.
        </p>
      </div>
    </div>
  `,
  styles: [`
    .login { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--color-bg-secondary); padding: 24px; }
    .login__card { width: 100%; max-width: 380px; background: var(--color-bg-primary); border: 1px solid var(--color-border-tertiary); border-radius: var(--radius-lg); padding: 32px 28px; }
    .login__brand { display: flex; align-items: center; gap: 8px; justify-content: center; margin-bottom: 22px; }
    .login__logo { width: 28px; height: 28px; border-radius: 6px; background: var(--color-info-bg); color: var(--color-info-text); display: flex; align-items: center; justify-content: center; font-weight: 700; }
    .login__name { font-size: var(--fs-lg); font-weight: 600; }
    .login__title { font-size: var(--fs-xl); text-align: center; }
    .login__subtitle { font-size: var(--fs-base); color: var(--color-text-secondary); text-align: center; margin-top: 4px; margin-bottom: 22px; }
    .field { margin-bottom: 14px; }
    .field input.input--error { border-color: var(--color-danger-text); }
    .field__error { display: block; margin-top: 4px; font-size: var(--fs-xs); color: var(--color-danger-text); }
    .login__submit { width: 100%; justify-content: center; margin-top: 6px; }
    .login__submit:disabled { opacity: 0.7; cursor: not-allowed; }
    .login__forgot { display: block; text-align: center; margin-top: 16px; font-size: var(--fs-sm); color: var(--color-text-secondary); }
    .alert { padding: 10px 12px; border-radius: var(--radius-md); font-size: var(--fs-sm); margin-bottom: 16px; }
    .alert--danger { background: var(--color-danger-bg); color: var(--color-danger-text); border: 1px solid var(--color-danger-border); }
  `],
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  invalid(control: keyof typeof this.form.controls): boolean {
    const c = this.form.controls[control];
    return c.invalid && (c.touched || c.dirty);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/dashboard';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.errorMessage.set(err.status === 401 ? 'Credenciales incorrectas. Verifica tus datos.' : err.message);
      },
    });
  }
}
