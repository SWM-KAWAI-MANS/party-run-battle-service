package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GpsDatas {

    List<GpsData> gpsDatas;

    public GpsDatas(List<GpsData> originalGpsDatas) {
        final List<GpsData> gpsDatas = new ArrayList<>(originalGpsDatas);
        validateEmpty(gpsDatas);
        Collections.sort(gpsDatas);

        this.gpsDatas = gpsDatas;
    }

    private void validateEmpty(List<GpsData> gpsDatas) {
        if (Objects.isNull(gpsDatas) || gpsDatas.isEmpty()) {
            // TODO: 2023/07/14 예외 변경
            throw new IllegalArgumentException("gpsDatas is empty");
        }
    }

    public List<Record> createNewRecords() {
        final GpsData firstGpsData = gpsDatas.get(0);
        final Record firstRecord = new Record(firstGpsData, 0);

        return createRecords(firstRecord);
    }

    public List<Record> createRecords(Record record) {
        List<Record> records = new ArrayList<>();
        for (GpsData newGpsData : this.gpsDatas) {
            record = record.createNewRecord(newGpsData);
            records.add(record);
        }

        return records;
    }
}
