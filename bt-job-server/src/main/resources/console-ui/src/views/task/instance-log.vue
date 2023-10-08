<template>
    <div>
        <el-dialog :visible.sync="visable">
            {{ instanceId }}
        </el-dialog>
    </div>
</template>
<script>
export default {
    props: {
        instanceId: { require: true, type: String },
        visable: { require: true, type: Boolean, default: false }
    }
}
</script>
