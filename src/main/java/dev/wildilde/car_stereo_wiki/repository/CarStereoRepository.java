package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CarStereoRepository extends JpaRepository<CarStereo, Long> {
    @Query("""
            select distinct cs
            from CarStereo cs
            left join cs.brands brand
            left join cs.sizes sizeTag
            left join cs.displays displayTag
            left join cs.inputs inputTag
            where (:query = '' or lower(cs.name) like lower(concat('%', :query, '%')))
              and (coalesce(:brands, null) is null or lower(brand.name) in :brands)
              and (coalesce(:years, null) is null or cs.year in :years)
              and (coalesce(:sizes, null) is null or lower(sizeTag.name) in :sizes)
              and (coalesce(:displays, null) is null or lower(displayTag.name) in :displays)
              and (coalesce(:inputs, null) is null or lower(inputTag.name) in :inputs)
            """)
    Page<CarStereo> search(@Param("query") String query,
                           @Param("brands") Collection<String> brands,
                           @Param("years") Collection<Integer> years,
                           @Param("sizes") Collection<String> sizes,
                           @Param("displays") Collection<String> displays,
                           @Param("inputs") Collection<String> inputs,
                           Pageable pageable);

    CarStereo findCarStereoByName(String name);
    
    Page<CarStereo> findAllByOrderByLastModifiedDesc(Pageable pageable);

    @Query("select min(cs.year) from CarStereo cs")
    int findLowestYear();
    @Query("select max(cs.year) from CarStereo cs")
    int findHighestYear();
}
