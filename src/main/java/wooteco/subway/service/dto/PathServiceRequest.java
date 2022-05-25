package wooteco.subway.service.dto;

public class PathServiceRequest {

    private final Long source;
    private final Long target;
    private final int age;

    public PathServiceRequest(final Long source, final Long target, final int age) {
        this.source = source;
        this.target = target;
        this.age = age;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public int getAge() {
        return age;
    }
}
