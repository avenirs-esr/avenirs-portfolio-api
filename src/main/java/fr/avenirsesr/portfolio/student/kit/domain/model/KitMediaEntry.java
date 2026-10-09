package fr.avenirsesr.portfolio.student.kit.domain.model;

import fr.avenirsesr.portfolio.common.file.domain.model.File;
import java.util.List;

public record KitMediaEntry(File file, List<String> folderPath) {}
