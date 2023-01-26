import axios from 'axios'
import store from '@/store'

// 创建axios实例
const request = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: '',
    // 超时
    timeout: 10000,
    // 设置Content-Type，规定了前后端的交互使用json
    headers: {'Content-Type': 'application/json;charset=utf-8'}
})

request.interceptors.request.use(
    function(config) {
        if (store.state.user.token) {
            // 让每一个请求携带自定义的token
            config.headers['Authorization'] = store.state.user.token
            config.headers['username'] = store.state.user.username
        }
        return config   
    },
    function(error) {
        return Promise.reject(error)
})

export default request