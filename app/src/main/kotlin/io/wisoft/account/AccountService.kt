package io.wisoft.account

import com.google.protobuf.ByteString
import io.wisoft.account.v1.GetProfileStreamResponseV1
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.io.BufferedInputStream
import java.io.File

private const val PIECE_MIN_SIZE = 100_000
private const val PIECE_SIZE = 0
private const val DELAY = 1_000L

object AccountService {

    fun signUp(account: Account) = account
    fun getProfileStream(): Flow<GetProfileStreamResponseV1> {
        val file = File(javaClass.classLoader.getResource("sample.jpg")!!.toURI())
        val fileSize = file.length()
        val fileInputService = file.inputStream().buffered()
        return flow {
            getProfileStream(fileInputService, fileSize.toInt())
        }
    }

    private suspend fun FlowCollector<GetProfileStreamResponseV1>.getProfileStream(
        fileInputStream: BufferedInputStream,
        fileSize: Int,
    ) {
        var remain = fileSize
        fileInputStream.use {
            while (remain > PIECE_MIN_SIZE) {
                val readLength = if (remain < PIECE_SIZE) remain else PIECE_SIZE
                val readBytes = it.readNBytes(readLength)
                remain -= readLength
                emitProfile(readBytes)
                delay(DELAY)
            }
        }
    }

    private suspend fun FlowCollector<GetProfileStreamResponseV1>.emitProfile(
        readBytes: ByteArray,
    ) = emit(
        GetProfileStreamResponseV1.newBuilder()
            .setData(ByteString.copyFrom(readBytes))
            .build()
    )

    fun getAccountAll(): List<Account> = (1..100).map {
        Account(id = "test_$it", password = "123", name = "name_$it")
    }

}
