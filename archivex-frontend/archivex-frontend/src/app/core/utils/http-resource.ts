import { Signal, computed, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { AppError } from '../models/api.model';

/** Estado de una operación asíncrona. */
export interface AsyncState<T> {
  data: T | null;
  loading: boolean;
  error: AppError | null;
}

/**
 * Contenedor de estado reactivo para una llamada HTTP.
 *
 * Uso típico en un componente de feature (Capa 2):
 *
 *   readonly docs = new HttpResource<Page<Document>>();
 *
 *   ngOnInit() {
 *     this.docs.load(this.documentService.list());
 *   }
 *
 * En la plantilla:
 *
 *   @if (docs.loading()) { <app-spinner /> }
 *   @else if (docs.error(); as err) { {{ err.message }} }
 *   @else if (docs.data(); as page) { ...render... }
 */
export class HttpResource<T> {
  private readonly _state = signal<AsyncState<T>>({
    data: null,
    loading: false,
    error: null,
  });

  readonly state: Signal<AsyncState<T>> = this._state.asReadonly();
  readonly data = computed(() => this._state().data);
  readonly loading = computed(() => this._state().loading);
  readonly error = computed(() => this._state().error);
  readonly loaded = computed(
    () => !this._state().loading && this._state().error === null && this._state().data !== null,
  );

  /**
   * Ejecuta la fuente y actualiza el estado.
   * @param source$ Observable de una llamada de servicio.
   * @param opts.keepPreviousData Si es true, conserva los datos anteriores
   *        mientras recarga (útil para paginación/refrescos sin parpadeo).
   */
  load(source$: Observable<T>, opts: { keepPreviousData?: boolean } = {}): void {
    this._state.update((s) => ({
      data: opts.keepPreviousData ? s.data : null,
      loading: true,
      error: null,
    }));

    source$.subscribe({
      next: (data) => this._state.set({ data, loading: false, error: null }),
      error: (error: AppError) =>
        this._state.update((s) => ({
          data: opts.keepPreviousData ? s.data : null,
          loading: false,
          error,
        })),
    });
  }

  /** Actualiza los datos localmente (ej. tras un POST/PUT optimista). */
  setData(data: T): void {
    this._state.set({ data, loading: false, error: null });
  }

  reset(): void {
    this._state.set({ data: null, loading: false, error: null });
  }
}