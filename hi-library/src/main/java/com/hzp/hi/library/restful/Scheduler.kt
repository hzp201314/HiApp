package com.hzp.hi.library.restful

import com.hzp.hi.library.cache.HiStorage
import com.hzp.hi.library.executor.HiExecutor
import com.hzp.hi.library.log.HiLog
import com.hzp.hi.library.restful.annotation.CacheStrategy
import com.hzp.hi.library.util.MainHandler

/**
 * 代理CallFactory创建出来的call对象，从而实现拦截器的派发动作
 */
class Scheduler(
    private val callFactory: HiCall.Factory,
    private val interceptors: MutableList<HiInterceptor>
) {

    fun newCall(request: HiRequest): HiCall<*> {
        val newCall: HiCall<*> = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    internal inner class ProxyCall<T>(
        private val delegate: HiCall<T>,
        private val request: HiRequest
    ) : HiCall<T> {
        override fun execute(): HiResponse<T> {
            dispatchInterceptor(request, null)

            if(request.cacheStrategy == CacheStrategy.CACHE_FIRST){
                val cacheResponse =readCache<T>()
                if(cacheResponse.data!=null){
                    return cacheResponse
                }
            }

            val response = delegate.execute()

            saveCacheIfNeed(response)

            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callback: HiCallback<T>) {
            dispatchInterceptor(request, null)

            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                HiExecutor.execute(runnable = Runnable {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        //抛到主线程里面
                        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
                            callback.onSuccess(cacheResponse)
                        })
                        HiLog.d("enqueue,cache:${request.getCacheKey()}")

                    }
                })
            }

            delegate.enqueue(object : HiCallback<T> {
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request, response)

                    saveCacheIfNeed(response)

                    callback.onSuccess(response)
                }

                override fun onFailed(throwable: Throwable) {
                    callback.onFailed(throwable)
                }
            })
        }

        private fun saveCacheIfNeed(response: HiResponse<T>) {
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST || request.cacheStrategy == CacheStrategy.NET_CACHE) {
                if (response.data != null) {
                    HiExecutor.execute(runnable = Runnable {
                        HiStorage.saveCache(request.getCacheKey(), response.data)
                    })
                }
            }
        }

        private fun <T> readCache(): HiResponse<T> {
            //hiStorage查询缓存需要提供一个cache_key
            //方案一:request的url+参数
            //方案二:CacheStrategy注解定义时扩展一个参数cacheKey
            val cacheKey = request.getCacheKey()
            val cache = HiStorage.getCache<T>(cacheKey)
            val cacheResponse = HiResponse<T>()
            cacheResponse.data = cache
            cacheResponse.code = HiResponse.CACHE_SUCCESS
            cacheResponse.msg = "缓存获取成功"
            return cacheResponse
        }


        private fun dispatchInterceptor(request: HiRequest, response: HiResponse<T>?) {
            if (interceptors.size <= 0) {
                return
            }

            InterceptorChain(request, response).dispatch()
        }

        internal inner class InterceptorChain(
            private val request: HiRequest,
            private val response: HiResponse<T>?
        ) : HiInterceptor.Chain {
            //代表的是 分发的第几个拦截器
            var callIndex: Int = 0

            //是否在网络发起之前
            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): HiRequest {
                return request
            }

            override fun response(): HiResponse<*>? {
                return response
            }

            fun dispatch() {
                val interceptor = interceptors[callIndex]
                val intercept = interceptor.intercept(this)

                callIndex++
                //继续下一次分发
                if (!intercept && callIndex < interceptors.size) {
                    dispatch()
                }
            }
        }

    }


}