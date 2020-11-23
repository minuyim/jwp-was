package slipp.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {
    @DisplayName("등록된 패스워드를 입력하면 인증이 성공한다.")
    @Test
    void authenticate() {
        User user = new User("id", "password", "name", "id@email.com");
        assertThat(user.authenticate(user.getPassword())).isTrue();
    }
}