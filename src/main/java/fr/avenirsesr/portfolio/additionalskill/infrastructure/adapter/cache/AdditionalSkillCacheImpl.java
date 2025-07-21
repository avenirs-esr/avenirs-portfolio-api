package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillCacheImpl implements AdditionalSkillCache {
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public AdditionalSkillsPaginated findAll(Integer page, Integer pageSize) {
    List<AdditionalSkill> additionalSkills = loadAdditionalSkills();
    return paginate(additionalSkills, page, pageSize);
  }

  private List<AdditionalSkill> loadAdditionalSkills() {
    try (InputStream is = getClass().getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});
      return entities.stream().map(AdditionalSkillMapper::toDomain).toList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }

  private AdditionalSkillsPaginated paginate(List<AdditionalSkill> skills, int page, int pageSize) {
    int totalElements = skills.size();
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);
    int start = Math.min(page * pageSize, totalElements);
    int end = Math.min(start + pageSize, totalElements);
    List<AdditionalSkill> paginatedSkills = skills.subList(start, end);
    PageInfo pageInfo = new PageInfo(pageSize, totalElements, totalPages, page);
    return new AdditionalSkillsPaginated(paginatedSkills, pageInfo);
  }
}
