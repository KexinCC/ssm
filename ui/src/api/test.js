import request from "@/api/index.js"

export function test(query) {
    return request({
        url: '/test',
        method: 'get',
        params: query
    })
}
