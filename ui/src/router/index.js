// 导入用来创建路由和确定路由模式的两个方法
import {
    createRouter,
    createWebHistory
} from 'vue-router'
import store from '@/store'
import storage from '@/utils/storage'

/**
 * 定义路由信息
 * 
 */
const routes = [{
    name: 'login',
    path: '/login',
    component: () => import('@/components/login'),
},
{
    name: 'main',
    path: '/main',
    component: () => import('@/components/main'),
    children: [
        {
            name: 'user',
            path: '/user',
            component: () => import('@/components/system/user')
        }
    ]
}]

// 创建路由实例并传递 `routes` 配置
// 我们在这里使用 html5 的路由模式，url中不带有#，部署项目的时候需要注意。
const router = createRouter({
    history: createWebHistory(),
    routes,
})


// 全局的路由守卫 会在每一次路由进行跳转的时候执行
router.beforeEach((to) => {
    // 每次进行路由都判断一下是否要去登录, 如果没有登录则路由到登录界面

    if (to.name === 'login') {
        return true
    }
    // 如果已经登录了 那么就放行
    
    if (!store.getters.isLogin) {
        // 可能没有登录  也可能因为页面刷新 vuex 丢失了数据
        // 去storage中查看 到底有没有登陆
        
        if (!storage.getSesstionObject("loginUser")) {
            // 真的没有登录
            router.push({ name: 'login'})
        } else {
            // 从 storage 中取出数据恢复 vuex中的数据
            store.dispatch("RECOVER_USER")
            store.dispatch("GET_INFO")
        }
    }

    return true
})

// 讲路由实例导出
export default router