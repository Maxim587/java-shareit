package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemDataDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemDataDto itemDataDto = new ItemDataDto();
        itemDataDto.setItemId(1L);
        itemDataDto.setName("name");
        itemDataDto.setOwnerId(6L);
        List<ItemDataDto> itemDataDtos = List.of(itemDataDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("description");
        requestDto.setRequestorId(2L);
        requestDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        requestDto.setItems(itemDataDtos);

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(requestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
    }

}
