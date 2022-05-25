package wooteco.subway.dto;

import wooteco.subway.service.dto.PathServiceRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PathRequest {

    @NotNull(message = "출발역은 공백일 수 없습니다.")
    private Long source;

    @NotNull(message = "도착역은 공백일 수 없습니다.")
    private Long target;

    @NotNull
    @Min(value = 0, message = "나이는 공백이거나 음수일 수 없습니다.")
    private int age;

    private PathRequest() {
    }

    public PathRequest(final Long source, final Long target, final int age) {
        this.source = source;
        this.target = target;
        this.age = age;
    }

    public PathServiceRequest toPathServiceRequest() {
        return new PathServiceRequest(source, target, age);
    }

    public Long getSource() {
        return source;
    }

    public void setSource(final Long source) {
        this.source = source;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(final Long target) {
        this.target = target;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }
}
