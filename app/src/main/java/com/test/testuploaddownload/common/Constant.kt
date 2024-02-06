package com.test.testuploaddownload.common

import com.test.testuploaddownload.BuildConfig

object Constant {
    //only used for testing
    const val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3NlcmlhbG51bWJlciI6IjQiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoi2YHYsdmK2YIg2KzZiNiv2Kkg2YXYr9ix2LPYqSDZhdi02KrZh9ixINin2YTYq9in2YbZiNmK2Kkg2KfZhNiy2LHYp9i52YrYqSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL2VtYWlsYWRkcmVzcyI6Im1vc2h0b2hvci1xdWFsaXR5QGdpei1pbnNwZWN0aW9uLmNvbSIsIlNjaG9vbElkIjoiMSIsImV4cCI6MTcwNzMwOTA0OSwiaXNzIjoiYXBpLmVkdWluc3BlY3Rpb24uY29tIiwiYXVkIjoiYXBpLmVkdWluc3BlY3Rpb24uY29tIn0.K4GVhLBjJ0zHATt3_351cS4JtvNGN6ISbxk0Zvr0jb4"

    object Api {
        private const val PRODUCTION_HOST ="https://edu-inspection-api.demoday.us"
        private const val TEST_HOST = "https://edu-inspection-api.demoday.us"

        var BASE_PATH = if (BuildConfig.DEBUG)  TEST_HOST else PRODUCTION_HOST
        var UPLOAD_ATTACHMENT_PATH = "$BASE_PATH/api/v1/school/user/createAttachment"

        const val PREFIX_TOKEN = "bearer "
        const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
        const val SESSION_ID = "Authorization"
        const val ACCESS_KEY = "x-qms-access-key"

        var GET_ATTACHMENT_PATH = "$BASE_PATH/api/v1/inspection/user/getAttachment"

        const val DOWNLOAD_TAG = "downloadGIZFile"
        fun getAttachmentUrl(id: Int): String {
            return "${GET_ATTACHMENT_PATH}?id=${id}"
        }
    }

    const val DOWNLOADS_DIR = "/storage/emulated/0/Download"
}