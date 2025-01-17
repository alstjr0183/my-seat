package com.codesoom.myseat.controllers.seats;

import com.codesoom.myseat.controllers.seats.SeatListController;
import com.codesoom.myseat.domain.Seat;
import com.codesoom.myseat.services.auth.AuthenticationService;
import com.codesoom.myseat.services.seats.SeatListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatListController.class)
@AutoConfigureRestDocs(
        uriScheme = "https", 
        uriHost = "api.codesoom-myseat.site"
)
class SeatListControllerTest {
    private static final String ACCESS_TOKEN 
            = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private static final Long ID = 1L;
    private static final int NUMBER = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private SeatListService service;

    private Seat seat;

    @Test
    @DisplayName("좌석 목록 조회 요청 테스트")
    void test() throws Exception {
        // given
        seat = Seat.builder()
                .id(ID)
                .number(NUMBER)
                .status(false)
                .build();

        given(service.seats())
                .willReturn(List.of(seat));
        
        // when
        ResultActions subject 
                = mockMvc.perform(
                        get("/seats")
                                .header("Authorization", "Bearer " + ACCESS_TOKEN));

        // then
        subject.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[0].status").value(false));

        // docs
        subject.andDo(document("seats",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("[].number").type(JsonFieldType.NUMBER).description("좌석 번호"),
                        fieldWithPath("[].status").type(JsonFieldType.BOOLEAN).description("예약 여부")
                )
        ));
    }
}
