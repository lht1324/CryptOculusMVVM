package org.techtown.cryptoculus.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import org.techtown.cryptoculus.repository.model.CoinDao
import org.techtown.cryptoculus.repository.model.CoinDatabase
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.RetrofitClient

class CoinRepository(application: Application) {
    private val coinDao: CoinDao by lazy { CoinDatabase.getInstance(application)!!.coinDao() }
    var exchange = "coinone"

    // DB에서 가져오든 Network에서 받아오든
    // 결국 coinInfos는 바뀐다

    fun getCoinInfos(exchange: String): ArrayList<CoinInfo> {
        return RetrofitClient.getData(exchange)
        // return coinDao.getAll() as MutableLiveData<ArrayList<CoinInfo>> // 오류 가능성 존재
        // 이게 아니라 coinInfos를 해야 하는 것 아닐까
        // rx로 유닛 받는 건 안 되냐?
        // insert 하는 것처럼
        // exchange 넣는 거지
    }

    fun getCoinInfosFromDB(exchange: String): ArrayList<CoinInfo> {
        return coinDao.getAllByExchange(exchange) as ArrayList<CoinInfo>
    }

    fun insert(coinInfo: CoinInfo): Observable<Unit> {
        return Observable.fromCallable { coinDao.insert(coinInfo) }
    }

    fun update(coinInfo: CoinInfo): Observable<Unit> {
        return Observable.fromCallable { coinDao.update(coinInfo) }
    }

    fun insertAll(coinInfos: List<CoinInfo>): Observable<Unit> {
        return Observable.fromCallable { coinDao.insertAll(coinInfos) }
    }

    fun updateAll(coinInfos: List<CoinInfo>): Observable<Unit> {
        return Observable.fromCallable { coinDao.updateAll(coinInfos) }
    }
}