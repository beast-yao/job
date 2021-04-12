import * as http from './http'

export const getServices = () => {
  return http.GET('/distro');
}

export const getTask = (params) => {
  return http.GET('/task/page', params);
}

export const addTask = (data) => {
  return http.POST('/task', data)
}
