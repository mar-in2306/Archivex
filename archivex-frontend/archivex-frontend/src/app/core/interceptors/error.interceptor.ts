import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { AppError } from '../models/api.model';
import { retryTransient } from '../utils/retry-transient';

/**
 * Interceptor global de errores. Responsabilidades:
 *
 *  1. Reintentar peticiones GET ante errores transitorios (5xx / red caída)
 *     con backoff exponencial. Las mutaciones (POST/PUT/PATCH/DELETE) NO se
 *     reintentan automáticamente para evitar efectos duplicados.
 *  2. Normalizar cualquier `HttpErrorResponse` a `AppError`, soportando los
 *     formatos de error más comunes de Spring Boot 3:
 *       - ProblemDetail RFC 7807  { type, title, status, detail, instance }
 *       - Error por defecto       { timestamp, status, error, message, path }
 *       - Validación @Valid       { errors: [{ field, defaultMessage }] }
 *       - Forma personalizada     { code, message, fieldErrors }
 *
 * Debe registrarse DESPUÉS del authInterceptor en `withInterceptors([...])`
 * para que el flujo de refresh token del auth interceptor reciba el error
 * ya tipado (AppError mantiene `status`, así que sigue funcionando).
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const isGet = req.method === 'GET';

  const handled$ = isGet ? next(req).pipe(retryTransient(2, 500)) : next(req);

  return handled$.pipe(
    catchError((err: unknown) => {
      // Si ya viene normalizado (otro interceptor), no lo toques.
      if (isAppError(err)) {
        return throwError(() => err);
      }
      return throwError(() => normalizeError(err, req.url));
    }),
  );
};

function isAppError(e: unknown): e is AppError {
  return (
    typeof e === 'object' &&
    e !== null &&
    'status' in e &&
    'message' in e &&
    !(e instanceof HttpErrorResponse)
  );
}

function normalizeError(err: unknown, url: string): AppError {
  if (!(err instanceof HttpErrorResponse)) {
    return {
      status: 0,
      message: 'Ocurrió un error inesperado en la aplicación.',
      path: url,
      raw: err,
    };
  }

  // Sin respuesta del servidor (red, CORS, servidor caído).
  if (err.status === 0) {
    return {
      status: 0,
      message:
        'No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.',
      path: url,
      raw: err,
    };
  }

  const body = err.error ?? {};
  const fieldErrors = extractFieldErrors(body);

  const message =
    pickMessage(body) ?? fallbackMessageByStatus(err.status) ?? err.message;

  return {
    status: err.status,
    message,
    code: typeof body?.code === 'string' ? body.code : undefined,
    fieldErrors: Object.keys(fieldErrors).length ? fieldErrors : undefined,
    path: typeof body?.path === 'string' ? body.path : url,
    raw: body,
  };
}

function pickMessage(body: any): string | undefined {
  if (typeof body === 'string' && body.trim()) return body;
  // ProblemDetail: detail/title — Spring default: message/error — custom: message
  return (
    body?.detail ??
    body?.message ??
    body?.title ??
    body?.error ??
    undefined
  );
}

function extractFieldErrors(body: any): Record<string, string> {
  const out: Record<string, string> = {};
  if (!body || typeof body !== 'object') return out;

  // Spring @Valid por defecto: { errors: [{ field, defaultMessage }] }
  if (Array.isArray(body.errors)) {
    for (const e of body.errors) {
      const field = e?.field ?? e?.objectName;
      const msg = e?.defaultMessage ?? e?.message;
      if (field && msg) out[field] = msg;
    }
  }

  // Forma personalizada: { fieldErrors: { email: "..." } }
  if (body.fieldErrors && typeof body.fieldErrors === 'object') {
    for (const [k, v] of Object.entries(body.fieldErrors)) {
      out[k] = String(v);
    }
  }

  return out;
}

function fallbackMessageByStatus(status: number): string | undefined {
  switch (status) {
    case 400:
      return 'La solicitud no es válida. Verifica los datos ingresados.';
    case 401:
      return 'Tu sesión expiró o no estás autenticado.';
    case 403:
      return 'No tienes permisos para realizar esta acción.';
    case 404:
      return 'El recurso solicitado no existe.';
    case 409:
      return 'Conflicto: el recurso ya existe o fue modificado.';
    case 413:
      return 'El archivo es demasiado grande.';
    case 422:
      return 'No se pudo procesar la solicitud.';
    case 429:
      return 'Demasiadas solicitudes. Espera un momento e inténtalo de nuevo.';
    case 500:
      return 'Error interno del servidor. Inténtalo más tarde.';
    case 503:
      return 'El servicio no está disponible temporalmente.';
    default:
      return undefined;
  }
}