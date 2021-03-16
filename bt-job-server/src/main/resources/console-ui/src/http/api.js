import * as http from './http'

export const getServices = () => {
  return http.GET('/distro');
}
