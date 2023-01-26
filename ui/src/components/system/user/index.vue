<template>

    <el-form :inline="true" :model="queryParam" class="demo-form-inline">
        <el-form-item label="用户名:">
            <el-input v-model="queryParam.userName" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="昵称:">
            <el-input v-model="queryParam.nickName" placeholder="昵称" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
        </el-form-item>
    </el-form>

    <div class="flex">
        <el-button type="primary" :icon="Plus" @click="dialogVisible = true" />
        <el-button type="primary" :icon="Share" />
        <el-button type="primary" :icon="Delete" />
        <el-button type="primary" :icon="Search">Search</el-button>
        <el-button type="primary">
            Upload
            <el-icon class="el-icon--right">
                <Upload />
            </el-icon>
        </el-button>
    </div>
    <br>
    <el-table :data="tableData" row-key="userName" style="width: 100%">
        <el-table-column prop="userName" label="用户名" width="180" />
        <el-table-column prop="nickName" label="昵称" width="180" />
        <el-table-column prop="email" label="邮箱" />
    </el-table>
    <el-pagination background layout="prev, pager, next" @current-change="changePage" :total="total"
        :page-size="queryParam.pageSize" />

    <el-dialog v-model="dialogVisible" title="新增用户" width="30%" :before-close="handleClose">
        <el-form ref="userFormRef" :model="userForm" label-width="120px">
            <el-form-item label="用户名">
                <el-input v-model="userForm.userName" />
            </el-form-item>
            <el-form-item label="昵称">
                <el-input v-model="userForm.nickName" />
            </el-form-item>
            <el-form-item label="密码">
                <el-input v-model="userForm.password" />
            </el-form-item>
            <el-form-item label="确认密码">
                <el-input v-model="userForm.confirmPassword" />
            </el-form-item>
        </el-form>

        <span></span>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">Cancel</el-button>
                <el-button type="primary" @click="submitForm">
                    Confirm
                </el-button>
            </span>
        </template>
    </el-dialog>

</template>
  
<script setup>
import { ref } from 'vue'
import { listUser, add } from '@/api/user'
import { onMounted } from 'vue'
import { Delete, Plus, Search, Share, Upload } from '@element-plus/icons-vue'



const tableData = ref([])
const total = ref(0)
let dialogVisible = ref(false)

const queryParam = ref({
    size: 1,
    page: 0,
    userName: '',
    nickName: ''
})

let userForm = ref({
    userId: null,
    userName: '',
    nickName: '',
    password: '',
    confirmPassword: ''
})

const getList = function () {
    listUser(queryParam.value).then(res => {
        tableData.value = res.data.content
        total.value = res.data.totalElements
        queryParam.value.pageSize = res.data.size
    })
}

const changePage = async function (current) {
    queryParam.value.page = current - 1
    getList()
}

const submitForm = function() {
    // 提交 ajax 请求
    delete userForm.value.confirmPassword
    add(userForm.value).then(res => {
        console.log(res)
        dialogVisible.value = false
    })
}

onMounted(() => {
    getList()
})


const onSubmit = function () {

}



</script>
  

<style>

</style>

