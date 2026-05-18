import type { CreateAxiosDefaults } from "axios";

export const BASE_URL = "http://localhost:8080/api";

export const configAxios: CreateAxiosDefaults = {
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
}