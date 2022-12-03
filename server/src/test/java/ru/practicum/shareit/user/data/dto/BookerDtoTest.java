package ru.practicum.shareit.user.data.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookerDtoTest {

    @Autowired
    private JacksonTester<BookerDto> json;

    @Test
    void testSerialize() throws Exception {
        BookerDto bookerDto = new BookerDto(1L);
        var result = json.write(bookerDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookerDto.getId().intValue());
    }
}