export default {
    saveSessionString(key, vaule) {
        window.sessionStorage.setItem(key, vaule)
    },
    getSessionString(key) {
        return window.sessionStorage.getSessionString(key)
    },
    saveSessionStorageObject(key, vaule) {
        window.sessionStorage.setItem(key, JSON.stringify(vaule))
    },
    getSesstionObject(key) {
        return JSON.parse(window.sessionStorage.getItem(key))
    },
    remove(key) {
        return window.sessionStorage.removeItem(key)
    },

}