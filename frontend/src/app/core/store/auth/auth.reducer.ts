import { createFeature, createReducer, on } from '@ngrx/store';
import { AuthResponse } from '../../models/auth.model';
import { authActions } from './auth.actions';

export interface AuthState {
  user: AuthResponse | null;
  token: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('token'),
  loading: false,
  error: null,
};

export const authFeature = createFeature({
  name: 'auth',
  reducer: createReducer(
    initialState,
    on(authActions.login, (state): AuthState => ({ ...state, loading: true, error: null })),
    on(authActions.loginSuccess, (state, { response }): AuthState => ({
      ...state,
      user: response,
      token: response.token,
      loading: false,
      error: null,
    })),
    on(authActions.loginFailure, (state, { error }): AuthState => ({
      ...state,
      user: null,
      token: null,
      loading: false,
      error,
    })),
    on(authActions.logout, (): AuthState => initialState),
  ),
});

export const { selectAuthState, selectUser, selectToken, selectLoading, selectError } = authFeature;
