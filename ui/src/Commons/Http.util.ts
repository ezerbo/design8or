import axios, {AxiosRequestConfig, AxiosResponse} from "axios";

export const API_BASE_URL = 'http://localhost:8080'; //TODO Create a variable

export const handleErrors = (response: AxiosResponse) => {
    if (response.status !== 200) {
        console.error(`${JSON.stringify(response)}`);
        throw response;
    }
    return response;
}

export async function httpRequest<T>(config: AxiosRequestConfig): Promise<T> {
    try {
        const response: AxiosResponse<T> = await axios(config);
        return response.data;
    } catch (error: any) {
        if (error.response) {
            console.error('Server responded with an error:', error.response.data);
        } else if (error.request) {
            console.error('No response received:', error.request);
        } else {
            console.error('Error setting up request:', error.message);
        }
        throw error;
    }
}

export async function httpGet<T>(url: string): Promise<T> {
    return httpRequest({
        method: 'GET',
        url: `${url}`,
        headers: {'Accept': 'application/json'}
    });
}

export async function httpGetWithHeaders<T>(url: string): Promise<AxiosResponse<T>> {
    try {
        const response: AxiosResponse<T> = await axios({
            method: 'GET',
            url: `${url}`,
            headers: {'Accept': 'application/json'}
        });
        return response;
    } catch (error: any) {
        if (error.response) {
            console.error('Server responded with an error:', error.response.data);
        } else if (error.request) {
            console.error('No response received:', error.request);
        } else {
            console.error('Error setting up request:', error.message);
        }
        throw error;
    }
}