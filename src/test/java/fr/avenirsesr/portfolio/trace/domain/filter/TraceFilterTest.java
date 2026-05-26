package fr.avenirsesr.portfolio.trace.domain.filter;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TraceFilterTest {

  @Test
  void shouldAlwaysContainIsAssociated_evenWhenNull() {
    TraceFilter filter = new TraceFilter(null, null, null, null);

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsKey(ETraceFilterKey.IS_ASSOCIATED);
    assertThat(map.get(ETraceFilterKey.IS_ASSOCIATED)).isNull();
    assertThat(map).hasSize(1);
  }

  @Test
  void shouldNotPutStatusesWhenNull() {
    TraceFilter filter = new TraceFilter(true, null, null, null);

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, true);
    assertThat(map).doesNotContainKey(ETraceFilterKey.STATUS);
  }

  @Test
  void shouldNotPutStatusesWhenEmpty() {
    TraceFilter filter = new TraceFilter(true, null, null, List.of());

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, true);
    assertThat(map).doesNotContainKey(ETraceFilterKey.STATUS);
  }

  @Test
  void shouldPutStatusesWhenNotEmpty() {
    TraceFilter filter =
        new TraceFilter(true, null, null, List.of(ETraceStatus.ASSOCIATED_EVALUATED));

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, true);
    assertThat(map).containsKey(ETraceFilterKey.STATUS);
    assertThat((List<ETraceStatus>) map.get(ETraceFilterKey.STATUS))
        .containsExactly(ETraceStatus.ASSOCIATED_EVALUATED);
  }

  @Test
  void shouldNotPutFileTypesWhenNullOrEmpty() {
    TraceFilter nullList = new TraceFilter(false, null, null, null);
    TraceFilter emptyList = new TraceFilter(false, List.of(), null, null);

    assertThat(nullList.toMap()).doesNotContainKey(ETraceFilterKey.FILE_TYPE);
    assertThat(emptyList.toMap()).doesNotContainKey(ETraceFilterKey.FILE_TYPE);
  }

  @Test
  void shouldPutFileTypesWhenNotEmpty() {
    TraceFilter filter = new TraceFilter(false, List.of(EFileType.PDF, EFileType.PNG), null, null);

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, false);
    assertThat((List<EFileType>) map.get(ETraceFilterKey.FILE_TYPE))
        .containsExactly(EFileType.PDF, EFileType.PNG);
  }

  @Test
  void shouldNotPutSkillIdsWhenNullOrEmpty() {
    TraceFilter nullList = new TraceFilter(false, null, null, null);
    TraceFilter emptyList = new TraceFilter(false, null, List.of(), null);

    assertThat(nullList.toMap()).doesNotContainKey(ETraceFilterKey.SKILL);
    assertThat(emptyList.toMap()).doesNotContainKey(ETraceFilterKey.SKILL);
  }

  @Test
  void shouldPutSkillIdsWhenNotEmpty() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    TraceFilter filter = new TraceFilter(false, null, List.of(id1, id2), null);

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, false);
    assertThat((List<UUID>) map.get(ETraceFilterKey.SKILL)).containsExactly(id1, id2);
  }

  @Test
  void shouldPutAllKeysWhenAllProvided() {
    UUID id = UUID.randomUUID();

    TraceFilter filter =
        new TraceFilter(
            true,
            List.of(EFileType.PDF),
            List.of(id),
            List.of(ETraceStatus.ASSOCIATED_IN_EVALUATION));

    Map<ETraceFilterKey, Object> map = filter.toMap();

    assertThat(map).containsEntry(ETraceFilterKey.IS_ASSOCIATED, true);
    assertThat(map)
        .containsKeys(ETraceFilterKey.FILE_TYPE, ETraceFilterKey.SKILL, ETraceFilterKey.STATUS);
    assertThat(map).hasSize(4);
  }
}
