package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;

import java.util.List;
import java.util.stream.Stream;

public class FakeTrace {
    private final Trace trace;

    private FakeTrace(Trace trace) {
        this.trace = trace;
    }

    public static FakeTrace of(User user) {
        return new FakeTrace(Trace.create(user));
    }

    public FakeTrace withSkillLevel(List<SkillLevel> skillLevels) {
        trace.setSkillLevels(skillLevels);
        skillLevels.forEach(skillLevel -> skillLevel.setTraces(
                Stream.concat(skillLevel.getTraces().stream(), Stream.of(trace)).toList()
        ));
        return this;
    }

    public FakeTrace withAMS(List<AMS> amses) {
        trace.setAmses(amses);
        return this;
    }

    public Trace toModel() {
        return trace;
    }
}
