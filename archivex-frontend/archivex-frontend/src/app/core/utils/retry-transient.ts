import { HttpErrorResponse } from '@angular/common/http';
import { MonoTypeOperatorFunction, retry, timer } from 'rxjs';

/**
 * Reintenta solo errores transitorios (sin respuesta del servidor o 5xx)
 * con backoff exponencial. Los errores 4xx (validación, auth, etc.) NO
 * se reintentan: fallan de inmediato.
 *
 * @param maxRetries  Número de reintentos (no cuenta el intento original).
 * @param baseDelayMs Retardo base; crece 2^n (500ms → 1s → 2s ...).
 */
export function retryTransient<T>(
  maxRetries = 2,
  baseDelayMs = 500,
): MonoTypeOperatorFunction<T> {
  return retry<T>({
    count: maxRetries,
    delay: (error: unknown, retryCount: number) => {
      const isTransient =
        error instanceof HttpErrorResponse &&
        (error.status === 0 || error.status >= 500);

      // No transitorio → propaga el error sin reintentar.
      if (!isTransient) {
        throw error;
      }

      const delayMs = baseDelayMs * Math.pow(2, retryCount - 1);
      return timer(delayMs);
    },
  });
}