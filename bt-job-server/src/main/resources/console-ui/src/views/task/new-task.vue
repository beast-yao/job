<template>
    <div>
        <el-dialog :visible.sync="visible" title="新增任务" top="5vh" width="45%">
            <el-form :model="form" label-width="120px" :rules="rules" ref="form">
                <el-form-item label="任务名" prop="taskName">
                    <el-input v-model="form.taskName" maxlength="25" show-word-limit clearable placeholder="请输入任务名"/>
                </el-form-item>
                <el-form-item label="任务描述" prop="des">
                    <el-input v-model="form.des" maxlength="120" show-word-limit clearable placeholder="请输入任务描述"/>
                </el-form-item>
                <el-form-item label="服务名" prop="appName">
                    <el-input v-model="form.appName" maxlength="25" show-word-limit clearable placeholder="请输入服务名"/>
                </el-form-item>
                <el-form-item label="执行类型" prop="timeType">
                    <el-select v-model="form.timeType" placeholder="请选择">
                        <el-option label="corn 表达式" value="CORN"/>
                        <el-option label="固定频率" value="FIX_RATE"/>
                        <el-option label="延时执行" value="DELAY"/>
                        <el-option label="固定时间" value="FIX_DATE"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="执行表达式" prop="timeExpression">
                    <el-input v-model="form.timeExpression" maxlength="25" show-word-limit clearable placeholder="执行表达式"/>
                    <el-tooltip style="margin-left:20px" placement="top">
                        <div slot="content">
                            1. 执行类型为corn，请输入corn表达式<br/>
                            2. 执行类型为固定频率和延时执行，请输入毫秒数，最小1000ms<br/>
                            3. 执行类型为固定时间，请输入时间类型，格式 yyyy-MM-dd HH:mm:ss:SSS
                        </div>
                        <i class="el-icon-info"/>
                    </el-tooltip>
                </el-form-item>
                <el-form-item label="任务执行类型" prop="executeType">
                    <el-select v-model="form.executeType" placeholder="请选择">
                        <el-option label="单机执行" value="SINGLE"/>
                        <el-option label="广播执行" value="BROADCAST"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="任务类型" prop="taskType">
                    <el-select v-model="form.taskType" placeholder="请选择">
                        <el-option label="客户端" value="REMOTE_CLIENT"/>
                        <el-option label="Shell脚本" value="SHELL"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="任务元信息" prop="jobMeta">
                    <el-input v-model="form.jobMeta" maxlength="4000" show-word-limit type="textarea" :rows="5" placeholder="任务类型为脚本类型时，请填入脚本"/>
                </el-form-item>
                <el-form-item>
                    <el-button type="info" :loading="load" @click="cancel">取消</el-button>
                    <el-button type="primary" :loading="load" @click="confirm">确定</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
<script>
import { addTask } from '@/http/api'
export default {
    name: 'newTask',
    data() {
        return {
            visible: false,
            load: false,
            form: {
                taskName: '',
                des: '',
                appName: '',
                timeType: '',
                timeExpression: '',
                executeType: '',
                taskType: '',
                jobMeta: ''
            },
            rules: {
                taskName: [
                    { max: 25, message: '任务名长度最大25个字符', trigger: 'blur' }
                ],
                des: [
                    { max: 120, message: '任务描述长度最大120个字符', trigger: 'blur' }
                ],
                appName: [
                    { required: true, message: '请输入服务名', trigger: 'blur' },
                    { max: 25, message: '服务名长度最大25个字符', trigger: 'blur' }
                ],
                timeType: [
                    { required: true, message: '请选择执行类型', trigger: 'blur' }
                ],
                timeExpression: [
                    { required: true, message: '请输入执行表达式', trigger: 'blur' },
                    { max: 25, message: '执行表达式长度最大25个字符', trigger: 'blur' }
                ],
                executeType: [
                    { required: true, message: '请选择任务执行类型', trigger: 'blur' }
                ],
                taskType: [
                    { required: true, message: '请选择任务类型', trigger: 'blur' }
                ],
                jobMeta: [
                    { max: 4000, message: '任务元信息长度最大25个字符', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        confirm() {
            this.load = true;
            this.$refs.form.validate((valid) => {
                if (valid) {
                    addTask(this.form).then((result) => {
                        this.$message({
                            message: '增加任务成功',
                            type: 'success'
                        });
                        this.visible = false;
                        this.$nextTick(() => {
                            this.load = false
                            this.$refs.form.resetFields();
                        });
                    }).catch((err) => {
                        var msg = err.response.data;
                        this.$message({
                            message: `${msg.message}`,
                            type: 'error'
                        });
                    }).finally(() => {
                        this.load = false;
                    });
                } else {
                    this.load = false;
                }
            })
        },
        cancel() {
            this.load = true;
            this.visible = false;
            this.$nextTick(() => {
                this.load = false
                this.$refs.form.resetFields();
            });
        }
    }
}
</script>
<style scope>
    .el-input,.el-textarea{
        width: 85%;
    }
</style>
