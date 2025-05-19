package `in`.groww.ajeeshapptask.data.repository

import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.Stock
import `in`.groww.ajeeshapptask.domain.model.topGainersTopLosers.TopGainersTopLosersResponse

interface ApiRepository {

    suspend fun getTopGainersLosers(): TopGainersTopLosersResponse

    fun getTopGainers(): List<Stock>

    fun getTopLosers(): List<Stock>

    fun getMAT(): List<Stock>

}
