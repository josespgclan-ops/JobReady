import { HttpInterceptorFn } from '@angular/common/http';

const TOKEN_KEY = 'jobready.jwt';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  const token = sessionStorage.getItem(TOKEN_KEY);
  if (!token || !request.url.includes('/api/')) return next(request);

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
