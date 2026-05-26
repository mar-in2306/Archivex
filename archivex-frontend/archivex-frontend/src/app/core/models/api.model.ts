/**
 * Tipos compartidos para la comunicación con el backend Spring Boot.
 */

/**
 * Estructura de respuesta paginada de Spring Data (`Page<T>`).
 * Coincide con el JSON que serializa `org.springframework.data.domain.Page`.
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  /** Página actual (0-based, igual que Spring). */
  number: number;
  size: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/** Parámetros de paginación/orden que entiende Spring Data (`Pageable`). */
export interface PageRequest {
  /** 0-based. */
  page?: number;
  size?: number;
  /** Formato Spring: `"campo,asc"` o `"campo,desc"`. Acepta múltiples. */
  sort?: string | string[];
}

/**
 * Error normalizado de la aplicación. El `errorInterceptor` convierte
 * cualquier `HttpErrorResponse` a esta forma para que toda la app
 * maneje errores de manera uniforme.
 */
export interface AppError {
  /** Código HTTP. 0 = error de red / sin respuesta. */
  status: number;
  /** Mensaje legible para el usuario (ya en español cuando es posible). */
  message: string;
  /** Código de negocio si el backend lo envía (ej. "USER_ALREADY_EXISTS"). */
  code?: string;
  /** Errores de validación por campo: { email: "ya está en uso" }. */
  fieldErrors?: Record<string, string>;
  /** Ruta que originó el error. */
  path?: string;
  /** Respuesta cruda del servidor por si se necesita depurar. */
  raw?: unknown;
}