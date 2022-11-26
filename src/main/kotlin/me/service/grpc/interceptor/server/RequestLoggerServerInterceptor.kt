package me.service.grpc.interceptor.server

import io.grpc.*
import io.quarkus.grpc.GlobalInterceptor
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicReference
import javax.enterprise.context.ApplicationScoped
import kotlin.collections.ArrayList

@GlobalInterceptor
@ApplicationScoped
class RequestLoggerServerInterceptor : ServerInterceptor {
    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>, headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val requestLogger = RequestLogger(call.methodDescriptor, false)
        requestLogger.setRequestMetadata(headers)
        val delegate: ServerCall.Listener<ReqT> = try {
            next.startCall(object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                override fun sendMessage(message: RespT) {
                    requestLogger.addResponseMessage(message)
                    super.sendMessage(message)
                }

                override fun sendHeaders(headers: Metadata) {
                    requestLogger.setResponseMetadata(headers)
                    super.sendHeaders(headers)
                }

                override fun close(status: Status?, trailers: Metadata?) {
                    requestLogger.setStatus(status)
                    super.close(status, trailers)
                }
            }, headers)
        } catch (e: Exception) {
            requestLogger.logException(e)
            throw e
        }
        return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            override fun onMessage(message: ReqT) {
                requestLogger.addRequestMessage(message)
                super.onMessage(message)
            }

            override fun onComplete() {
                requestLogger.logCompleted()
                super.onComplete()
            }

            override fun onCancel() {
                requestLogger.logCancelled()
                super.onCancel()
            }
        }
    }

    class RequestLogger<ReqT, RespT>(
        private val method: MethodDescriptor<ReqT, RespT>,
        outbound: Boolean
    ) {
        private val requestKey: String
        private val responseKey: String
        private val requestMessages: MutableCollection<ReqT> = ConcurrentLinkedDeque()
        private val responseMessages: MutableCollection<RespT> = ConcurrentLinkedDeque()
        private val requestMetadata = AtomicReference<Metadata>()
        private val responseMetadata = AtomicReference<Metadata>()
        private val statusMap: MutableMap<String, String> = mutableMapOf()

        init {
            requestKey = if (outbound) "outbound" else "inbound"
            responseKey = if (outbound) "inbound" else "outbound"
        }

        fun addRequestMessage(message: ReqT) {
            requestMessages.add(message)
        }

        fun addResponseMessage(message: RespT) {
            responseMessages.add(message)
        }

        fun setRequestMetadata(metadata: Metadata) {
            requestMetadata.set(metadata)
        }

        fun setResponseMetadata(metadata: Metadata) {
            responseMetadata.set(metadata)
        }

        fun setStatus(status: Status?) {
            if (status != null) {
                statusMap["code"] = status.code.name
                status.description?.let { statusMap["description"] = it }
            }
        }

        fun logCompleted() {
            log.info("Completed {} call to method {}.", *requestContext(requestKey, method.fullMethodName))
        }

        fun logCancelled() {
            log.warn("Cancelled {} call to method {}.", *requestContext(requestKey, method.fullMethodName))
        }

        fun logException(e: Exception) {
            log.error(
                "Exception {} call to method {}: {}",
                *requestContext(requestKey, method.fullMethodName, e.message!!, e)
            )
        }

        private fun requestContext(vararg firstArguments: Any): Array<Any> {
            val context = ArrayList<Any>(firstArguments.size + 5)
            Collections.addAll(context, *firstArguments)
            return context.toTypedArray()
        }

        companion object {
            private val log = LoggerFactory.getLogger(RequestLogger::class.java)
        }
    }
}
