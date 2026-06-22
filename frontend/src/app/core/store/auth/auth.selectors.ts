import { createSelector } from '@ngrx/store';
import { authFeature } from './auth.reducer';

export const { selectAuthState, selectUser, selectToken, selectLoading, selectError } = authFeature;

export const selectIsAuthenticated = createSelector(
  selectToken,
  token => !!token,
);

export const selectIsAdmin = createSelector(
  selectUser,
  user => user?.role === 'ADMIN',
);
