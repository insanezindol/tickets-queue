package com.example.ticketsqueue.service

import com.example.ticketsqueue.dto.TicketResponse
import com.example.ticketsqueue.exception.TicketSoldOutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TicketService(
    private val redisTemplate: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    // Redis 키 상수
    companion object {
        private const val TICKET_COUNTER = "ticket:counter"
        private const val MAX_TICKETS = 1000000L
    }

    // 메모리 캐시 (중복 요청 방지)
    private val userCache = ConcurrentHashMap<String, String>()

    suspend fun allocateTicket(userId: String): TicketResponse {
        // 1. 이미 받은 사용자인지 확인
        val cached = userCache[userId]
        if (cached != null) {
            return TicketResponse(cached, getTicketNumberFromCache(userId))
        }

        // 2. Redis에서 티켓 번호 발급 (원자적 증가)
        val ticketNumber = withContext(Dispatchers.IO) {
            redisTemplate.opsForValue().increment(TICKET_COUNTER) ?: 1L
        }

        // 3. 티켓 한도 확인
        if (ticketNumber > MAX_TICKETS) {
            throw TicketSoldOutException()
        }

        // 4. UUID 생성
        val uuid = UUID.randomUUID().toString()

        // 5. 캐시에 저장 (비동기)
        withContext(Dispatchers.IO) {
            log.info("캐시에 저장 : $uuid")
            userCache[userId] = uuid
            redisTemplate.opsForValue().set("ticket:user:$userId", uuid)
            redisTemplate.opsForValue().set("ticket:uuid:$uuid", ticketNumber.toString())
        }

        return TicketResponse(uuid, ticketNumber)
    }

    private fun getTicketNumberFromCache(userId: String): Long {
        val uuid = userCache[userId] ?: return 0L
        val numberStr = redisTemplate.opsForValue().get("ticket:uuid:$uuid")
        log.info("캐시에서 가져오기 : $uuid : $numberStr")
        return numberStr?.toLongOrNull() ?: 0L
    }

}
