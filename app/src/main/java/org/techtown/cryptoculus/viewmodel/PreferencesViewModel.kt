package org.techtown.cryptoculus.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.repository.RepositoryImpl
import org.techtown.cryptoculus.repository.model.CoinInfo

class PreferencesViewModel(application: Application) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val coinInfos = MutableLiveData<ArrayList<CoinInfo>>()
    private val checkAll = MutableLiveData<Boolean>()

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PreferencesViewModel(application) as T
        }
    }

    init {
        getData()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
    
    // 첫 실행이면 당연히 DB에 저장된 게 없다
    // 근데 첫 실행 아니어도 못 불러온다
    private fun getData() = addDisposable(repository.getAllAsSingle()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
            for (i in it.indices) {
                if (i < it.size - 1 && !it[i].coinViewCheck) {
                    checkAll.value = false
                    break
                }
                if (i == it.size - 1 && it[i].coinViewCheck)
                    checkAll.value = true
            }
            ArrayList(it)
        }
        .subscribe(
            {
                coinInfos.value = it
            },
            { println("onError: ${it.message}") }
        ))

    fun getCoinInfos() = coinInfos

    fun getCheckAll() = checkAll

    // 전체 체크 눌렀을 때
    @RequiresApi(Build.VERSION_CODES.N)
    fun changeCoinViewCheckAll(coinInfos: ArrayList<CoinInfo>, checkAll: Boolean): ArrayList<CoinInfo> {
        coinInfos.replaceAll {
            it.coinViewCheck = !checkAll
            it
        }
        this.checkAll.value = !checkAll
        updateAll(!checkAll)

        return coinInfos
    }

    fun updateCoinViewCheck(coinName: String) {
        update(coinName)

        if (checkAll.value!!)
            checkAll.value = false

        else {
            val coinViewChecks = repository.getCoinViewChecks()

            for (i in coinViewChecks.indices) {
                if (i < coinViewChecks.size - 1 && !coinViewChecks[i])
                    break
                if (i == coinViewChecks.size - 1 && coinViewChecks[i])
                    checkAll.value = true
            }
        }
    }

    private fun update(coinName: String) = repository.updateCoinViewCheck(coinName)

    private fun updateAll(checkAll: Boolean) = repository.updateCoinViewCheckAll(checkAll)

    private fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    private fun println(data: String) = Log.d("PreferencesViewModel", data)
}