package wooteco.subway.domain;

import wooteco.subway.domain.exception.DuplicatedStationsException;
import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Line line;

    public Section(final Long id, final Station upStation, final Station downStation, final int distance, final Line line) {
        validateDuplicatedStations(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Section(final Station upStation, final Station downStation, final int distance, final Line line) {
        this(null, upStation, downStation, distance, line);
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance, null);
    }

    public boolean isGreaterOrEqualTo(final Section other) {
        return this.distance >= other.distance;
    }

    public Section createSectionByUpStation(final Section section) {
        return new Section(this.id, this.upStation, section.upStation, calculateDistance(section), this.line);
    }

    public Section createSectionByDownStation(final Section section) {
        return new Section(this.id, section.downStation, this.downStation, calculateDistance(section), this.line);
    }

    public Section createSectionInBetween(Section section) {
        if (this.upStation.equals(section.upStation)) {
            return createSectionByDownStation(section);
        }
        return createSectionByUpStation(section);
    }

    public Section merge(final Section section) {
        return new Section(this.upStation, section.downStation, mergeDistance(section), this.line);
    }

    private void validateDuplicatedStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new DuplicatedStationsException();
        }
    }

    private int calculateDistance(final Section section) {
        return this.distance - section.distance;
    }

    private int mergeDistance(final Section section) {
        return this.distance + section.distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line=" + line +
                '}';
    }
}
