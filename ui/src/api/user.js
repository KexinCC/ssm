import request from "@/api"

export function login(data) {
    return request({
        url: '/login',
        method: 'post',
        data: data
    })
}

export function logout() {
    return request({
        url: '/logout',
        method: 'get',
    })
}

export function listUser(data) {
    return request({
        url: '/ydlUser',
        method: 'get',
        params: data
    })
}

export function getInfo() {
    return request({
        url: '/ydlUser/getInfo',
        method: 'get',
    })
}

export function getById(id) {
    return request({
        url: '/ydlUser/' + id,
        method: 'get',
    })
}

// 新增用户
export function add(data) {
    return request({
        url: '/ydlUser',
        method: 'post',
        data: data
    })
}




// export function listUser(query) {
//     return request({
//         url: '/user',
//         method: 'get',
//         params: query
//     })
// }

// export function addUser(data) {
//     return request({
//             url: '/user',
//             method: 'post',
//             params: data
//         })
// }