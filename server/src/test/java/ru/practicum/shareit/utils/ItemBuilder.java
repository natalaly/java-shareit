package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemBuilder {

  public ItemDto buildItemDto(Long id) {
    return ItemDto.builder()
        .id(id)
        .name("Item")
        .description("For Test")
        .available(true)
        .build();
  }

  public ItemDto buildItemDto(String name, String description) {
    return ItemDto.builder()
        .name("Item")
        .description("For Test")
        .available(true)
        .build();
  }

  public  Item buildItem(String name, String description, boolean available, User owner) {
    return Item.builder()
        .name(name)
        .description(description)
        .available(available)
        .owner(owner)
        .build();
  }

  public  Item buildItem() {
    return buildItem("Item",
        "For Test",
        true,
        UserBuilder.buildUser());
  }


}
