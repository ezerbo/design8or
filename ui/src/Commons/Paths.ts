export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const GET_POOLS_URL = `${API_BASE_URL}/pools?size=100&page=0`;

export const GET_USERS_URL = `${API_BASE_URL}/users?size=10&page=0`;

export const GET_CURRENT_DESIGNATION = `${API_BASE_URL}/designations/current`;

export const acceptDesignationPath = (assignmentId: number) =>
    `${API_BASE_URL}/designations/assignments/${assignmentId}/accept`;

export const declineDesignationPath = (assignmentId: number) =>
    `${API_BASE_URL}/designations/assignments/${assignmentId}/decline`;

export const designateManuallyPath = (assignmentId: number) =>
    `${API_BASE_URL}/designations/assignments/${assignmentId}/designate`;

export const getPoolAssignmentsPath = (poolId: number) =>
    `${API_BASE_URL}/pools/${poolId}/assignments`;

export const START_NEW_POOL_URL = `${API_BASE_URL}/pools/start-new`;

export const deletePoolPath = (poolId: number) =>
    `${API_BASE_URL}/pools/${poolId}`;