package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicatedSourceAndTargetException;
import wooteco.subway.exception.PathNotExistsException;
import wooteco.subway.exception.SectionNotExistException;
import wooteco.subway.service.dto.PathServiceRequest;
import java.util.List;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Line savedLine;


    @BeforeEach
    void setUp() {
        savedLine = lineDao.save(new Line("2호선", "bg-green-600"));
        station1 = stationDao.save(new Station("강남역"));
        station2 = stationDao.save(new Station("역삼역"));
        station3 = stationDao.save(new Station("선릉역"));
        sectionDao.save(new Section(station1, station2, 10, savedLine));
        sectionDao.save(new Section(station2, station3, 10, savedLine));
    }

    @DisplayName("경로를 생성한다.")
    @Test
    void createPath() {
        final List<StationResponse> stationResponses = List.of(StationResponse.from(station1),
                StationResponse.from(station2),
                StationResponse.from(station3));
        final PathServiceRequest request = new PathServiceRequest(station1.getId(), station3.getId(), 20);

        assertThat(pathService.createPath(request)).usingRecursiveComparison()
                .isEqualTo(new PathResponse(stationResponses, 20, 1450));
    }

    @DisplayName("출발지와 도착지가 같은 경우 예외를 발생한다.")
    @Test
    void createPath_throwsExceptionDuplicatedSourceAndTarget() {
        final PathServiceRequest request = new PathServiceRequest(station1.getId(), station1.getId(), 15);

        assertThatThrownBy(() -> pathService.createPath(request))
                .isInstanceOf(DuplicatedSourceAndTargetException.class)
                .hasMessage("출발역과 도착역은 같을 수 없습니다.");
    }

    @DisplayName("출발역 또는 도착역이 구간에 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void createPath_throwsExceptionIfStationNotExistsInSection() {
        final Station station = stationDao.save(new Station("교대역"));
        final PathServiceRequest request = new PathServiceRequest(station1.getId(), station.getId(), 15);

        assertThatThrownBy(() -> pathService.createPath(request))
                .isInstanceOf(SectionNotExistException.class)
                .hasMessage("출발 또는 도착역에 해당하는 구간이 존재하지 않습니다.");
    }

    @DisplayName("출발역에서 도착역까지의 경로가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void createPath_throwsExceptionIfPathNotExists() {
        final Station station4 = stationDao.save(new Station("교대역"));
        final Station station5 = stationDao.save(new Station("양재역"));
        final Line line = lineDao.save(new Line("3호선", "bg-orange-600"));
        sectionDao.save(new Section(station4, station5, 10, line));
        final PathServiceRequest request = new PathServiceRequest(station1.getId(), station5.getId(), 15);

        assertThatThrownBy(() -> pathService.createPath(request))
                .isInstanceOf(PathNotExistsException.class)
                .hasMessage("출발역에서 도착역까지의 경로가 존재하지 않습니다.");
    }
}
