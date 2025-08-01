package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.opensearch;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillPagedResult;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearch;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.utils.AdditionalSkillConstants;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenSearchIndex implements OpenSearch {
  private final RestHighLevelClient client;

  @Override
  public void cleanAndCreateAdditionalSkillIndex() {
    try {
      if (client
          .indices()
          .exists(new GetIndexRequest(AdditionalSkillConstants.INDEX), RequestOptions.DEFAULT)) {
        client
            .indices()
            .delete(new DeleteIndexRequest(AdditionalSkillConstants.INDEX), RequestOptions.DEFAULT);
        log.info("Index '{}' deleted.", AdditionalSkillConstants.INDEX);
      }

      client
          .indices()
          .create(new CreateIndexRequest(AdditionalSkillConstants.INDEX), RequestOptions.DEFAULT);
      log.info("Index '{}' created.", AdditionalSkillConstants.INDEX);
    } catch (IOException e) {
      throw new RuntimeException("Failed to recreate index: " + AdditionalSkillConstants.INDEX, e);
    }
  }

  @Override
  public void indexAll(List<AdditionalSkill> additionalSkillList) {
    BulkRequest bulkRequest = new BulkRequest();

    for (AdditionalSkill additionalSkill : additionalSkillList) {
      try {
        Map<String, Object> source =
            Map.ofEntries(
                Map.entry(AdditionalSkillConstants.FIELD_ID, additionalSkill.getId().toString()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_SKILL_CODE,
                    additionalSkill.getPathSegments().getSkill().getCode()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_SKILL_LIBELLE,
                    additionalSkill.getPathSegments().getSkill().getLibelle()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_MACRO_SKILL_CODE,
                    additionalSkill.getPathSegments().getMacroSkill().getCode()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_MACRO_SKILL_LIBELLE,
                    additionalSkill.getPathSegments().getMacroSkill().getLibelle()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_TARGET_CODE,
                    additionalSkill.getPathSegments().getTarget().getCode()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_TARGET_LIBELLE,
                    additionalSkill.getPathSegments().getTarget().getLibelle()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_ISSUE_CODE,
                    additionalSkill.getPathSegments().getIssue().getCode()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_ISSUE_LIBELLE,
                    additionalSkill.getPathSegments().getIssue().getLibelle()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_DOMAIN_CODE,
                    additionalSkill.getPathSegments().getDomain().getCode()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_DOMAIN_LIBELLE,
                    additionalSkill.getPathSegments().getDomain().getLibelle()),
                Map.entry(
                    AdditionalSkillConstants.FIELD_TYPE, additionalSkill.getType().getValue()));

        bulkRequest.add(
            new IndexRequest(AdditionalSkillConstants.INDEX)
                .id(additionalSkill.getId().toString())
                .source(source));
      } catch (Exception e) {
        throw new RuntimeException("Failed additionalSkill: " + additionalSkill.getId(), e);
      }
    }

    try {
      BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      if (response.hasFailures()) {
        log.error("Bulk indexing errors: {}", response.buildFailureMessage());
      } else {
        log.info("Bulk indexing succeeded for {} documents", additionalSkillList.size());
      }
    } catch (IOException e) {
      throw new RuntimeException("OpenSearch bulk indexing failed", e);
    }
  }

  @Override
  @Cacheable(
      value = AdditionalSkillConstants.INDEX,
      key = "#keyword + '_' + #pageCriteria.page() + '_' + #pageCriteria.pageSize()")
  public AdditionalSkillPagedResult search(String keyword, PageCriteria pageCriteria) {
    SearchRequest searchRequest = new SearchRequest(AdditionalSkillConstants.INDEX);

    SearchSourceBuilder sourceBuilder =
        new SearchSourceBuilder()
            .query(
                QueryBuilders.matchPhrasePrefixQuery(
                    AdditionalSkillConstants.FIELD_SKILL_LIBELLE, keyword))
            .from(pageCriteria.page() * pageCriteria.pageSize())
            .size(pageCriteria.pageSize())
            .trackTotalHits(true);

    searchRequest.source(sourceBuilder);

    try {
      SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
      long totalHits = response.getHits().getTotalHits().value;

      List<AdditionalSkill> additionalSkillList =
          Arrays.stream(response.getHits().getHits())
              .map(
                  hit -> {
                    Map<String, Object> src = hit.getSourceAsMap();
                    return AdditionalSkill.toDomain(
                        UUID.fromString((String) src.get(AdditionalSkillConstants.FIELD_ID)),
                        PathSegments.toDomain(
                            SegmentDetail.toDomain(
                                (String) src.get(AdditionalSkillConstants.FIELD_SKILL_CODE),
                                (String) src.get(AdditionalSkillConstants.FIELD_SKILL_LIBELLE)),
                            SegmentDetail.toDomain(
                                (String) src.get(AdditionalSkillConstants.FIELD_MACRO_SKILL_CODE),
                                (String)
                                    src.get(AdditionalSkillConstants.FIELD_MACRO_SKILL_LIBELLE)),
                            SegmentDetail.toDomain(
                                (String) src.get(AdditionalSkillConstants.FIELD_TARGET_CODE),
                                (String) src.get(AdditionalSkillConstants.FIELD_TARGET_LIBELLE)),
                            SegmentDetail.toDomain(
                                (String) src.get(AdditionalSkillConstants.FIELD_ISSUE_CODE),
                                (String) src.get(AdditionalSkillConstants.FIELD_ISSUE_LIBELLE)),
                            SegmentDetail.toDomain(
                                (String) src.get(AdditionalSkillConstants.FIELD_DOMAIN_CODE),
                                (String) src.get(AdditionalSkillConstants.FIELD_DOMAIN_LIBELLE))),
                        EAdditionalSkillType.fromValue(
                            (String) src.get(AdditionalSkillConstants.FIELD_TYPE)));
                  })
              .toList();

      return new AdditionalSkillPagedResult(
          additionalSkillList,
          new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), totalHits));
    } catch (IOException e) {
      throw new RuntimeException(
          "OpenSearch search failed with param keyword="
              + keyword
              + ", page="
              + pageCriteria.page()
              + ", size="
              + pageCriteria.pageSize(),
          e);
    }
  }
}
