package fr.avenirsesr.portfolio.student.progress.imported.domain.port.input;

import java.util.UUID;

public interface ActivityProgressService {
    void subscribe(UUID activityId);
}
