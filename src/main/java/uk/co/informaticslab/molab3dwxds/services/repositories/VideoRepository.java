package uk.co.informaticslab.molab3dwxds.services.repositories;

import com.mysema.query.types.Predicate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import uk.co.informaticslab.molab3dwxds.domain.Phenomenon;
import uk.co.informaticslab.molab3dwxds.domain.Video;

/**
 * MongoDB video repository
 * <p>
 * Makes use of 'spring-data query creation from method names' hence the terrible method names.
 */
public interface VideoRepository extends MongoRepository<Video, String>, QueryDslPredicateExecutor<Video> {

    @Query(value = "{}", fields = "{ 'data' : 0 }")
    Iterable<Video> findAllVideoMeta(Predicate predicate);

    @Query(value = "{}", fields = "{ 'data' : 0 }")
    Video findVideoMetaById(String id);

    @Query(value = "{}", fields = "{ 'data' : 0 }")
    Video findFirstVideoMetaByOrderByModelRunDTDesc();

    @Query(value = "{}", fields = "{ 'data' : 0 }")
    Video findFirstVideoMetaByPhenomenonOrderByModelRunDTDesc(Phenomenon phenomenon);

}