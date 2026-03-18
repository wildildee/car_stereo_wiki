package dev.wildilde.car_stereo_wiki.repository;

import dev.wildilde.car_stereo_wiki.entity.CarStereo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarStereoRepository extends PagingAndSortingRepository<CarStereo, Long> {
    @Query("""
            select distinct cs
            from CarStereo cs
            left join cs.brands brand
            left join cs.sizes sizeTag
            left join cs.displays displayTag
            left join cs.inputs inputTag
            where (:query = '' or lower(cs.name) like lower(concat('%', :query, '%')))
              and (:brand = '' or lower(brand.name) = lower(:brand))
              and (:year = '' or cs.year = :yearValue)
              and (:size = '' or lower(sizeTag.name) = lower(:size))
              and (:display = '' or lower(displayTag.name) = lower(:display))
              and (:input = '' or lower(inputTag.name) = lower(:input))
            """)
    Page<CarStereo> search(@Param("query") String query,
                           @Param("brand") String brand,
                           @Param("year") String year,
                           @Param("yearValue") Integer yearValue,
                           @Param("size") String size,
                           @Param("display") String display,
                           @Param("input") String input,
                           Pageable pageable);

    CarStereo findCarStereoByName(String name);
}
