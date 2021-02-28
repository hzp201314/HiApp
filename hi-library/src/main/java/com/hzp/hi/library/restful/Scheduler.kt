package com.hzp.hi.library.restful

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

            val response = delegate.execute()

            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callback: HiCallback<T>) {
            dispatchInterceptor(request,null)

            delegate.enqueue(object :HiCallback<T>{
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request,response)

                    callback.onSuccess(response)
                }

                override fun onFailed(throwable: Throwable) {
                    callback.onFailed(throwable)
                }
            })
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