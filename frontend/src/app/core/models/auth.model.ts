export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  token: string;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
}
