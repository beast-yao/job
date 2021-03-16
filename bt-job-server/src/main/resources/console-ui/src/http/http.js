/* eslint-disable quotes */
import axios from 'axios'

const instance = axios.create({
  baseURL: '/job/svc/v1',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json;charset=utf8' }
});

export const GET = async (url, param) => {
  return await instance.get(`${url}`, { params: param });
}

export const DELETE = async (url, param) => {
  return await instance.delete(`${url}`, { params: param });
}

export const POST = async (url, param, data) => {
  return await instance.post(`${url}`, { params: param, data: data });
}

export const PUT = async (url, param, data) => {
  return await instance.put(`${url}`, { params: param, data: data });
}
