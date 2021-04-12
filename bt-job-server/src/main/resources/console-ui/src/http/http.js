/* eslint-disable quotes */
import axios from 'axios'

const instance = axios.create({
  baseURL: '/job/svc/v1',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json;charset=utf8' }
});

instance.interceptors.response.use(function (response) {
  // 对响应数据做点什么
  if (response.data.code === 0) {
    return response.data;
  }
  this.$message({
    message: `${response.data.message}`,
    type: 'warning'
  });
  return Promise.reject(response.data.code)
}, function (error) {
  // 对响应错误做点什么
  return Promise.reject(error);
});

export const GET = async (url, param) => {
  return await instance.get(`${url}`, { params: param });
}

export const DELETE = async (url, param) => {
  return await instance.delete(`${url}`, { params: param });
}

export const POST = async (url, data, param) => {
  return await instance.post(`${url}`, data, { params: param });
}

export const PUT = async (url, data, param) => {
  return await instance.put(`${url}`, data, { params: param });
}
