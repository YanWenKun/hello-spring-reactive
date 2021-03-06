package com.example.demo.handler

import com.example.demo.repository.ArticleRepository
import com.example.demo.repository.UserRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

// Handler 层
// 参考： https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-fn-handler-functions
// 这个概念来自于消息处理中的： Router -> Handler -> ...
// Router 和 Handler 加在一起可以近似地理解为 Controller，只是变成了函数式风格。
// 同样，为了易于理解，这里写得很罗嗦。

@Component
class ArticleHandler(private val repository: ArticleRepository) {

    /**
     * 查找所有文章
     */
    fun findAll(request: ServerRequest): Mono<ServerResponse> {
        return repository.findAllByOrderByPostTimeDesc().collectList()
                .flatMap { articles -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(articles!!)) }
                .switchIfEmpty(ServerResponse.notFound().build())
    }

    /**
     * 根据 slug 查找文章
     */
    fun findOne(request: ServerRequest): Mono<ServerResponse> {
        val notFound = ServerResponse.notFound().build()
        val articleSlug = request.pathVariable("slug")
        val articleMono = repository.findBySlug(articleSlug)
        return articleMono
                .flatMap { article -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(article!!)) }
                .switchIfEmpty(notFound)
    }

}

@Component
class UserHandler(private val repository: UserRepository) {

    fun findAll(request: ServerRequest): Mono<ServerResponse> {
        return repository.findAllByOrderByIdAsc().collectList()
                .flatMap { users -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(users!!)) }
                .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun findOne(request: ServerRequest): Mono<ServerResponse> {
        val notFound = ServerResponse.notFound().build()
        val userName = request.pathVariable("userName")
        val userMono = repository.findByUserName(userName)
        return userMono
                .flatMap { user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(user!!)) }
                .switchIfEmpty(notFound)
    }

}
