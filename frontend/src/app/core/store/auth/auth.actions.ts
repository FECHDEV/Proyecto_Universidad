import { createActionGroup, props, emptyProps } from '@ngrx/store';
import { LoginRequest, AuthResponse } from '../../models/auth.model';

export const authActions = createActionGroup({
  source: 'Auth',
  events: {
    Login: props<{ credentials: LoginRequest }>(),
    'Login Success': props<{ response: AuthResponse }>(),
    'Login Failure': props<{ error: string }>(),
    Logout: emptyProps(),
    'Load User': emptyProps(),
  },
});
