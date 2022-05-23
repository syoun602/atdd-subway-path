package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("새로운 지하철역을 등록한다.")
    @Test
    void createStation() {
        final String name = "선릉역";
        final StationRequest stationRequest = new StationRequest(name);
        given(stationDao.save(stationRequest.toEntity())).willReturn(new Station(1L, name));

        final StationResponse actual = stationService.createStation(stationRequest);

        assertAll(
                () -> assertThat(actual.getId()).isOne(),
                () -> assertThat(actual.getName()).isEqualTo(name)
        );
    }

    @DisplayName("중복된 이름의 지하철역을 등록할 경우 예외를 발생한다.")
    @Test
    void createStation_throwsExceptionWithDuplicateName() {
        final String name = "선릉역";
        final StationRequest stationRequest = new StationRequest(name);
        given(stationDao.existByName("선릉역")).willReturn(true);

        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessage("이미 존재하는 지하철 역입니다.");
    }

    @DisplayName("등록된 모든 지하철역을 반환한다.")
    @Test
    void getAllStations() {
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("역삼역");
        final Station station3 = new Station("선릉역");
        final List<Station> expected = List.of(station1, station2, station3);

        given(stationDao.findAll()).willReturn(expected);

        assertThat(stationService.getAllStations()).usingRecursiveComparison()
                .isEqualTo(List.of(
                        StationResponse.from(station1),
                        StationResponse.from(station2),
                        StationResponse.from(station3))
                );
    }

    @DisplayName("등록된 지하철역을 삭제한다.")
    @Test
    void delete() {
        given(stationDao.deleteById(1L)).willReturn(1);
        stationService.delete(1L);
        verify(stationDao, times(1)).deleteById(1L);
    }
}
