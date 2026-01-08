package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void search() {
        User user = new User(null, "name", "mail@mail.ru");
        userRepository.save(user);

        Item item1 = makeItem("searched_item1", "desc1", true, user);
        Item item2 = makeItem("name", "searched_item2", true, user);
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.search("item");

        assertThat(items.size()).isEqualTo(2);
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }


    private Item makeItem(String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }
}
