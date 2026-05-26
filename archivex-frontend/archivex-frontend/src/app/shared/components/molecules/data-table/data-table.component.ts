import { Component, input } from '@angular/core';

export interface TableColumn<T> {
  key: keyof T | string;
  label: string;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  template: `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            @for (column of columns(); track column.key) {
              <th>{{ column.label }}</th>
            }
          </tr>
        </thead>
        <tbody>
          @for (row of rows(); track $index) {
            <tr>
              @for (column of columns(); track column.key) {
                <td>{{ value(row, column.key) }}</td>
              }
            </tr>
          } @empty {
            <tr>
              <td class="empty" [attr.colspan]="columns().length">Sin datos para mostrar.</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .table-wrap { overflow: auto; border: 1px solid var(--color-border-tertiary); border-radius: var(--radius-md); background: var(--color-bg-primary); }
    table { width: 100%; border-collapse: collapse; min-width: 620px; }
    th, td { text-align: left; padding: 9px 10px; border-bottom: 1px solid var(--color-border-tertiary); vertical-align: top; }
    th { font-size: var(--fs-xs); color: var(--color-text-tertiary); text-transform: uppercase; font-weight: 600; }
    td { font-size: var(--fs-base); }
    tr:last-child td { border-bottom: 0; }
    .empty { color: var(--color-text-tertiary); text-align: center; padding: 18px; }
  `],
})
export class DataTableComponent<T extends Record<string, unknown>> {
  readonly columns = input<TableColumn<T>[]>([]);
  readonly rows = input<T[]>([]);

  value(row: T, key: keyof T | string): string {
    const raw = row[key as keyof T];
    if (raw === null || raw === undefined || raw === '') return '-';
    if (typeof raw === 'boolean') return raw ? 'Si' : 'No';
    return String(raw);
  }
}
