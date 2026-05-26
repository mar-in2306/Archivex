import { Component, input } from '@angular/core';

@Component({
  selector: 'app-status-message',
  standalone: true,
  template: `
    @if (message()) {
      <div class="status" [class]="'status status--' + type()">
        {{ message() }}
      </div>
    }
  `,
  styles: [`
    .status { padding: 10px 12px; border-radius: var(--radius-md); border: 1px solid var(--color-border-tertiary); font-size: var(--fs-sm); margin-bottom: 14px; }
    .status--error { background: var(--color-danger-bg); color: var(--color-danger-text); border-color: var(--color-danger-border); }
    .status--success { background: var(--color-success-bg); color: var(--color-success-text); }
    .status--info { background: var(--color-info-bg); color: var(--color-info-text); border-color: var(--color-info-border); }
  `],
})
export class StatusMessageComponent {
  readonly message = input<string | null>(null);
  readonly type = input<'info' | 'success' | 'error'>('info');
}
