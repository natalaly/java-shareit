package ru.practicum.shareit.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class HeaderConstantsTest {

  @Test
  void shouldHaveCorrectUserIdHeaderConstant() {
    assertThat(HeaderConstants.USER_ID_HEADER)
        .isEqualTo("X-Sharer-User-Id");
  }
}