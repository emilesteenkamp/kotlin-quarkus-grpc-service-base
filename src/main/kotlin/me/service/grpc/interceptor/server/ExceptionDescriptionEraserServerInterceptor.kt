package me.service.grpc.interceptor.server

import io.grpc.*
import io.quarkus.grpc.GlobalInterceptor
import javax.enterprise.context.ApplicationScoped

@GlobalInterceptor
@ApplicationScoped
class ExceptionDescriptionEraserServerInterceptor : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return next.startCall(ExceptionTranslatingServerCall(call), headers)
    }

    class ExceptionTranslatingServerCall<ReqT, RespT>(
        call: ServerCall<ReqT, RespT>
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
        override fun close(status: Status, trailers: Metadata) {
            when (status.code) {
                Status.Code.UNKNOWN -> super.close(Status.fromCode(status.code), trailers)
                else -> {}
            }

            super.close(status, trailers)
        }
    }
}
