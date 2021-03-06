package com.github.j5ik2o.akka.persistence.s3.base.utils

import java.time.{ Duration => JavaDuration }

import com.github.j5ik2o.akka.persistence.s3.base.config.S3ClientConfig
import software.amazon.awssdk.http.Protocol
import software.amazon.awssdk.http.nio.netty.{ Http2Configuration, NettyNioAsyncHttpClient, SdkEventLoopGroup }

object HttpClientBuilderUtils {

  def setup(clientConfig: S3ClientConfig): NettyNioAsyncHttpClient.Builder = {
    val result = NettyNioAsyncHttpClient.builder()
    clientConfig.maxConcurrency.foreach(v => result.maxConcurrency(v))
    clientConfig.maxPendingConnectionAcquires.foreach(v => result.maxPendingConnectionAcquires(v))
    clientConfig.readTimeout.foreach(v => result.readTimeout(JavaDuration.ofMillis(v.toMillis)))
    clientConfig.writeTimeout.foreach(v => result.writeTimeout(JavaDuration.ofMillis(v.toMillis)))
    clientConfig.connectionTimeout.foreach(v => result.connectionTimeout(JavaDuration.ofMillis(v.toMillis)))
    clientConfig.connectionAcquisitionTimeout.foreach(v =>
      result.connectionAcquisitionTimeout(JavaDuration.ofMillis(v.toMillis))
    )
    clientConfig.connectionTimeToLive.foreach(v => result.connectionTimeToLive(JavaDuration.ofMillis(v.toMillis)))
    clientConfig.maxIdleConnectionTimeout.foreach(v => result.connectionMaxIdleTime(JavaDuration.ofMillis(v.toMillis)))
    clientConfig.useConnectionReaper.foreach(v => result.useIdleConnectionReaper(v))
    clientConfig.useHttp2.foreach(v =>
      if (v) result.protocol(Protocol.HTTP2)
      else result.protocol(Protocol.HTTP1_1)
    )
    val http2Builder = Http2Configuration.builder()
    clientConfig.http2MaxStreams.foreach(v => http2Builder.maxStreams(v))
    clientConfig.http2InitialWindowSize.foreach(v => http2Builder.initialWindowSize(v))
    clientConfig.http2HealthCheckPingPeriod.foreach(v =>
      http2Builder.healthCheckPingPeriod(JavaDuration.ofMillis(v.toMillis))
    )
    result.http2Configuration(http2Builder.build())
    clientConfig.threadsOfEventLoopGroup.foreach(v =>
      result.eventLoopGroup(
        SdkEventLoopGroup.builder().numberOfThreads(v).build()
      )
    )
    result
  }

}
