import axios from "axios";
import { configAxios } from "./config";
import { requestInterceptor, requestErrorInterceptor } from "./interceptors/request";
import { responseInterceptor, responseErrorInterceptor } from "./interceptors/response";

export const axiosInstance = axios.create(configAxios);

// Mount Request Interceptors
axiosInstance.interceptors.request.use(requestInterceptor, requestErrorInterceptor);

// Mount Response Interceptors
axiosInstance.interceptors.response.use(responseInterceptor, responseErrorInterceptor);
