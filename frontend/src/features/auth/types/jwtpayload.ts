export interface AccessTokenPayload {
    sub: string;       // username
    username: string;
    iss: string;
    iat: number;
    exp: number;
    jti: string;

    type: 'access';
    // user_id: number;
}

export interface RefreshTokenPayload {
    sub: string;
    username: string;
    iss: string;
    iat: number;
    exp: number;
    jti: string;

    type: 'refresh';
}

export type JwtPayload = AccessTokenPayload | RefreshTokenPayload;