package wooteco.subway.maps.map.documentation;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;
import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;

@WebMvcTest(controllers = {MapController.class})
public class PathDocumentation extends Documentation {
    @MockBean
    private MapService mapService;

    protected TokenResponse tokenResponse;

    @BeforeEach
    public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        super.setUp(context, restDocumentation);
    }

    @DisplayName("지하철 경로를 구한다.")
    @Test
    void findPath() {
        PathResponse pathResponse = new PathResponse(Lists.newArrayList(
            new StationResponse(1L, "교대역", LocalDateTime.now(), LocalDateTime.now()),
            new StationResponse(2L, "강남역", LocalDateTime.now(), LocalDateTime.now()),
            new StationResponse(3L, "양재역", LocalDateTime.now(), LocalDateTime.now())
        ), 3, 4, 1250);

        when(mapService.findPath(any(), any(), any(), any())).thenReturn(pathResponse);

        given().log().all().
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/paths?source={sourceId}&target={targetId}&type={type}", 1, 3, "DISTANCE").
            then().
            log().all().
            apply(document("paths/find",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                    parameterWithName("source").description("출발역 번호"),
                    parameterWithName("target").description("도착역 번호"),
                    parameterWithName("type").description("경로 타입")
                ),
                responseFields(
                    fieldWithPath("stations.[]").type(JsonFieldType.ARRAY).description("지하철 경로"),
                    fieldWithPath("stations.[].id").type(JsonFieldType.NUMBER).description("역 번호"),
                    fieldWithPath("stations.[].name").type(JsonFieldType.STRING).description("역 이름"),
                    fieldWithPath("distance").type(JsonFieldType.NUMBER).description("총 비용"),
                    fieldWithPath("duration").type(JsonFieldType.NUMBER).description("총 소요 시간"),
                    fieldWithPath("fare").type(JsonFieldType.NUMBER).description("총 요금")
                ))).
            extract();
    }
}
