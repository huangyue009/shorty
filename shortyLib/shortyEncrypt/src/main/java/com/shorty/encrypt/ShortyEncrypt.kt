package com.shorty.encrypt

import com.shorty.test.annotation.UnitTest


class ShortyEncrypt private constructor() {
    companion object {
        private var instance: ShortyEncrypt? = null
            get() {
                if(field == null){
                    field = ShortyEncrypt()
                    field!!.setDefaultKey(null)
                }

                return field
            }


        init {
            System.loadLibrary("shortyEncrypt")
        }

        fun getShortyEncrypt() :ShortyEncrypt{
            return instance!!
        }
    }


    /**
     * 设置秘钥 key, 秘钥是全局的
     */
    external fun setDefaultKey(key: String?)

    fun aesEncrypt(msg: ByteArray): ByteArray{
        return aesEncrypt(msg, null)
    }

    fun aesDecrypt(msg: ByteArray): ByteArray{
        return aesDecrypt(msg, null)
    }

    @UnitTest(intput = "#msg='aaaa', #key='121345'")
    @Synchronized
    external fun aesEncrypt(msg: ByteArray, key: String?): ByteArray

    @Synchronized
    external fun aesDecrypt(msg: ByteArray, key: String?): ByteArray


}
