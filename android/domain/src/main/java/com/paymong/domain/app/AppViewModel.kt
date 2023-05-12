package com.paymong.domain.app

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paymong.common.code.MongCode
import com.paymong.common.code.MapCode
import com.paymong.common.code.MongStateCode
import com.paymong.data.model.request.AddMongReqDto
import com.paymong.data.model.response.ManagementRealTimeResDto
import com.paymong.data.repository.ManagementRepository
import com.paymong.data.repository.InformationRepository
import com.paymong.data.repository.MemberRepository
import com.paymong.domain.entity.Mong
import com.paymong.domain.entity.MongInfo
import com.paymong.domain.entity.MongStats
import com.paymong.domain.watch.socket.ManagementSocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class AppViewModel(application: Application) : AndroidViewModel(application) {
    // 몽 생성
    var mongname by mutableStateOf("")
    var mongsleepStart by mutableStateOf("")
    var mongsleepEnd by mutableStateOf("")

    // 메인화면
    var point by mutableStateOf(0L)
    var mapCode by mutableStateOf(MapCode.MP000)
    var isClick by mutableStateOf(false)

    // 몽 정보
    var mong by mutableStateOf(Mong())
    var undomong by mutableStateOf(MongCode.CH000)
    var stateCode by mutableStateOf(MongStateCode.CD000)
    var poopCount by mutableStateOf(0)

    var retry by mutableStateOf(false)

    // socket
    private lateinit var socketJob : Job
    private lateinit var managementSocketService: ManagementSocketService

    private val memberRepository: MemberRepository = MemberRepository()
    private val informationRepository: InformationRepository = InformationRepository()
    private val managementRepository: ManagementRepository = ManagementRepository()

    // 메인화면 진입시 초기화
    init {
        viewModelScope.launch(Dispatchers.Main) {
            findMong()
            findPoint()
            managementSocketService = ManagementSocketService()
            managementSocketService.init(listener)
        }
    }
    private val listener: WebSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val managementRealTimeResDto = Gson().fromJson(text, ManagementRealTimeResDto::class.java)

                Log.e("WatchViewModel", "response : $managementRealTimeResDto")

                if (managementRealTimeResDto.code != "201") {
                    if (managementRealTimeResDto.code != "209") {
                        stateCode = MongStateCode.valueOf(managementRealTimeResDto.stateCode)
                        poopCount = managementRealTimeResDto.poopCount
                    } else {
                        mapCode = MapCode.valueOf(managementRealTimeResDto.mapCode)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 몽 생성
    fun addMong(){
        viewModelScope.launch(Dispatchers.IO) {
            managementRepository.addMong(AddMongReqDto(mongname,mongsleepStart,mongsleepEnd))
                .catch {
                    it.printStackTrace()
                }
                .collect { data ->
                    mong = Mong(
                        data.mongId,
                        data.name,
                        MongCode.valueOf(data.mongCode)
                    )
                    stateCode = MongStateCode.CD000
                }
        }
    }
    // 몽 정보
    private fun findMong() {
        viewModelScope.launch(Dispatchers.IO) {
            informationRepository.findMong()
                .catch {
                    it.printStackTrace()
                }
                .collect { data ->
                    mong = Mong(
                        data.mongId,
                        data.name,
                        MongCode.valueOf(data.mongCode)
                    )
                    stateCode = MongStateCode.valueOf(data.stateCode)
                    poopCount = data.poopCount
                    mapCode = MapCode.valueOf(data.mapCode ?: "MP000")
                }
        }
    }

    // 포인트
    private fun findPoint() {
        viewModelScope.launch(Dispatchers.IO) {
            memberRepository.findMember()
                .catch {
                    it.printStackTrace()
                }
                .collect { data ->
                    point = data.point
                }
        }
    }

    //진화
    fun evolution(){
        viewModelScope.launch(Dispatchers.IO) {
            managementRepository.evolution()
                .catch {
                    it.printStackTrace()
                }
                .collect{
                    data->
                    undomong = mong.mongCode
                    stateCode = MongStateCode.valueOf(data.stateCode)
                    mong.mongCode = MongCode.valueOf(data.mongCode)
                }
        }
    }

    //졸업
    fun graduation(){
        viewModelScope.launch(Dispatchers.IO) {
            managementRepository.graduation()
                .catch {
                    it.printStackTrace()
                }
                .collect{
                        data->
                    mong.mongCode = MongCode.valueOf(data.mongCode)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            managementSocketService.disConnect()
            socketJob.cancel()
        } catch (e: Exception) {  }
    }
}