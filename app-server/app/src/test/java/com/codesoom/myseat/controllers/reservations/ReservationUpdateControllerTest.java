package com.codesoom.myseat.controllers.reservations;

import com.codesoom.myseat.domain.User;
import com.codesoom.myseat.dto.ReservationRequest;
import com.codesoom.myseat.exceptions.NotOwnedReservationException;
import com.codesoom.myseat.exceptions.ReservationNotFoundException;
import com.codesoom.myseat.services.auth.AuthenticationService;
import com.codesoom.myseat.services.reservations.ReservationUpdateService;
import com.codesoom.myseat.services.users.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReservationUpdateController.class)
class ReservationUpdateControllerTest {

    private static final String ACCESS_TOKEN
            = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReservationUpdateService reservationUpdateService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        User mockUser = User.builder()
                .id(1L)
                .name("김철수")
                .email("soo@email.com")
                .password("$2a$10$hxqWrlGa7SQcCEGURjmuQup4J9kN6qnfr4n7j7R3LvzHEoEOUTWeW")
                .build();

        given(authService.parseToken(ACCESS_TOKEN))
                .willReturn(1L);

        given(userService.findById(1L))
                .willReturn(mockUser);
    }

    @DisplayName("예약을 성공적으로 수정하면 204 no content를 응답한다.")
    @Test
    void PUT_reservation_update_with_204_status() throws Exception {
        ReservationRequest request = new ReservationRequest("2022-10-18", "수정 데이터");

        mockMvc.perform(put("/reservations/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("본인 소유가 아닌 예약 id가 주어지면 403 forbidden를 응답한다.")
    @Test
    void PUT_reservation_update_with_403_status() throws Exception {
        ReservationRequest request = new ReservationRequest("2022-10-18", "수정 데이터");

        doThrow(NotOwnedReservationException.class)
                .when(reservationUpdateService)
                .updateReservation(anyLong(), anyLong(), any(ReservationRequest.class));

        mockMvc.perform(put("/reservations/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("찾을 수 없는 예약 id가 주어지면 404 not found를 응답한다.")
    @Test
    void PUT_reservation_update_with_404_status() throws Exception {
        ReservationRequest request = new ReservationRequest("2022-10-18", "수정 데이터");

        doThrow(ReservationNotFoundException.class)
                .when(reservationUpdateService)
                .updateReservation(anyLong(), anyLong(), any(ReservationRequest.class));

        mockMvc.perform(put("/reservations/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
