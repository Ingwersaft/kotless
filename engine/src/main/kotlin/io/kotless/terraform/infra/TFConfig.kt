package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.utils.withIndent

/** Declaration of Terraform configuration */
class TFConfig : HCLEntity.Named() {
    override val hcl_name: String = "terraform"
    override val hcl_ref: String
        get() = hcl_name

    override fun render(): String {
        return """
            |terraform {
            |${super.render().withIndent()}
            |${(backend?.render() ?: "").withIndent()}
            |}
            """.trimMargin()
    }

    var required_version by text()

    sealed class Backend(val type: String) : HCLEntity() {
        override fun render(): String {
            return """
            |backend "$type" {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
        }


        class S3 : Backend("s3") {
            var bucket by text()
            var key by text()
            var profile by text()
            var region by text()
        }
    }

    var backend: Backend? = null
}

fun terraform(configure: TFConfig.() -> Unit) = TFConfig().apply(configure)

fun TFFile.terraform(configure: TFConfig.() -> Unit) {
    add(TFConfig().apply(configure))
}


