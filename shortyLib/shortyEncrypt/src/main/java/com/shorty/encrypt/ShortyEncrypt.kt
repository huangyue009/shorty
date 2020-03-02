package com.shorty.encrypt

import com.shorty.test.annotation.UnitTest


class ShortyEncrypt private constructor() {
    companion object {
        private var instance: ShortyEncrypt? = null
            get() {
                if(field == null){
                    field = ShortyEncrypt()
                    field!!.setKey(null)
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
    external fun setKey(key: String?)

    @UnitTest(intput = "#msg='aaaa', #key='121345'")
    @Synchronized
    external fun aesEncrypt(msg: String): String

    @Synchronized
    external fun aesDecrypt(msg: String): String


}
