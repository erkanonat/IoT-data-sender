package com.iot.tb.datasender.repository;

import com.iot.tb.datasender.entity.AnomallyTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface AnomallyTrafficDataRepository extends CassandraRepository<AnomallyTrafficData> {

    @Query("SELECT * FROM traffickeyspace.anomally_traffic_data WHERE recorddate = ?0 ALLOW FILTERING")
    Iterable<AnomallyTrafficData> findAnomalyByDate(String date);

}
