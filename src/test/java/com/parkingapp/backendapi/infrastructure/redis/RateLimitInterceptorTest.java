package com.parkingapp.backendapi.infrastructure.redis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.parkingapp.backendapi.common.annotations.RateLimit;
import com.parkingapp.backendapi.common.annotations.WebConfig;
import com.parkingapp.backendapi.infrastructure.security.jwt.JwtTokenProvider;
import com.parkingapp.backendapi.infrastructure.security.service.UserDetailsServiceImpl; // Add this
// import
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers; // For ArgumentMatchers.startsWith
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 1. TEMPORARY TEST CONTROLLER
// The inner class is the standard pattern for @WebMvcTest.
@RestController
class TestController {
  // Secured endpoint, requires user ID and limit=1
  @RateLimit(limit = 1, timeWindowSeconds = 60, keyPrefix = "userId")
  @GetMapping("/api/secured")
  public String secured() {
    return "OK";
  }

  // Public endpoint, falls back to IP and limit=2
  @RateLimit(limit = 2, timeWindowSeconds = 30, keyPrefix = "ip")
  @GetMapping("/api/public")
  public String open() {
    return "OK";
  }

  // Unannotated endpoint, should always pass
  @GetMapping("/api/unlimited")
  public String unlimited() {
    return "OK";
  }
}

// 2. TEST CLASS SETUP
@WebMvcTest(controllers = TestController.class)
@Import({WebConfig.class, RateLimitInterceptor.class})
class RateLimitInterceptorTest {

  @Autowired private MockMvc mockMvc;

  // Mocking the dependencies to isolate the Interceptor logic
  @MockitoBean private RedisRateLimiterService rateLimiterService;
  @MockitoBean private JwtTokenProvider jwtTokenProvider;
  @MockitoBean private UserDetailsServiceImpl userDetailsService;

  // -----------------------------------------------------------
  // TEST CASES
  // -----------------------------------------------------------

  /** Test case 1: Successful request (count is under limit) */
  @Test
  @WithMockUser(username = "testUser123")
  void whenCountIsUnderLimit_shouldReturnOk() throws Exception {
    // Mock: The service returns a count of 1 (limit is 2 for /api/public)
    when(rateLimiterService.incrementAndExpire(any(), anyLong())).thenReturn(1L);

    mockMvc.perform(get("/api/public")).andExpect(status().isOk());
  }

  /**
   * Test case 2: Two different users access the same rateLimited endpoint. Both should succeed
   * because they have separate counters. Limit = 1 for this endpoint.
   */
  @Test
  void whenTwoDifferentUsersHitLimit1Endpoint_bothShouldSucceed() throws Exception {
    // Mock the service to return count 1 (indicating the first successful access)
    when(rateLimiterService.incrementAndExpire(any(), anyLong())).thenReturn(1L);

    // --- User A Access ---
    mockMvc.perform(get("/api/secured").with(user("userA"))).andExpect(status().isOk());

    // Verify key was generated using "userA"
    verify(rateLimiterService)
        .incrementAndExpire(ArgumentMatchers.startsWith("rate:userId:userA:secured"), anyLong());

    // --- User B Access ---
    mockMvc.perform(get("/api/secured").with(user("userB"))).andExpect(status().isOk());

    // Verify a SECOND call was made, and the key was generated using "userB"
    verify(rateLimiterService)
        .incrementAndExpire(ArgumentMatchers.startsWith("rate:userId:userB:secured"), anyLong());
  }

  /**
   * Test case 3: Same user makes multiple calls to same rateLimited endpoint. User should fail on
   * second call as it's the same counter. Limit = 1 for this endpoint.
   */
  @Test
  void whenUserExceedsRateLimit_ShouldFail() throws Exception {
    // Mock the service to return count 1 (indicating the first successful access)
    when(rateLimiterService.incrementAndExpire(any(), anyLong())).thenReturn(1L).thenReturn(2L);

    // --- User A Access ---
    mockMvc.perform(get("/api/secured").with(user("userA"))).andExpect(status().isOk());

    // Verify key was generated using "userA"
    verify(rateLimiterService)
        .incrementAndExpire(ArgumentMatchers.startsWith("rate:userId:userA:secured"), anyLong());

    // Repeat User A Access
    mockMvc
        .perform(get("/api/secured").with(user("userA")))
        .andExpect(status().isTooManyRequests());
  }

  /** Test case 4: No userId call should use ip as the identifier */
  @Test
  @WithMockUser(username = "anonymous", roles = "ANONYMOUS") // needed to bypass security filters
  void whenPublicEndpointIsAccessed_ShouldUseIpAsIdentifier() throws Exception {
    when(rateLimiterService.incrementAndExpire(any(), anyLong())).thenReturn(1L);

    final String TEST_IP = "192.168.1.1";

    // identifier
    mockMvc
        .perform(
            get("/api/public")
                .with(
                    request -> {
                      request.setRemoteAddr(TEST_IP);
                      return request;
                    }))
        .andExpect(status().isOk());

    // verify key has ip in identifier
    verify(rateLimiterService)
        .incrementAndExpire(
            ArgumentMatchers.startsWith("rate:ip:" + TEST_IP + ":open"), ArgumentMatchers.eq(30L));
  }

  /**
   * Test case 5: Access an endpoint with NO @RateLimit annotation. The request should succeed, and
   * the rate limiting service should never be called.
   */
  @Test
  @WithMockUser(username = "noLimitUser")
  void whenEndpointIsUnlimited_shouldAlwaysSucceedWithoutCallingService() throws Exception {
    final int NUMBER_OF_HITS = 10; // tested with 1000 as well

    for (int i = 0; i < NUMBER_OF_HITS; i++) {
      mockMvc.perform(get("/api/unlimited")).andExpect(status().isOk());
    }

    verify(rateLimiterService, never()).incrementAndExpire(any(), anyLong());
  }
}
